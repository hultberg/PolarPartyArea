package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.util.logging.Logger;

import net.mittnett.edvin.area.PolarPartyArea.commands.GroupChatCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.GroupCommand;
import net.mittnett.edvin.area.PolarPartyArea.commands.ModCommand;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GroupHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;
import net.mittnett.edvin.area.PolarPartyArea.listeners.BlockListener;
import net.mittnett.edvin.area.PolarPartyArea.listeners.PlayerListener;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	private GroupHandler groupHandler;
	private LogHandler logHandler;
	private Broadcaster broadcaster;
	
	/* States */
	private boolean finished = false;
	private boolean starting = false;
	private boolean ongoingBattle = false;
	
	/* Listeners */
	private PlayerListener playerListener;
	private BlockListener blockListener;
	
	@Override
	public void onEnable() {
		ref = this;
		this.getDataFolder().mkdirs();
		
		log = getLogger();
		
		log.info("PolarPartyPlugin starting up...");
		
		config.loadConfig();
		
		/* Connect mysql */
		log.info("Connecting to mysql...");
		mysqlconnection = new MySQL();
		mysqlconnection.mysqlConn = mysqlconnection.connect("localhost", "minecraft", "1234", "minecraft");
		if (mysqlconnection.mysqlConn == null) {
			log.severe("- CONNECTION TO MYSQL FAILED! -");						
			System.exit(0);
		}
		log.info("- Success!");
		
		/* Create handlers */
		userHandler = new UserHandler(this);
		groupHandler = new GroupHandler(this);
		logHandler = new LogHandler(this);
		broadcaster = new Broadcaster(this);
		
		/* Enable handlers */
		userHandler.prepare();
		groupHandler.prepare();
		logHandler.prepare();
		broadcaster.prepare();
		
		/* Create listeners */
		playerListener = new PlayerListener(this);
		blockListener = new BlockListener(this);
		
		/* Enable listeners */
		Bukkit.getPluginManager().registerEvents(playerListener, this);
		Bukkit.getPluginManager().registerEvents(blockListener, this);
		
		/* Enable commands */
		enableCommands();
	}
	
	@Override
	public void onDisable() {
		broadcaster.cleanup();
		logHandler.cleanup();
		groupHandler.cleanup();
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
		getCommand("gr").setExecutor(new GroupCommand(this));
		getCommand("g").setExecutor(new GroupChatCommand(this));
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

	public GroupHandler getGroupHandler() {
		return groupHandler;
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
