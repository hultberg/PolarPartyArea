package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {

	private PolarPartyArea plugin;

	public EntityListener(PolarPartyArea plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!plugin.hasOngoingGame())
			event.setCancelled(true);
		
		return;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (!plugin.hasOngoingGame())
			event.setCancelled(true);
		
		return;
	}
	
}
