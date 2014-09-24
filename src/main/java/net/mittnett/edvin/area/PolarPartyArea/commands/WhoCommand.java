package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public class WhoCommand extends BaseCommand {

	public WhoCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(0);
		this.setPlayerCommand(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		String target = player.getName();
		Player targetPl = player;		
		boolean isOnline = true;
		
		if (args.length > 0) {
			target = args[0];
			
			// Find player
			targetPl = Bukkit.getPlayer(target);
			if (targetPl == null) {
				isOnline = false;
			}
		}
		
		if (isOnline) {
			player.sendMessage(ChatColor.GREEN + "Brukeren " + ChatColor.WHITE.toString() + target + ChatColor.GREEN + " er pålogget.");			
		} else {
			player.sendMessage(ChatColor.RED + "Brukeren " + ChatColor.WHITE.toString() + target + ChatColor.RED + " er frakoblet.");
		}
		
		if (this.userHandler.getAccessLevel(player) >= 5) {
			player.sendMessage(ChatColor.GRAY + "BrukerID: " + ChatColor.WHITE.toString() + this.userHandler.getUserId(target));
			player.sendMessage(ChatColor.GRAY + "Tilgang: " + ChatColor.WHITE.toString() + this.userHandler.getAccessLevel(target));
			if (isOnline) {
				player.sendMessage(ChatColor.GRAY + "Gamemode: " + ChatColor.WHITE.toString() + targetPl.getGameMode().toString());				
			}
		}
		
		return true;
	}

}
