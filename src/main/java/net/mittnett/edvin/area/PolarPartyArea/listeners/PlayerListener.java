package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;
import net.mittnett.edvin.area.PolarPartyArea.handlers.PlayerData;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

	private PolarPartyArea plugin;
	private UserHandler userHandler;
	private LogHandler log;
	private ConfigurationHandler config;
	private GameHandler gameHandler;

	public PlayerListener(PolarPartyArea instance) {
		this.plugin = instance;
		this.userHandler = instance.getUserHandler();
		this.log = instance.getLogHandler();
		this.config = instance.getConfigHandler();
		this.gameHandler = instance.getGameHandler();
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		
		PlayerData pd = this.userHandler.getPlayerData(p.getUniqueId().toString());
		if (pd.isBanned()) {
			event.disallow(Result.KICK_BANNED, "You have been banned from the server, no reason has been given.");
			return;
		}
		
		/* if ongoing game, and player is not in it... temp ban. */
		if (gameHandler.hasOngoingGame() && gameHandler.getPlayer(p.getName()) == null) {
			event.disallow(Result.KICK_BANNED, "En runde pågår og du er ikke med i den, vennligst logg inn senere.");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		/* Send players to temp map */
		p.teleport(Bukkit.getWorld(config.getTempMap()).getSpawnLocation());
		
		this.userHandler.loginUser(p);
		
		p.sendMessage(ChatColor.GOLD + "------------- " + ChatColor.DARK_AQUA + "POLARPARTY 23 " + ChatColor.GOLD + "-------------");
		p.sendMessage("Informasjon om compoen finner du på www.polarparty.no/pp23/");
		p.sendMessage("");
		
		this.log.log(this.userHandler.getUserId(p.getName()), null, 0, null, 0, 0, p.getAddress().getAddress().getHostAddress(), LogType.JOIN);
		event.setJoinMessage(this.userHandler.getPrefix(p.getName()) + ChatColor.GREEN + " logget på.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		
		Broadcaster.broadcastAll(this.userHandler.getPrefix(p.getName()) + ChatColor.RED + " logget av.");
		
		this.userHandler.logout(p);
		gameHandler.removePlayer(p.getName());
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		event.setCancelled(true);
		
		String msg = event.getMessage();
		this.log.log(this.userHandler.getUserId(p.getName()), p.getName(), 0, null, 0, 0, msg, LogType.CHAT);
		Broadcaster.broadcastChat(this.userHandler.getPrefix(p.getName()), msg);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		
	}
}
