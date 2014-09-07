package net.mittnett.edvin.area.PolarPartyArea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PpGamePlayerKilledEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Player p_killed;
	private Player p_killer;
	
	public PpGamePlayerKilledEvent(Player killed, Player killer) {
		this.p_killed = killed;
		this.p_killer = killer;
	}
	 
	public Player getKilledPlayer() {
		return p_killed;
	}

	public Player getKiller() {
		return p_killer;
	}

	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
