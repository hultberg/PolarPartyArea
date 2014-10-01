package net.mittnett.edvin.area.PolarPartyArea.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanDataCollection;

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
		
		if (args.length == 0) {
			// List of online users.
			
			Player[] ps = Bukkit.getOnlinePlayers();
			StringBuilder sb = new StringBuilder();
			for (Player p : ps) {
				switch (this.userHandler.getAccessLevel(p)) {
				case 10:
					sb.append(PolarPartyArea.ADMIN_COLOUR);
					break;
				case 5:
					sb.append(PolarPartyArea.MOD_COLOUR);					
					break;
				case 1:
					sb.append(PolarPartyArea.BUILDER_COLOUR);
					break;
				case 0:
					sb.append(PolarPartyArea.GUEST_COLOUR);
					break;
				default: break;
				}
				sb.append(p.getName() + ChatColor.RESET + ", ");
			}
			
			String players = sb.toString();
			players = players.substring(0, (players.length() - 2)) + ".";
			
			player.sendMessage(ChatColor.GREEN + "Spillere pålogget (" + ps.length + "): " + ChatColor.RESET + players);
		} else {
			// Try to find target.
			String target = args[0];
			UUID targetUUID = UUID.randomUUID();
			int uid = this.userHandler.getUserId(target);
			
			Player tmp = Bukkit.getPlayer(target);
			if (tmp != null) {
				targetUUID = tmp.getUniqueId();
				target = tmp.getName();
				player.sendMessage(target + ChatColor.GREEN + " er pålogget nå.");
			} else {
				if (uid == -1) {
					player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + target + ChatColor.RED + "' er ikke online.");
					return true;
				}
				
				targetUUID = this.userHandler.getUserUUID(uid);
				target = this.userHandler.getUsername(uid); // Refetch username to get it right.
				player.sendMessage(target + ChatColor.RED + " er frakoblet.");	
			}
			
			// Check bans
			BanDataCollection bdc = this.userHandler.getBanData(targetUUID);
			if (bdc.hasBan()) {
				player.sendMessage(ChatColor.RED + "Denne brukeren har bans, se " + ChatColor.GRAY + "/listbans " + target + ChatColor.RED + ".");
			}
		}
		return true;
	}

}
