package net.mittnett.edvin.area.PolarPartyArea.commands;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MCommand extends BaseCommand {

	public MCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(0);
		this.setPlayerCommand(true);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 2) {
			return false;
		}
		
		Player target = Bukkit.getPlayer(args[0]);
		if (target == null) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' er ikke online.");
			return true;
		}
		
		if (target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.RED + "Du kan ikke sende en melding til deg selv.");
			return true;			
		}
		
		StringBuilder build = new StringBuilder();
		for (int i = 1; i < args.length; i++) {
			build.append(args[i] + " ");
		}
		
		target.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] -> [" + target.getName() + "] " + build.toString());
		player.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] -> [" + target.getName() + "] " + build.toString());
		this.userHandler.addLastMessageGot(player, target);
		this.userHandler.addLastMessageSent(player, target);
		
		return true;
	}

}