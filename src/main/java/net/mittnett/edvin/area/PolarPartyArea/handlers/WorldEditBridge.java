package net.mittnett.edvin.area.PolarPartyArea.handlers;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public class WorldEditBridge {

	private PolarPartyArea plugin;
	private WorldEditPlugin weplugin;
	
	public WorldEditBridge(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.weplugin = null;
	}
	
	public boolean loadWe()
	{
		this.weplugin = (WorldEditPlugin) this.plugin.getServer().getPluginManager().getPlugin("WorldEdit");
		
		if (this.weplugin == null) {
			PolarPartyArea.log.severe("WorldEdit was not found.");
			return false;
		}
		
		return true;
	}
	
	public boolean isEnabled()
	{
		return this.weplugin != null;
	}
	
	public WorldEditPlugin getWorldEdit()
	{
		return this.weplugin;
	}
	
}
