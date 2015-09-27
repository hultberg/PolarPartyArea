package net.mittnett.edvin.area.PolarPartyArea.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public class TpAllCommand extends BaseCommand {

	public TpAllCommand(PolarPartyArea plugin) {
		super(plugin);
		this.setAccessLevel(10);
		this.setPlayerCommand(true);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.getName().equalsIgnoreCase(player.getName())) continue;
			
			p.teleport(player);
		}
		
		return true;
	}

}
