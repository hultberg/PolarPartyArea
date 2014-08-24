package net.mittnett.edvin.area.PolarPartyArea;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration config;
	
	private String database,username,password,host,orgMap,tempMap;
	
	public ConfigurationHandler(PolarPartyArea instance)
	{
		this.plugin = instance;
		this.config = plugin.getConfig();
	}
	
	public void loadConfig()
	{
		if (!this.config.getBoolean("config.default")) {
			// Database
			this.config.addDefault("config.default", true);
			this.config.addDefault("db.host", "localhost");
			this.config.addDefault("db.username", "mc");
			this.config.addDefault("db.password", "m");
			this.config.addDefault("db.database", "minecraft");
			this.config.addDefault("maps.orginalMapName", "world");
			this.config.addDefault("maps.tempMap", "world_temp");
			
			this.config.options().copyDefaults(true);
			plugin.saveConfig();
		}

		this.host = this.config.getString("db.host");
		this.username = this.config.getString("db.username");
		this.password = this.config.getString("db.password");
		this.database = this.config.getString("db.database");
		this.orgMap = this.config.getString("maps.orginalMapName");
		this.tempMap = this.config.getString("maps.tempMap");
	}

	/**
	 * 
	 * @return String
	 */
	public String getDatabase() {
		return database;
	}

	/**
	 * 
	 * @return String
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * 
	 * @return String
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * 
	 * @return String
	 */
	public String getHost() {
		return host;
	}

	public String getOrgMap() {
		return orgMap;
	}

	public String getTempMap() {
		return tempMap;
	}

}
