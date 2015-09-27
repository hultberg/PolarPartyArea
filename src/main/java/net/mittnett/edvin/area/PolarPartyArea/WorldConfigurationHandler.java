package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WorldConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration config;
	private File worldFile = null;
	private World world = null;
	
	private int spectPointX,spectPointY,spectPointZ;
	
	public WorldConfigurationHandler(PolarPartyArea instance, World w)
	{
		this.plugin = instance;
		this.config = plugin.getConfig();
		
		this.world = w;
	}
	
	public void loadConfig()
	{
		if (worldFile == null) {
			worldFile = new File(plugin.getDataFolder(), this.world.getName() + ".yml");
		}		
		
		config = YamlConfiguration.loadConfiguration(worldFile);
		
		if (!worldFile.exists()) {
			this.config.addDefault("zones.spectZone.X", 0);
			this.config.addDefault("zones.spectZone.Y", 128);
			this.config.addDefault("zones.spectZone.Z", 0);	
			
			this.config.options().copyDefaults(true);
			
			try {
				config.save(worldFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Load some zones.
		this.spectPointX = this.config.getInt("zones.spectZone.X");
		this.spectPointY = this.config.getInt("zones.spectZone.Y");
		this.spectPointZ = this.config.getInt("zones.spectZone.Z");
	}
	
	public World getWorld() {
		return Bukkit.getWorld("world_temp");
	}

	public int getSpectatePointX() {
		return this.spectPointX;
	}

	public int getSpectatePointY() {
		return this.spectPointY;
	}

	public int getSpectatePointZ() {
		return this.spectPointZ;
	}
	
	public Location getSpectateSpawnPoint() {
		return new Location(this.getWorld(), this.getSpectatePointX(), this.getSpectatePointY(), this.getSpectatePointZ());
	}
	
	public void setSpectateSpawnPoint(Location loc) {
		this.config.set("zones.spectZone.X", loc.getBlockX());
		this.config.set("zones.spectZone.Y", loc.getBlockY());
		this.config.set("zones.spectZone.Z", loc.getBlockZ());

		try {
			config.save(worldFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.loadConfig();
	}

}
