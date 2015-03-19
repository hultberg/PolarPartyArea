package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

	@SuppressWarnings("unused")
	private PolarPartyArea plugin;

	public BlockListener(PolarPartyArea plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {		
		if (event.getPlayer().getGameMode() == GameMode.ADVENTURE) {
			event.setCancelled(true);
			return;
		}
	}
	
}
