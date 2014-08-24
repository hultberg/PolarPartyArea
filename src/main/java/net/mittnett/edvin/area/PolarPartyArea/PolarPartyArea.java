package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import net.mittnett.edvin.area.PolarPartyArea.commands.ReloadConfigCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.ModCommand;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;
import net.mittnett.edvin.area.PolarPartyArea.listeners.BlockListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.EntityListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.PlayerListener;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class PolarPartyArea extends JavaPlugin {

	public static PolarPartyArea ref;
	
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
	
	/* Handlers */
	private UserHandler userHandler;
	private LogHandler logHandler;
	private Broadcaster broadcaster;
	
	/* States */
	private boolean finished = false;
	private boolean starting = false;
	private boolean ongoingBattle = false;
	
	/* Maps */
	private HashMap<String, Player> players = new HashMap<String, Player>();
	
	/* Listeners */
	private PlayerListener playerListener;
	private BlockListener blockListener;
	private EntityListener entityListener;
	
	@Override
	public void onEnable() {
		ref = this;
		this.getDataFolder().mkdirs();
		
		log = getLogger();		
		log.info("PolarPartyPlugin starting up...");
		
		/* Create config */
		config = new ConfigurationHandler(this);		
		config.loadConfig();
		
		/* Connect mysql */
		log.info("Connecting to mysql...");
		this.mysqlconnection = new MySQL(this);
		if (!this.mysqlconnection.connectFriendly()) {
			log.severe("- CONNECTION TO MYSQL FAILED! -");						
			System.exit(0);
		}
		log.info("- Success!");
		
		/* Create handlers */
		userHandler = new UserHandler(this);
		logHandler = new LogHandler(this);
		broadcaster = new Broadcaster(this);
		
		/* Enable handlers */
		userHandler.prepare();
		logHandler.prepare();
		broadcaster.prepare();
		
		/* Create listeners */
		playerListener = new PlayerListener(this);
		blockListener = new BlockListener(this);
		entityListener = new EntityListener(this);
		
		/* Enable listeners */
		Bukkit.getPluginManager().registerEvents(playerListener, this);
		Bukkit.getPluginManager().registerEvents(blockListener, this);
		Bukkit.getPluginManager().registerEvents(entityListener, this);
		
		/* Enable commands */
		enableCommands();
		
		/* Copy orgmap */
		this.refreshWorld();
	}
	
	@Override
	public void onDisable() {
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
	}
	
	public void refreshWorld()
	{
		// First, get orgmap
		World w = Bukkit.getWorld(this.config.getOrgMap());
		if (w == null) {
			PolarPartyArea.log.severe("MapRefreshing feature is disabled because map by the name: '" + this.config.getOrgMap() + "' was not found.");
			return;
		}
		
		WorldCreator wc = new WorldCreator(this.config.getTempMap());
		wc.copy(w);
		wc.createWorld();
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
	
	/**
	 * If an game is on-going.
	 * @return boolean
	 */
	public boolean hasOngoingGame()
	{
		return this.ongoingBattle;
	}
	
	/**
	 * Returns if game is starting...
	 * @return boolean
	 */
	public boolean isStarting()
	{
		return this.starting;
	}
	
	/**
	 * Returns if game has finished. This will be true while server is allowing
	 * people to join.
	 * 
	 * @return boolean
	 */
	public boolean isFinished()
	{
		return this.finished;
	}
	
	/**
	 * Set ongoing
	 * @param var1 boolean
	 */
	public void setOngoingGame(boolean var1)
	{
		this.ongoingBattle = var1;
	}
	
	/**
	 * 
	 * @param var1 boolean
	 */
	public void setFinished(boolean var1)
	{
		this.finished = var1;
	}
	
	public ConfigurationHandler getConfigHandler()
	{
		return this.config;
	}
	
	public HashMap<String, Player> getPlayersMapRaw()
	{
		return this.players;
	}
	
	public void addPlayer(Player pl)
	{
		this.players.put(pl.getName(), pl);
	}
	
	public void removePlayer(String pl)
	{
		this.players.remove(pl);
	}
	
	public Player getPlayer(String pl)
	{
		return this.players.get(pl);
	}
	
	/**
	 * 
	 * @param var1 boolean
	 */
	public void setStarting(boolean var1)
	{
		this.starting = var1;
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
	
}
