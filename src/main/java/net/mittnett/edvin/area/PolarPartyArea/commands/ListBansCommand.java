package net.mittnett.edvin.area.PolarPartyArea.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanData;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanDataCollection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListBansCommand extends BaseCommand {
	
	private SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM/yyyy HH:mm:ss");

	public ListBansCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length == 0) {
			return false;
		}
		
		int targetUID = this.userHandler.getUserId(args[0]);
		if (targetUID == -1) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' ble ikke funnet.");
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

		player.sendMessage(ChatColor.DARK_GREEN + "Ban-oversikt for " + ChatColor.WHITE + target + ChatColor.DARK_GREEN + ":");
		BanDataCollection bdc = this.userHandler.getBanData(targetUUID);
		if (bdc.hasBans()) {
			for (Integer i : bdc.getAllBans().keySet()) {
				BanData bd = bdc.getSpecificBanData(i);
				String server = bd.getFromServerFriendly();
				if (bd.getFromServer() == 0) {
					server = ChatColor.DARK_RED + "Global";
				}

				Date time = new Date((long)bd.getTimestamp()*1000);
				player.sendMessage(ChatColor.DARK_GREEN + "Ban: " + ChatColor.WHITE + bd.getReason());
				player.sendMessage(ChatColor.DARK_GREEN + "BanID: " + ChatColor.WHITE + bd.getBanID());
				player.sendMessage(ChatColor.DARK_GREEN + "Server: " + ChatColor.WHITE + server + ChatColor.DARK_GREEN + ", "
							+ ChatColor.GREEN + "satt av: " + ChatColor.WHITE + this.userHandler.getUsername(bd.getBanner())
							+ ChatColor.GREEN + ".");
				player.sendMessage(ChatColor.DARK_GREEN + "Dato: " + ChatColor.WHITE + dateformat.format(time));
				player.sendMessage("");
			}
			player.sendMessage("Totalt " + bdc.getAllBans().size() + " ban(s)");
		} else {
			player.sendMessage("(ingen bans funnet)");			
		}
		player.sendMessage(ChatColor.DARK_GREEN + "----------------------");		
		
		return true;
		
	}
}
