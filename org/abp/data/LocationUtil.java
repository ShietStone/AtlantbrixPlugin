package org.abp.data;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {
	
	public static Location getFallbackLocation() {
		return Bukkit.getWorlds().get(0).getSpawnLocation();
	}
	
	public static boolean areEqual(Location a, Location b) {
		if(a != null && b != null)
			return a.getBlockX() == b.getBlockX() && a.getBlockY() == b.getBlockY() && a.getBlockZ() == b.getBlockZ() 
				&& a.getWorld().getName().equals(b.getWorld().getName());
		else
			return false;
	}
}
