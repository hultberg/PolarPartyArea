package net.mittnett.edvin.area.PolarPartyArea.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PpGamePlayerLeaveEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private Player player;
	private boolean kicked;
	
	public PpGamePlayerLeaveEvent(Player arg0, boolean kicked)
	{
		this.player = arg0;
		this.kicked = kicked;
	}
	
	public Player getPlayer()
	{
		return this.player;
	}
	
	public boolean wasKicked()
	{
		return this.kicked;
	}
	 
	public HandlerList getHandlers() {
	    return handlers;
	}
	 
	public static HandlerList getHandlerList() {
	    return handlers;
	}

}
