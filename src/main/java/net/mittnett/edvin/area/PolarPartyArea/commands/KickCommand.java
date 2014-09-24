package net.mittnett.edvin.area.PolarPartyArea.commands;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KickCommand extends BaseCommand {

	private LogHandler logHandler;

	public KickCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
		
		this.logHandler = plugin.getLogHandler();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 2) {
			return false;
		}
		
		int targetUID = this.userHandler.getUserId(args[0]);
		if (targetUID == -1) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' ble ikke funnet.");
			return true;
		}

		String target = "";
		Player tmp = Bukkit.getPlayer(args[0]);
		if (tmp != null) {
			target = tmp.getName();
		} else {
			target = this.userHandler.getUsername(targetUID);
		}
		
		if (target.equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.RED + "Du kan ikke kicke deg selv.");
			return true;			
		}
		
		// Set ban itself.
		StringBuilder build = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			build.append(args[i] + " ");
		}
		String reason = build.toString();
		
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "Brukeren " + ChatColor.WHITE + this.userHandler.getPrefix(target)
				+ ChatColor.DARK_GREEN + " ble kicket for: " + ChatColor.WHITE + reason);
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "Kicket av: " + ChatColor.RESET + this.userHandler.getPrefix(player.getName()));
		this.logHandler.log(this.userHandler.getUserId(player.getName()), null, targetUID, null, 0, 0, reason, LogType.KICK);
		
		if (tmp != null) {
			tmp.kickPlayer("Kicket: " + reason);
		}
		
		return true;
		
	}

}