package net.mittnett.edvin.area.PolarPartyArea.listeners;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GameHandler;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class EntityListener implements Listener {

	private PolarPartyArea plugin;
	private GameHandler gameHandler;

	public EntityListener(PolarPartyArea instance) {
		this.plugin = instance;
		this.gameHandler = instance.getGameHandler();
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if (!gameHandler.hasOngoingGame()) {
			event.setCancelled(true);			
		} else {
			if (gameHandler.hasOngoingGame()) {
				if (event.getEntity() instanceof Player) {
					Player pl = (Player) event.getEntity();
					if (pl.getKiller() instanceof Player) {
						// PvP
						if (this.gameHandler.isIgnored(pl.getKiller().getName())) {
							event.setCancelled(true);
						} else if (this.gameHandler.isIgnored(pl.getName())) {
							event.setCancelled(true);
						}
					}
				}
			}
		}
		
		return;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (gameHandler.hasOngoingGame()) {
			event.setCancelled(false);			
		} else if (gameHandler.isStarting()) {
			event.setCancelled(true);					
		} else if (!gameHandler.isFinished() && !gameHandler.isStarting() && !gameHandler.hasOngoingGame()) {
			event.setCancelled(true);					
		}
		
		return;
	}
	
}
