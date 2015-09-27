package net.mittnett.edvin.area.PolarPartyArea.commands;

import java.util.UUID;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanDataCollection;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BanCommand extends BaseCommand {

	public BanCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 3) {
			return false;
		}
		
		int targetUID = this.userHandler.getUserId(args[0]);
		if (targetUID == -1) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' ble ikke funnet.");
			return true;
		}
		
		int banType = UserHandler.SERVER_ID;
		try {
			banType = Integer.parseInt(args[1]);
		} catch (NumberFormatException ex) {
			player.sendMessage(ChatColor.RED + "BanMode forventes som et tall.");
			return false;
		}
		
		if (banType == 0 && this.userHandler.getAccessLevel(player) != 10) {
			player.sendMessage(ChatColor.RED + "Kun admins kan sette globale bans.");
			return true;
		}

		String target = "";
		UUID targetUUID = UUID.randomUUID();
		Player tmp = Bukkit.getPlayer(args[0]);
		if (tmp != null) {
			target = tmp.getName();
			targetUUID = tmp.getUniqueId();
		} else {
			target = this.userHandler.getUsername(targetUID);
			targetUUID = this.userHandler.getUserUUID(target);
		}
		
		BanDataCollection bdc = this.userHandler.getBanData(targetUUID);
		if (bdc.hasBan()) {
			if (bdc.hasArenaServerBan()) {
				player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' er bannet på denne serveren.");	
				return true;			
			} else if (bdc.hasGlobalBan()) {
				player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' er globalt bannet.");		
				return true;				
			} else if (bdc.hasCreativeServerBan()) {
				player.sendMessage(ChatColor.DARK_RED + "INFO:" + ChatColor.YELLOW + " Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.YELLOW + "' er bannet på creative serveren.");			
			}
		}
		
		if (target.equalsIgnoreCase(player.getName())) {
			player.sendMessage(ChatColor.RED + "Du kan ikke banne deg selv.");
			return true;			
		}
		
		// Set ban itself.
		StringBuilder build = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			build.append(args[i] + " ");
		}
		String reason = build.toString();
		
		this.userHandler.setBan(targetUUID, player.getUniqueId(), reason, banType);
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "Brukeren " + ChatColor.WHITE + this.userHandler.getPrefix(target)
				+ ChatColor.DARK_GREEN + " ble"
				+ (banType == 0 ? ChatColor.DARK_RED + " globalt" + ChatColor.DARK_GREEN.toString() : "") + " bannet for: " + ChatColor.WHITE + reason);
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "Bannet av: " + ChatColor.RESET + this.userHandler.getPrefix(player.getName()));
		
		if (tmp != null) {
			tmp.kickPlayer("BANNET: " + reason + " | For klager/spørsmål, kontakt game-crew.");
		}
		
		return true;
		
	}
}
