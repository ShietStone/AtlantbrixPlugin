package org.abp.systems.pls;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.logging.Logger;
import org.abp.logging.MultiLogger;
import org.abp.logging.PlayerLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

public class PLSListener implements Listener {

	private Plugin plugin;
	private PortalLinkSystem portalLinkSystem;
	
	PLSListener(Plugin plugin, PortalLinkSystem portalLinkSystem) {
		this.plugin = plugin;
		this.portalLinkSystem = portalLinkSystem;
	}
	
	@EventHandler
	public void onPortalTravel(PlayerPortalEvent event) {
		Player player = event.getPlayer();
		MultiLogger logger = new MultiLogger(new Logger[] {new PlayerLogger(player, plugin.getChatTheme()), plugin});
		LocationData linkedLocation = portalLinkSystem.getLinkedLocation(new LocationData(player.getLocation()));
		
		if(linkedLocation != null) {
			Location location = linkedLocation.toLocation();
			
			if(location != null)
				event.setTo(location);
			else {
				logger.log("§eLinked location could not be found");
				event.setCancelled(true);
			}
		}
	}
}
