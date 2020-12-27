package org.abp.data;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class PlayerUtil {

	public static String nameToUUID(String name) {
		@SuppressWarnings("deprecation")
		OfflinePlayer player = Bukkit.getOfflinePlayer(name);
		
		if(player != null)
			return player.getUniqueId().toString();
		else
			return null;
	}
	
	public static String uuidToName(String uuid) {
		try {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
			return player.getName();
		} catch(Exception exception) {
			return null;
		}
	}
}
