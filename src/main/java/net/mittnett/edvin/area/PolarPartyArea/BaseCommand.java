package net.mittnett.edvin.area.PolarPartyArea;

import net.mittnett.edvin.area.PolarPartyArea.handlers.UserHandler;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class BaseCommand implements CommandExecutor {

	protected final PolarPartyArea plugin;
	protected final UserHandler userHandler;
	
	private int accessLevel;
	
	/**
	 * If this command only works for Player(s).
	 */
	private boolean isPlayerCommand;	
	
	public BaseCommand(PolarPartyArea plugin) {
		this.plugin = plugin;
		this.userHandler = plugin.getUserHandler();
		
		this.setAccessLevel(10);
		this.setPlayerCommand(false);
	}

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		
		if (this.isPlayerCommand && !BaseCommand.isPlayer(arg0)) {
			sendMessage(arg0, arg1.getName(), ChatColor.RED + "This command is for only for Player");
			return false;
		}
		
		int access = this.userHandler.getAccessLevel(arg0.getName());
		if (accessLevel > access && !arg0.isOp()) {
			sendMessage(arg0, arg1.getName(), ChatColor.RED + "Permission denied.");
			return false;
		}
		
		if (BaseCommand.isPlayer(arg0) && this.isPlayerCommand) {
			Player player = (Player) arg0;			
			return onPlayerCommand(player, null, arg1, arg2, arg3);
		} else {
			return onPlayerCommand(null, arg0, arg1, arg2, arg3);			
		}
	}
	
	public abstract boolean onPlayerCommand(Player player, CommandSender sender, Command command, String label, String[] args);
	
	public static boolean isPlayer(final CommandSender sender) {
		return sender instanceof Player;
	}

	public void setAccessLevel(int accessLevel) {
		this.accessLevel = accessLevel;
	}

	public void setPlayerCommand(boolean isPlayerCommand) {
		this.isPlayerCommand = isPlayerCommand;
	}
	
	public void sendMessage(CommandSender player, String command, String message) {
		player.sendMessage(ChatColor.DARK_GRAY + command + ": " + message);
	}

}
