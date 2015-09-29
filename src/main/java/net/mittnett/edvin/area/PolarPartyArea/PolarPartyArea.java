package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;

import net.mittnett.edvin.area.PolarPartyArea.commands.BanCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.CompoCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.GameCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.KickCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.ListBansCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.MCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.ModCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.RCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.ReloadConfigCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.SpawnCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.TpAllCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.UnbanCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.WhoCommand;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.WorldEditBridge;
import net.mittnett.edvin.area.PolarPartyArea.listeners.BlockListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.EntityListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.GameListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.PlayerListener;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class PolarPartyArea extends JavaPlugin {

	public static PolarPartyArea ref;
	public static boolean anyoneHasDiamond;
	
	/* Admin ranks */
	public static int ADMIN_RANK = 10;
	public static ChatColor ADMIN_COLOUR = ChatColor.GOLD;
	
	/* Mod rank */
	public static int MOD_RANK = 5;
	public static ChatColor MOD_COLOUR = ChatColor.BLUE;
	
	/* Builder rank */
	public static int BUILDER_RANK = 1;
	public static ChatColor BUILDER_COLOUR = ChatColor.WHITE;
	
	/* Guest rank */
	public static int GUEST_RANK = 0;
	public static ChatColor GUEST_COLOUR = ChatColor.GRAY;
	
	/* Logger */
	public static Logger log;
	
	/* SQL */
	public MySQL mysqlconnection;
	
	/* Configuration */
	public ConfigurationHandler config;
	public WorldConfigurationHandler worldconfig;
	
	/* Handlers */
	private WorldEditBridge webridge;
	private UserHandler userHandler;
	private LogHandler logHandler;
	private Broadcaster broadcaster;
	private GameHandler gameHandler;
	
	/* Listeners */
	private GameListener gameListener;
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private EntityListener entityListener;
	
	public void onEnable() {
		ref = this;
		this.getDataFolder().mkdirs();
		
		log = getLogger();		
		log.info("PolarPartyPlugin starting up...");
		
		/* Create config */
		config = new ConfigurationHandler(this);		
		config.loadConfig();
		
		/* Copy orgmap */
		this.refreshWorld();
		
		/* Connect mysql */
		log.info("Connecting to mysql...");
		this.mysqlconnection = new MySQL(this);
		if (!this.mysqlconnection.connectFriendly()) {
			log.severe("- CONNECTION TO MYSQL FAILED! -");						
			System.exit(0);
		}
		log.info("- Success!");
		
		/* Create handlers */
		webridge = new WorldEditBridge(this);
		userHandler = new UserHandler(this);
		logHandler = new LogHandler(this);
		broadcaster = new Broadcaster(this);
		gameHandler = new GameHandler(this);
		
		/* Enable handlers */
		if (!webridge.loadWe()) {
			log.severe("WorldEdit required functions is disabled.");
		}
		userHandler.prepare();
		logHandler.prepare();
		broadcaster.prepare();
		gameHandler.prepare();
		
		/* Create listeners */
		gameListener = new GameListener(this);
		playerListener = new PlayerListener(this);
		blockListener = new BlockListener(this);
		entityListener = new EntityListener(this);
		
		/* Enable listeners */
		Bukkit.getPluginManager().registerEvents(gameListener, this);
		Bukkit.getPluginManager().registerEvents(playerListener, this);
		Bukkit.getPluginManager().registerEvents(blockListener, this);
		Bukkit.getPluginManager().registerEvents(entityListener, this);
		
		/* Enable commands */
		enableCommands();
	}
	
	public void onDisable() {
		gameHandler.cleanup();
		broadcaster.cleanup();
		logHandler.cleanup();
		userHandler.cleanup();
		
		/* Close mysql connection */
		try {
			this.mysqlconnection.mysqlConn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		Bukkit.getScheduler().cancelAllTasks();
	}
	
	public void enableCommands() {
		getCommand("mod").setExecutor(new ModCommand(this));
		getCommand("reloadConfig").setExecutor(new ReloadConfigCommand(this));
		getCommand("game").setExecutor(new GameCommand(this));
		getCommand("compo").setExecutor(new CompoCommand(this));
		getCommand("who").setExecutor(new WhoCommand(this));
		getCommand("msg").setExecutor(new MCommand(this));
		getCommand("reply").setExecutor(new RCommand(this));
		getCommand("who").setExecutor(new WhoCommand(this));
		getCommand("kick").setExecutor(new KickCommand(this));
		getCommand("listbans").setExecutor(new ListBansCommand(this));
		getCommand("ban").setExecutor(new BanCommand(this));
		getCommand("unban").setExecutor(new UnbanCommand(this));
		getCommand("tpall").setExecutor(new TpAllCommand(this));
		getCommand("spawn").setExecutor(new SpawnCommand(this));
	}
	
	public void refreshWorld()
	{		
		String usingCopy = "1";
		try {
			usingCopy = (String) PolarPartyArea.load(new File(getDataFolder(), "lastUsed.bin"));
		} catch (Exception e) {
			usingCopy = "1";
		}
		
		int using = 1;
		try {
			using = Integer.parseInt(usingCopy);
		} catch (NumberFormatException e) {
			using = 1;
		}
		
		if (using > 3)
			using = 1;
		
		log.info("Using world number: " + using);
		
		// First, get orgmap
		World w = Bukkit.getWorld("world_pp_" + using);
		if (w == null) {
			WorldCreator w2 = new WorldCreator("world_pp_" + using);
			w = w2.createWorld();
		}
		
		// Save just used map.
		try {
			save(new File(getDataFolder(), "lastUsed.bin"), "" + (using + 1) + "");
		} catch (Exception e) {
			log.severe("Can't save last used map");
		}
		
		// The world to copy
		File sourceFolder = w.getWorldFolder();
		 
		// The world to overwrite when copying		
		File targetFolder = new File("world_temp");		
		this.copyWorld(sourceFolder, targetFolder);
		
		// Load world_temp
		WorldCreator temp = new WorldCreator("world_temp");
		temp.createWorld();
		
		/* Create worldconfig */
		worldconfig = new WorldConfigurationHandler(this, w);
		worldconfig.loadConfig();
		
		/* Set default world settings */
		this.resetWorldSettings();
	}
	
	public void resetWorldSettings()
	{
		World w = this.worldconfig.getWorld();
		w.setDifficulty(Difficulty.NORMAL);
		w.setPVP(true);
	}

	public Logger getLog() {
		return log;
	}

	public MySQL getMysqlconnection() {
		return mysqlconnection;
	}
	
	public UserHandler getUserHandler() {
		return userHandler;
	}

	public LogHandler getLogHandler() {
		return logHandler;
	}

	public Broadcaster getBroadcaster() {
		return broadcaster;
	}
	
	public ConfigurationHandler getConfigHandler()
	{
		return this.config;
	}
	
	public WorldConfigurationHandler getWorldConfigHandler()
	{
		return this.worldconfig;
	}
	
	public GameHandler getGameHandler()
	{
		return this.gameHandler;
	}
	
	/**
	 * Get WorldEditPlugin class.
	 * @return null if WorldEdit hasn't been found.
	 */
	public WorldEditPlugin getWorldEdit()
	{
		if (this.webridge.isEnabled())
			return this.webridge.getWorldEdit();
		
		return null;
	}

	/* Save and load functions */
	public static void save(File binFile, Object obj) throws Exception {		
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(binFile));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	
	public static Object load(File binFile) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(binFile));
		Object result = ois.readObject();
		ois.close();
		return result;
	}
	
	public void copyWorld(File source, File target) {
		try {
			ArrayList<String> ignore = new ArrayList<String>(Arrays.asList(
					"uid.dat", "session.dat"));
			if (!ignore.contains(source.getName())) {
				if (source.isDirectory()) {
					if (!target.exists())
						target.mkdirs();
					String files[] = source.list();
					for (String file : files) {
						File srcFile = new File(source, file);
						File destFile = new File(target, file);
						copyWorld(srcFile, destFile);
					}
				} else {
					InputStream in = new FileInputStream(source);
					OutputStream out = new FileOutputStream(target);
					byte[] buffer = new byte[1024];
					int length;
					while ((length = in.read(buffer)) > 0)
						out.write(buffer, 0, length);
					in.close();
					out.close();
				}
			}
		} catch (IOException e) {

		}
	}
	
	/**
	 * A method that will set the player as a spectator and teleports the player to the spectator area
	 * Will also clear inventory and set health
	 */
	public void setPlayerSpectator(Player player)
	{
		player.setGameMode(GameMode.ADVENTURE);
		player.getInventory().clear();
	}

}
