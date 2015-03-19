package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.WorldConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerKilledEvent;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerLeaveEvent;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanDataCollection;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

	private PolarPartyArea plugin;
	private UserHandler userHandler;
	private LogHandler log;
	private ConfigurationHandler config;
	private GameHandler gameHandler;
	private WorldConfigurationHandler worldconfig;

	public PlayerListener(PolarPartyArea instance) {
		this.plugin = instance;
		this.userHandler = instance.getUserHandler();
		this.log = instance.getLogHandler();
		this.config = instance.getConfigHandler();
		this.worldconfig = instance.getWorldConfigHandler();
		this.gameHandler = instance.getGameHandler();
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		
		BanDataCollection bdc = this.userHandler.getBanData(p.getUniqueId());
		if (bdc.hasBan()) {
			if (bdc.hasArenaServerBan()) {
				event.disallow(Result.KICK_BANNED, "BANNET: " + bdc.getReason(UserHandler.SERVER_ID) + " | Kontakt game-crew for klager/spm.");
				return;				
			} else if (bdc.hasGlobalBan()) {
				event.disallow(Result.KICK_BANNED, "GLOBAL-BAN: " + bdc.getGlobalBanReason() + " | Kontakt game-crew for klager/spm.");
				return;
			}
		}
		
		// Allow compo?
		if (this.gameHandler.getServerAllowCompo() == false) {
			if (p.isWhitelisted() == false) {
				event.disallow(Result.KICK_WHITELIST, "Servern er ikke åpen, kun whitelista personer kan joine.");
			}
		}
		
		/* if ongoing game, and player is not in it... temp ban. */
		if ((this.gameHandler.isStarting() || gameHandler.hasOngoingGame()) && gameHandler.getPlayer(p.getName()) == null) {
			event.disallow(Result.KICK_BANNED, "En runde pågår og du er ikke med i den, vennligst logg inn senere.");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		this.userHandler.loginUser(p);

		p.sendMessage(ChatColor.GOLD + "---------- " + ChatColor.DARK_AQUA + "GlobeLAN 24 " + ChatColor.GOLD + "----------");
		p.sendMessage("Gå til Gamedesk for hjelp eller spørsmål");
		p.sendMessage("");
		
		if (!this.gameHandler.hasOngoingGame()) {
			p.setGameMode(GameMode.ADVENTURE);	
			p.getInventory().clear();
			p.setHealth(20.0);
			p.setFoodLevel(20);
		} else {
			p.setGameMode(GameMode.SURVIVAL);
		}
		
		p.teleport(Bukkit.getWorld("world_temp").getSpawnLocation());
		
		this.log.log(this.userHandler.getUserId(p.getName()), null, 0, null, 0, 0, p.getAddress().getAddress().getHostAddress(), LogType.JOIN);
		event.setJoinMessage(this.userHandler.getPrefix(p.getName()) + ChatColor.GREEN + " logget på.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		
		this.log.log(this.userHandler.getUserId(p.getName()), null, 0, null, 0, 0, p.getAddress().getAddress().getHostAddress(), LogType.QUIT);
		
		// Call game event if ongoing
		if (this.gameHandler.hasOngoingGame())
			Bukkit.getPluginManager().callEvent(new PpGamePlayerLeaveEvent(p, false));
		else 		
			Broadcaster.broadcastAll(this.userHandler.getPrefix(p.getName()) + ChatColor.RED + " logget av.");
		
		this.userHandler.logout(p);
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Bukkit.getPluginManager().callEvent(new PpGamePlayerLeaveEvent(event.getPlayer(), true));
		event.setLeaveMessage(null);
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
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		event.setDeathMessage(null);
		
		if (this.gameHandler.hasOngoingGame()) {
			if (event.getEntityType() == EntityType.PLAYER) {
				Player p = event.getEntity();
				Bukkit.getPluginManager().callEvent(new PpGamePlayerKilledEvent(p, p.getKiller()));
			}
		}		
	}
}
