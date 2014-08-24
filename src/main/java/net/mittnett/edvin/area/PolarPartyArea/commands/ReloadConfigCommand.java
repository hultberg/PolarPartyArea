/**
 * 
 */
package net.mittnett.edvin.area.PolarPartyArea.commands;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author edvin
 *
 */
public class ReloadConfigCommand extends BaseCommand {

	/**
	 * @param plugin
	 */
	public ReloadConfigCommand(PolarPartyArea plugin) {
		super(plugin);
		this.setAccessLevel(10);
		this.setPlayerCommand(false);
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		
		plugin.reloadConfig();
		
		return true;
	}

}
