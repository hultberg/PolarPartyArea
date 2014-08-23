package net.mittnett.edvin.area.PolarPartyArea;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration yml;
	
	public ConfigurationHandler(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.yml = plugin.getConfig().;
	}
	
	public void loadConfig()
	{
		
	}

}
