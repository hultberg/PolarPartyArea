package net.mittnett.edvin.area.PolarPartyArea.listeners;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.WorldConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerKilledEvent;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerLeaveEvent;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGameStartedEvent;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

public class GameListener implements Listener {
	
	private GameHandler gameHandler;
	private WorldConfigurationHandler worldconfig;
	private LogHandler logHandler;
	private UserHandler userHandler;

	public GameListener(PolarPartyArea instance)
	{
		this.gameHandler = instance.getGameHandler();
		this.worldconfig = instance.getWorldConfigHandler();
		this.logHandler = instance.getLogHandler();
		this.userHandler = instance.getUserHandler();
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		
		String str = ChatColor.GREEN + "Join for å bli med!";		
		if (this.gameHandler.hasOngoingGame() | this.gameHandler.isFinished()) {
			str = ChatColor.YELLOW + "Match pågår...";
		} else if (!this.gameHandler.getServerAllowCompo()) {
			str = ChatColor.RED + "Server stengt";			
		}
		
		event.setMotd(str + ChatColor.RESET + " - PolarParty 24 Arena");
		try {
			event.setServerIcon(Bukkit.loadServerIcon(new File("server.png")));
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (UnsupportedOperationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler
	public void onGameStarted(PpGameStartedEvent event)
	{		
		
		World w = this.worldconfig.getWorld();
		w.setDifficulty(Difficulty.NORMAL);
		w.setPVP(true);
		
		w.setTime(0);
		
		// Teleport everybody
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (this.gameHandler.isIgnored(p.getName()) != true) {
				p.teleport(Bukkit.getWorld("world_temp").getSpawnLocation());
				p.getInventory().clear();
				p.setGameMode(GameMode.SURVIVAL);
				p.setHealth(20.0);
				p.setFoodLevel(20);
				this.gameHandler.addPlayer(p);
			}			
		}
	}
	
	@EventHandler
	/**
	 * Event fired when a player is killed.
	 * @param event
	 */
	public void onGamePlayerKilled(PpGamePlayerKilledEvent event)
	{
		// When a player is killed, kick him/her out of server and log
		// it. Also check how many players are left.
		// Update mysql server, which a website is listening to.
		Player killed = event.getKilledPlayer();
		Entity killer = event.getKiller();
		Player killerr = null;
		
		this.gameHandler.removePlayer(killed.getName());
		
		boolean boolKilled = false;
		if (killer instanceof Player) {
			boolKilled = true;
			killerr = (Player) killer;
			
			// Kick killed
			killed.sendMessage(ChatColor.RED + "Du ble drept av " + ChatColor.WHITE.toString() + killerr.getName() + ChatColor.RED + " og du er derfor ute av runden.");
			
			// Give killer one point.
			this.gameHandler.addPoints(killerr.getName(), 1);
			
			// Broadcast death.
			Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
						+ ChatColor.RED + killed.getName() + ChatColor.GOLD + " ble drept av "
						+ ChatColor.RED + killerr.getName() + ChatColor.GOLD + "!");		
			this.logHandler.log(this.userHandler.getUserId(killerr.getName()), "", this.userHandler.getUserId(killed.getName()), "", 0, 0, "player was killed", LogType.KILL);
		} else {
			// Broadcast death.
			Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
						+ ChatColor.RED + killed.getName() + ChatColor.GOLD + " døde av naturlige årsaker!");		
			this.logHandler.log(this.userHandler.getUserId(killed.getName()), "", this.userHandler.getUserId(killed.getName()), "", 0, 0, "player died natural", LogType.KILLNATURAL);		
		}
		
		// Kick the player since he/she lost.
		killed.kickPlayer("Du " + (boolKilled ? "ble drept av " + killerr.getName() : " døde av naturlige årsaker") + " og er ute, kontakt GameDesk for hjelp.");

		// Check if finished, after adding point.
		this.gameHandler.checkIfGameFinished();
	}
	
	@EventHandler
	public void onGamePlayerLeaves(PpGamePlayerLeaveEvent event)
	{
		// When a player is killed, kick him/her out of server and log
		// it. Also check how many players are left.
		// Update mysql server, which a website is listening to.
		Player player = event.getPlayer();
		
		if (!event.wasKicked()) {
			this.gameHandler.removePlayer(player.getName());
			this.gameHandler.checkIfGameFinished();
			
			// Broadcast death.
			Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
						+ ChatColor.RED + player.getName() + ChatColor.GOLD + " logget av og er ute!");
		}
	}
	
}
