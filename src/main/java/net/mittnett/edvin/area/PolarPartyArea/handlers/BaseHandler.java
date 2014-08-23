package net.mittnett.edvin.area.PolarPartyArea.handlers;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public abstract class BaseHandler {

	protected final PolarPartyArea plugin;
	protected final UserHandler userHandler;
	
	public BaseHandler(PolarPartyArea plugin) {
		this.plugin = plugin;
		this.userHandler = plugin.getUserHandler();
	}
	
	public abstract void prepare();
	
	public abstract void cleanup();
	
}
