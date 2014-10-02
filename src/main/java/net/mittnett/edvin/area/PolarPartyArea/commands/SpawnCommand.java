package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public class SpawnCommand extends BaseCommand {

	public SpawnCommand(PolarPartyArea plugin) {
		super(plugin);
		this.setAccessLevel(5);
		this.setPlayerCommand(true);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		player.teleport(player.getWorld().getSpawnLocation());
		
		return true;
	}

}
