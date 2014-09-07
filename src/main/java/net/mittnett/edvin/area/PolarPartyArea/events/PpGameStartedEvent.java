package net.mittnett.edvin.area.PolarPartyArea.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PpGameStartedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	public PpGameStartedEvent()
	{
		
	}
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
