package net.mittnett.edvin.area.PolarPartyArea;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration config;
	
	private String pluginDatabase,commonDatabase,username,password,host;
	
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
			this.config.addDefault("db.database.plugin", "pp_area");
			this.config.addDefault("db.database.common", "pp_common");
			
			this.config.options().copyDefaults(true);
			plugin.saveConfig();
		}

		this.host = this.config.getString("db.host");
		this.username = this.config.getString("db.username");
		this.password = this.config.getString("db.password");
		this.pluginDatabase = this.config.getString("db.database.plugin");
		this.commonDatabase = this.config.getString("db.database.common");
	}

	/**
	 * 
	 * @return String
	 */
	public String getPluginDatabase() {
		return pluginDatabase;
	}

	/**
	 * 
	 * @return String
	 */
	public String getCommonDatabase() {
		return commonDatabase;
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

	public String getTempMap() {
		return "world_temp";
	}

}
