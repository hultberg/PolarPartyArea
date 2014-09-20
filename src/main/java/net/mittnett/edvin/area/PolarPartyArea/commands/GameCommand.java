package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;

public class GameCommand extends BaseCommand {

	private PolarPartyArea plugin;
	private GameHandler gameHandler;
	
	public GameCommand(PolarPartyArea instance) {
		super(instance);
		this.setAccessLevel(10);
		this.setPlayerCommand(true);
		
		this.plugin = instance;
		this.gameHandler = instance.getGameHandler();
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length == 0) {
			player.sendMessage("/game - Shows this information");
			player.sendMessage("/game start - Start the new game counter.");
			player.sendMessage("/game stop - Stops a game.");
		} else if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("start")) {
				gameHandler.start(10);
			} else if (args[0].equalsIgnoreCase("stop")) {
				gameHandler.stop();
			} else if (args[0].equalsIgnoreCase("ignoreme")) {
				if (this.userHandler.getAccessLevel(player) >= 5) {
					if (this.gameHandler.isIgnored(player.getName())) {
						this.gameHandler.removedIgnoredPlayer(player.getName());
						player.sendMessage(ChatColor.GREEN + "Du er ikke lengre ignorert av arena systemet.");
					} else {
						gameHandler.ignorePlayer(player);
						player.sendMessage(ChatColor.GREEN + "Du er nå ignorert av arena systemet.");
					}
				}
			}
		}
		
		return true;
	}

}
