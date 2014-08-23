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

public class ModCommand extends BaseCommand {

	private LogHandler log;

	public ModCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.log = plugin.getLogHandler();
		
		// This command is not just for players.
		this.setPlayerCommand(false);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 2) {
			return false;
		}
		
		String toRank = args[0];
		int newRank = -1;
		
		if (toRank.equalsIgnoreCase("admin")) {
			newRank = 10;
		} else if (toRank.equalsIgnoreCase("mod")) {
			newRank = 5;
		} else if (toRank.equalsIgnoreCase("builder")) {
			newRank = 1;
		} else if (toRank.equalsIgnoreCase("guest")) {
			newRank = 0;
		} 
		
		if (newRank == -1) {
			return false;
		}
		
		String user = args[1];
		int userId = this.userHandler.getUserId(user);
		if (userId == -1) {
			sender.sendMessage(ChatColor.RED + "User was not found in database.");
			return true; // Don't send man text for command here.
		}
		
		user = this.userHandler.getUsername(userId);
		
		this.userHandler.updateAccess(newRank, userId);
		
		Player target = Bukkit.getPlayer(this.userHandler.getUserUUID(userId));
		if (target != null) {
			this.userHandler.getCacheUser(target.getName()).setAccess(newRank);	
		}
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "User " + ChatColor.WHITE + this.userHandler.getUsername(userId)
				+ ChatColor.DARK_GREEN + " has been made " + ChatColor.WHITE + toRank + ChatColor.DARK_GREEN + " by "
				+ ChatColor.WHITE + sender.getName() + ChatColor.DARK_GREEN + ".");
		this.log.log(this.userHandler.getUserId(sender.getName()), null, userId, null, 0, 0, toRank, LogType.MOD);
		
		return true;
	}

}
