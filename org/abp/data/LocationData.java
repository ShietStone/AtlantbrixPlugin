package org.abp.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationData {

	private float x;
	private float y;
	private float z;
	private String worldName;
	
	public LocationData(float x, float y, float z, String worldName) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		if(worldName != null)
			this.worldName = worldName;
		else
			this.worldName = "";
	}
	
	public LocationData(Location location) {
		if(location != null) {
			x = (float) location.getX();
			y = (float) location.getY();
			z = (float) location.getZ();
			worldName = location.getWorld().getName();
		} else
			worldName = "";
	}
	
	public Location toLocation() {
		if(worldName == null)
			return null;
		
		World world = Bukkit.getWorld(worldName);
		
		if(world != null)
			return new Location(world, x, y, z);
		
		return null;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public String getWorldName() {
		return worldName;
	}
}
