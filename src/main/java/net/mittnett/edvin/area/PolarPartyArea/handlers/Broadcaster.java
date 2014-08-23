package net.mittnett.edvin.area.PolarPartyArea.handlers;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Broadcaster extends BaseHandler {

	private GroupHandler groupHandler;

	public Broadcaster(PolarPartyArea plugin) {
		super(plugin);
		
		this.groupHandler = plugin.getGroupHandler();
	}
	
	public void prepare() {
		
	}

	public void cleanup() {
		
	}
	
	public static void broadcastAll(String msg) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(msg);
		}
	}
	
	public static void broadcastChat(String from, String msg) {
		String chatLine = from + ": " + ChatColor.WHITE + msg;
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(chatLine);
		}
		
		Bukkit.getConsoleSender().sendMessage(chatLine);
	}
	
	public void broadcastGroup(int gid, String from, String msg) {
		String chatLine = ChatColor.GREEN + "(" + this.groupHandler.getGroupName(gid) + ") " + from + ": " + ChatColor.YELLOW + msg;
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerData pd = this.userHandler.users.get(p.getName());
			if (pd.getGroupId() == gid)
				p.sendMessage(chatLine);
		}
		
		System.out.println(chatLine);
	}

}
