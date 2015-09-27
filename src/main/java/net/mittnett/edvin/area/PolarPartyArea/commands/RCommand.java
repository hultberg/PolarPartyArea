package net.mittnett.edvin.area.PolarPartyArea.commands;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RCommand extends BaseCommand {

	public RCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(0);
		this.setPlayerCommand(true);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 1) {
			return false;
		}
		
		String targetName = this.userHandler.getLastMessageGot(player.getName());
		if (targetName == null) {
			player.sendMessage(ChatColor.RED + "Ingen personer å svare til.");
			return true;
		}
		
		Player target = Bukkit.getPlayer(targetName);
		if (target == null) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + targetName + ChatColor.RED + "' er ikke online.");
			return true;
		}
		
		if (target.getName().equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.RED + "Du kan ikke sende en melding til deg selv.");
			return true;			
		}
		
		StringBuilder build = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			build.append(args[i] + " ");
		}
		
		target.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] -> [" + target.getName() + "] " + build.toString());
		player.sendMessage(ChatColor.AQUA + "[" + player.getName() + "] -> [" + target.getName() + "] " + build.toString());
		this.userHandler.addLastMessageGot(player, target);
		this.userHandler.addLastMessageSent(player, target);
		
		return true;
	}

}