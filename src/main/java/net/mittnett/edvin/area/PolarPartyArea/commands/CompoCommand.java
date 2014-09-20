package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;

public class CompoCommand extends BaseCommand {

	private GameHandler gameHandler;

	public CompoCommand(PolarPartyArea plugin) {
		super(plugin);
		
		this.gameHandler = plugin.getGameHandler();
		
		this.setAccessLevel(10);
		this.setPlayerCommand(false);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("allow")) {
				this.gameHandler.setAllowCompo(true);
				sender.sendMessage("Allowing compo!");
			} else if (args[0].equalsIgnoreCase("disallow")) {
				this.gameHandler.setAllowCompo(false);	
				sender.sendMessage("Disallowing compo!");			
			}
		}
		
		return true;
	}

}
