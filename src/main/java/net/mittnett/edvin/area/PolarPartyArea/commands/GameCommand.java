package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.WorldConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;

public class GameCommand extends BaseCommand {

	private GameHandler gameHandler;
	private WorldConfigurationHandler worldconfig;
	
	public GameCommand(PolarPartyArea instance) {
		super(instance);
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
		
		this.gameHandler = instance.getGameHandler();
		this.worldconfig = instance.getWorldConfigHandler();
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		int s = this.userHandler.getAccessLevel(player);
		
		if (args.length == 0) {
			player.sendMessage("/game - Shows this information");
			player.sendMessage("/game start - Start the new game counter.");
			player.sendMessage("/game stop - Stops a game.");
			player.sendMessage("/game ignoreMe - (Toggle) Ingore yourself in arena system.");
			player.sendMessage("/game setspectpoint - Set new spectate spawnpoint");
			player.sendMessage("/game testspectpoint - Test spectate spawnpoint");
		} else if (args.length >= 1) {
			if (args[0].equalsIgnoreCase("start") && s == 10) {
				if (this.gameHandler.getPlayerThatCanMatch() > 2) {
					player.sendMessage(ChatColor.RED + "Det er for få spillere på servern!");
					player.sendMessage(ChatColor.RED + "Minimum er to som ikke er ignorert, spillere telt: " + this.gameHandler.getPlayerThatCanMatch());
				} else {
					player.sendMessage(ChatColor.GREEN + "Starter en match.");
					gameHandler.start(10);					
				}
				
			} else if (args[0].equalsIgnoreCase("stop") && s == 10) {
				player.sendMessage(ChatColor.GREEN + "Stopper matchen.");
				gameHandler.stop();
			} else if (args[0].equalsIgnoreCase("ignoreme")) {
				if (this.gameHandler.isIgnored(player.getName())) {
					this.gameHandler.removedIgnoredPlayer(player.getName());
					player.sendMessage(ChatColor.GREEN + "Du er ikke lengre ignorert av arena systemet.");
				} else {
					gameHandler.ignorePlayer(player);
					player.sendMessage(ChatColor.GREEN + "Du er nå ignorert av arena systemet.");
				}
			} else if (args[0].equalsIgnoreCase("setspectpoint") && s == 10) {
				player.sendMessage(ChatColor.GREEN + "Setting new spectate spawnpoint.");
				worldconfig.setSpectateSpawnPoint(player.getLocation());
			} else if (args[0].equalsIgnoreCase("testspectpoint") && s == 10) {
				player.sendMessage(ChatColor.GREEN + "Testing spectate point.");
				player.teleport(this.worldconfig.getSpectateSpawnPoint());
			}
		}
		
		return true;
	}

}
