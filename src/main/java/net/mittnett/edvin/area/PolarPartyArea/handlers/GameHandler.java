package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGameStartedEvent;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class GameHandler {

	private PolarPartyArea plugin;
	private MySQL mysqlc;
	private Connection mysql;
	private UserHandler userHandler;
	
	private PreparedStatement insertGame;
	
	/* States */
	private boolean finished = false;
	private boolean starting = false;
	private boolean ongoingBattle = false;
	private boolean allowCompo = false;
	
	/* Vars */
	private int startedGame;
	
	/* Maps */
	private HashMap<String, Player> players = new HashMap<String, Player>();
	private HashMap<String, Player> ignoreplayers = new HashMap<String, Player>();
	private HashMap<String, Integer> points = new HashMap<String, Integer>();
	
	public static int counted = 20;
	public static int countedTask = 0;
	
	public GameHandler(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.mysqlc = instance.getMysqlconnection();
		this.mysql = this.mysqlc.mysqlConn;
		this.userHandler = instance.getUserHandler();
	}
	
	public void prepare()
	{
		try {
			this.insertGame = this.mysql.prepareStatement("INSERT INTO `games`(`game_world`, `game_winner`, `game_started`, `game_ended`)VALUES(?,?,?,?)");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void cleanup()
	{
		try {
			this.insertGame.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Check if game is finished by how many players left.
	 * Call AFTER removing a player.
	 */
	public void checkIfGameFinished()
	{
		// Check players on the server.
		int players = 0;
		
		// Loop players to check up with maps
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (this.getPlayer(p.getName()) != null && this.isIgnored(p.getName()) != true) {
				players++;
			}
		}
		
		if (players <= 1) {
			// If under 1 (One player left)
			this.finishGame(true);
			return;
		}
	}
	
	/**
	 * Will reset all variables and stop current game.
	 * Server will be set to "waiting mode"
	 * 
	 * Please don't call this function on its own, use checkIfGameFinished().
	 * 
	 * @see checkIfGameFinished()
	 * @param updateDb boolean Insert a record about this game in database?
	 */
	public void finishGame(boolean updateDb)
	{
		if (updateDb) {
			HashMap<String, Player> players = this.getPlayersMapRaw();
			if (players.size() == 1) {
				Player winner = null;
				for (String p : this.getPlayersMapRaw().keySet()) {
					winner = this.getPlayer(p);
					if (winner == null) {
						System.out.println("ERROR... Winner is null.");
						return;
					}
				}
			
				// Insert winner to database.
				this.addGameDatabase("world_pp_", this.userHandler.getUserId(winner.getName()), winner.getName(), this.getStartedGame());

				// Broadcast winner.
				Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
							+ ChatColor.RED + winner.getName() + ChatColor.GOLD + " vant matchen!");
				
				// Teleport winner to bed location
				winner.teleport(this.plugin.worldconfig.getSpectateSpawnPoint());
			} else {
				System.out.println("ERROR... More players left!!!");
				return;
			}
		}		
		
		plugin.resetWorldSettings();
		
		this.setFinished(true);
		this.setOngoingGame(false);
		this.setStarting(false);
		this.setStartedGame(0);
		this.players.clear();
		this.ignoreplayers.clear();
	}
	
	/**
	 * Record a game to the database.
	 * 
	 * @param world
	 * @param winnerID
	 * @param winner
	 * @param started
	 * @return
	 */
	public boolean addGameDatabase(String world, int winnerID, String winner, int started)
	{
		try {
			this.insertGame.setString(1, world);
			this.insertGame.setString(2, winnerID + "|" + winner);
			this.insertGame.setInt(3, started);
			this.insertGame.setInt(4, (int) (System.currentTimeMillis() / 1000));
			this.insertGame.executeUpdate();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Ignore a player for arena system.
	 * @param p
	 */
	public void ignorePlayer(Player p) {
		this.ignoreplayers.put(p.getName(), p);
	}
	
	/**
	 * Returns if p is ignored.
	 * @param p
	 * @return
	 */
	public boolean isIgnored(String p) {
		return this.ignoreplayers.get(p) != null;
	}
	
	public void removedIgnoredPlayer(String p) {
		this.ignoreplayers.remove(p);
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
	
	public HashMap<String, Player> getPlayersMapRaw()
	{
		return this.players;
	}
	
	public void addPlayer(Player pl)
	{
		this.players.put(pl.getName(), pl);
		this.points.put(pl.getName(), 0);
	}
	
	public void removePlayer(String pl)
	{
		this.players.remove(pl);
		this.points.remove(pl);
		
		if (this.isIgnored(pl))
			this.removedIgnoredPlayer(pl);
	}
	
	public Player getPlayer(String pl)
	{
		return this.players.get(pl);
	}
	
	public void addPoints(String pl, int points)
	{
		int has = 0;
		if (this.points.get(pl) != null) {
			has = this.points.get(pl);
		}
		
		this.points.put(pl, (has + points));
	}
	
	/**
	 * 
	 * @param var1 boolean
	 */
	public void setStarting(boolean var1)
	{
		this.starting = var1;
	}
	
	/**
	 * Get timestamp of when game started.
	 * 
	 * @return int the timestamp
	 */
	public int getStartedGame() {
		return startedGame;
	}

	public void setStartedGame(int startedGame) {
		this.startedGame = startedGame;
	}

	/**
	 * Starts a new game
	 * @param delay
	 */
	public void start(int delay)
	{		
		if (this.hasOngoingGame()) {
			finishGame(true);
		}
		
		this.setStarting(true);
		
		this.setSpectatePointOnPlayers();
		
		// Schedule task for sec remaning until start
		countedTask = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			public void run()
			{
				if (counted == 0) {
					Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> " + ChatColor.GOLD + "Spillet er igang!");
					Bukkit.getScheduler().cancelTask(countedTask);
					
					// Start game here.
					GameHandler.startGame();
					
					return;
				}
				
				Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> " + ChatColor.GOLD + "Spillet starter om " + counted + " sekund"
						+ (counted == 1 ? "" : "er") + ", gjør deg klar!");
				
				counted--;
			}
		}, 20L, 20L);
	}
	
	public int getPlayerThatCanMatch()
	{
		int players = 0;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (this.getPlayer(p.getName()) != null && this.isIgnored(p.getName()) != true) {
				players++;
			}
		}
		
		return players;
	}
	
	/**
	 * Static method for starting a game.
	 */
	public static void startGame()
	{
		GameHandler game = PolarPartyArea.ref.getGameHandler();
		game.setStarting(false);
		game.setOngoingGame(true);
		game.setFinished(false);
		game.setStartedGame((int) (System.currentTimeMillis() / 1000));
		
		// Fire event.
		Bukkit.getPluginManager().callEvent(new PpGameStartedEvent());
	}
	
	/**
	 * Stop game, calls finishGame(false)
	 * 
	 * @see finishGame()
	 */
	public void stop()
	{
		this.finishGame(false);
	}

	/**
	 * If true, allow everyone to join. If false, only admins and mods.
	 * @return
	 */
	public boolean getServerAllowCompo() {
		return allowCompo;
	}

	public void setAllowCompo(boolean allowCompo) {
		this.allowCompo = allowCompo;
	}
	
	public void setSpectatePointOnPlayers()
	{
		Collection<? extends Player> pls = Bukkit.getOnlinePlayers();
		if (pls.size() > 0) {
			for (Player p : pls) {
				if (!this.isIgnored(p.getName()))
					p.setBedSpawnLocation(this.plugin.getWorldConfigHandler().getSpectateSpawnPoint(), true);
			}
		}
	}
	
}
