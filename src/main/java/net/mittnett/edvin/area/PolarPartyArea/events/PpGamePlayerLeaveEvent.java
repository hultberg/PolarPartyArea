package net.mittnett.edvin.area.PolarPartyArea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PpGamePlayerLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	
	public PpGamePlayerLeaveEvent(Player arg0)
	{
		this.player = arg0;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
