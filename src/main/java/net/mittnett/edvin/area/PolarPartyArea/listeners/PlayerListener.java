package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GroupHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;
import net.mittnett.edvin.area.PolarPartyArea.handlers.PlayerData;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

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

	@SuppressWarnings("unused")
	private PolarPartyArea plugin;
	private UserHandler userHandler;
	private GroupHandler groupHandler;
	private LogHandler log;

	public PlayerListener(PolarPartyArea plugin) {
		this.plugin = plugin;
		this.userHandler = plugin.getUserHandler();
		this.groupHandler = plugin.getGroupHandler();
		this.log = plugin.getLogHandler();
	}
	
	@EventHandler
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();
		
		PlayerData pd = this.userHandler.getPlayerData(p.getUniqueId().toString());
		if (pd.isBanned()) {
			event.disallow(Result.KICK_BANNED, "You have been banned from the server, no reason has been given.");
			return;
		}
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		
		this.userHandler.loginUser(p);
		
		p.sendMessage(ChatColor.GOLD + "------------- " + ChatColor.DARK_AQUA + "POLARPARTY 23 " + ChatColor.GOLD + "-------------");
		p.sendMessage("Informasjon om compoen finner du pÂ www.polarparty.no/pp23/");
		p.sendMessage("For Â sende inn bidraget ditt bruk /compo");
		p.sendMessage("");
		
		int invites = this.groupHandler.getInvites(this.userHandler.getUserId(p.getName())).size();
		if (invites > 0) {
			p.sendMessage(ChatColor.DARK_GREEN + "Du har " + ChatColor.WHITE + invites + ChatColor.DARK_GREEN + " gruppeinvitasjon"
					+ (invites > 1 ? "er" : "") + ".");
			p.sendMessage("");
		}
		
		this.log.log(this.userHandler.getUserId(p.getName()), null, 0, null, 0, 0, p.getAddress().getAddress().getHostAddress(), LogType.JOIN);
		event.setJoinMessage(this.userHandler.getPrefix(p.getName()) + ChatColor.GREEN + " logget p√•.");
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		event.setQuitMessage(null);
		
		Broadcaster.broadcastAll(this.userHandler.getPrefix(p.getName()) + ChatColor.RED + " logget av.");
		
		this.userHandler.logout(p);
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
