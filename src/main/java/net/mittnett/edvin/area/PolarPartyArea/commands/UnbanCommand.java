package net.mittnett.edvin.area.PolarPartyArea.commands;

import java.util.UUID;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.BanDataCollection;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@SuppressWarnings("unused")
public class UnbanCommand extends BaseCommand {

	private LogHandler logHandler;

	public UnbanCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
		
		this.logHandler = plugin.getLogHandler();
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length < 1) {
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
			targetUUID = this.userHandler.getUserUUID(targetUID);
		}
		
		BanDataCollection bdc = this.userHandler.getBanData(targetUUID);
		if (!bdc.hasCreativeServerBan()) {
			player.sendMessage(ChatColor.RED + "Brukeren '" + ChatColor.WHITE + args[0] + ChatColor.RED + "' er ikke bannet.");
			return true;
		} else if (bdc.hasBan()) {
			if (bdc.hasCreativeServerBan()) {
				player.sendMessage(ChatColor.RED + "Brukeren er bannet på arena serveren, unban der.");
			} else {
				player.sendMessage(ChatColor.RED + "Brukeren er globalt bannet, kontakt Edvin for å få brukeren unbannet.");
			}
			return true;
		}
		
		Broadcaster.broadcastAll(ChatColor.DARK_GREEN + "Brukeren " + ChatColor.WHITE + this.userHandler.getPrefix(target)
				+ ChatColor.DARK_GREEN + " ble unbannet av " + ChatColor.RESET + this.userHandler.getPrefix(player.getName()));
		bdc.removeBan(UserHandler.SERVER_ID);
		
		return true;
		
	}

}
