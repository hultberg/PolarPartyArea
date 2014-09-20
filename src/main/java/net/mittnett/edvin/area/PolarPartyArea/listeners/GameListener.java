package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.WorldConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerKilledEvent;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGamePlayerLeaveEvent;
import net.mittnett.edvin.area.PolarPartyArea.events.PpGameStartedEvent;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class GameListener implements Listener {
	
	private PolarPartyArea plugin;
	private GameHandler gameHandler;
	private ConfigurationHandler config;
	private WorldConfigurationHandler worldconfig;

	public GameListener(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.gameHandler = instance.getGameHandler();
		this.config = instance.getConfigHandler();
		this.worldconfig = instance.getWorldConfigHandler();
	}
	
	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		
		String str = ChatColor.GREEN + "Join for å bli med!";		
		if (this.gameHandler.hasOngoingGame()) {
			str = ChatColor.RED + "Match pågår...";
		} else if (!this.gameHandler.getServerAllowCompo()) {
			str = ChatColor.RED + "Server stengt";			
		}
		
		event.setMotd(str);
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onGameStarted(PpGameStartedEvent event)
	{		
		
		World w = this.worldconfig.getWorld();
		w.setDifficulty(Difficulty.NORMAL);
		w.setPVP(true);
		
		// Teleport everybody
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (this.gameHandler.isIgnored(p.getName()) != true) {
				p.teleport(Bukkit.getWorld(this.config.getTempMap()).getSpawnLocation());
				p.setGameMode(GameMode.SURVIVAL);
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
		Player killer = event.getKiller();
		
		this.gameHandler.removePlayer(killed.getName());
		
		// Kick killed
		killed.sendMessage(ChatColor.RED + "Du ble drept av " + ChatColor.WHITE.toString() + killer.getName() + ChatColor.RED + " og du er derfor ute av runden.");
		
		// Teleport player to deathpoint.
		killed.teleport(this.worldconfig.getDeathPointLocation());
		killed.setGameMode(GameMode.ADVENTURE);
		
		// Give killer one point.
		this.gameHandler.addPoints(killer.getName(), 1);

		// Check if finished, after adding point.
		this.gameHandler.checkIfGameFinished();
		
		// Broadcast death.
		Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
					+ ChatColor.RED + killed.getName() + ChatColor.GOLD + " ble drept av "
					+ ChatColor.RED + killer.getName() + ChatColor.GOLD + "!");
	}
	
	@EventHandler
	public void onGamePlayerLeaves(PpGamePlayerLeaveEvent event)
	{
		// When a player is killed, kick him/her out of server and log
		// it. Also check how many players are left.
		// Update mysql server, which a website is listening to.
		Player player = event.getPlayer();
		
		this.gameHandler.removePlayer(player.getName());
		this.gameHandler.checkIfGameFinished();
		
		// Broadcast death.
		Broadcaster.broadcastAll(ChatColor.DARK_GRAY + "> "
					+ ChatColor.RED + player.getName() + ChatColor.GOLD + " logget av og er ute!");
	}
	
}
