package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

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
		
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {		
		
	}
	
}
