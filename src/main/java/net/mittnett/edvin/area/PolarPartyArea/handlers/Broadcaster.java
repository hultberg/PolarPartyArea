package net.mittnett.edvin.area.PolarPartyArea.handlers;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Broadcaster extends BaseHandler {

	public Broadcaster(PolarPartyArea plugin) {
		super(plugin);
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

}
