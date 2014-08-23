package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration config;
	private File file;
	
	public ConfigurationHandler(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.config = plugin.getConfig();
	}
	
	public void loadConfig()
	{
		// Database
		this.config.addDefault("db.host", "localhost");
		this.config.addDefault("db.username", "mc");
		this.config.addDefault("db.password", "m");
		this.config.addDefault("db.database", "minecraft");
		
		// Zones
		this.config.addDefault("zones.", "minecraft");
	}

}
