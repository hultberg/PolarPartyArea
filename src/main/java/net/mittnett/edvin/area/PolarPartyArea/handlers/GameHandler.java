package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

public class GameHandler {

	private PolarPartyArea plugin;
	private ConfigurationHandler config;
	private MySQL mysqlc;
	private Connection mysql;
	
	private PreparedStatement insertGame;
	
	/* States */
	private boolean finished = false;
	private boolean starting = false;
	private boolean ongoingBattle = false;
	
	/* Maps */
	private HashMap<String, Player> players = new HashMap<String, Player>();
	
	public GameHandler(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.config = instance.getConfigHandler();
		this.mysqlc = instance.getMysqlconnection();
		this.mysql = this.mysqlc.mysqlConn;
	}
	
	public void prepare()
	{
		try {
			this.insertGame = this.mysql.prepareStatement("INSERT INTO `games`('game_world', 'game_winner', 'game_started', 'game_ended')VALUES(?,?,?,?)");
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
	
}
