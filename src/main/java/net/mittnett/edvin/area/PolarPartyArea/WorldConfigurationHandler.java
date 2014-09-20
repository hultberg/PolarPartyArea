package net.mittnett.edvin.area.PolarPartyArea;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class WorldConfigurationHandler {
	
	private PolarPartyArea plugin;
	private FileConfiguration config;
	private File worldFile = null;
	private World world = null;
	
	private int deathPointX,deathPointY,deathPointZ;
	
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
		
		// Load some zones.
		this.deathPointX = this.config.getInt("zones.deathZone.X");
		this.deathPointY = this.config.getInt("zones.deathZone.Y");
		this.deathPointZ = this.config.getInt("zones.deathZone.Z");
	}
	
	public World getWorld() {
		return this.world;
	}

	public int getDeathPointX() {
		return deathPointX;
	}

	public int getDeathPointY() {
		return deathPointY;
	}

	public int getDeathPointZ() {
		return deathPointZ;
	}
	
	public Location getDeathPointLocation() {
		return new Location(this.getWorld(), this.getDeathPointX(), this.getDeathPointY(), this.getDeathPointZ());
	}
	
	public void setNewDeathPoint(Location loc) {
		this.config.set("zones.deathZone.X", loc.getBlockX());
		this.config.set("zones.deathZone.Y", loc.getBlockY());
		this.config.set("zones.deathZone.Z", loc.getBlockZ());
		
		this.loadConfig();
	}

}
