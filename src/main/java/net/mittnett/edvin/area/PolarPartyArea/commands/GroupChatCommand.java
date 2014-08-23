/**
 * 
 */
package net.mittnett.edvin.area.PolarPartyArea.commands;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.LogType;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author edvin
 *
 */
public class GroupChatCommand extends BaseCommand {

	private Broadcaster broadcaster;
	private LogHandler log;

	/**
	 * @param plugin
	 */
	public GroupChatCommand(PolarPartyArea plugin) {
		super(plugin);
		this.setAccessLevel(1);
		this.setPlayerCommand(true);
		
		this.broadcaster = plugin.getBroadcaster();
		this.log = plugin.getLogHandler();
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length == 0) {
			return false;
		}
		
		int grId = this.userHandler.getGroupId(player.getName());
		if (grId == -1) {
			player.sendMessage(ChatColor.RED + "Du er ikke medlem av noen grupper.");
			return true;
		}
		
		StringBuilder sb = new StringBuilder();
		for (String s : args) {
			sb.append(s);
		}
		
		String msg = sb.toString();

		this.log.log(this.userHandler.getUserId(player.getName()), player.getName(), grId, null, 0, 0, msg, LogType.CHAT);
		broadcaster.broadcastGroup(grId, player.getName(), msg);
		
		return true;
	}

}
