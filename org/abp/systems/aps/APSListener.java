package org.abp.systems.aps;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.logging.PlayerLogger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class APSListener implements Listener {

	private Plugin plugin;
	private AreaProtectionSystem areaProtectionSystem;
	
	APSListener(Plugin plugin, AreaProtectionSystem areaProtectionSystem) {
		this.plugin = plugin;
		this.areaProtectionSystem = areaProtectionSystem;	
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		
		if(player.isOp())
			return;
		
		Block interactedBlock = event.getClickedBlock();
		
		if(interactedBlock != null) {
			if(areaProtectionSystem.inProtectedArea(new LocationData(interactedBlock.getLocation()))) {
				event.setCancelled(true);
				PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
				playerLogger.log("§eYou are not allowed to do this");
			}
		}
	}
}
