package org.abp.systems.bms;

import org.abp.core.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@SuppressWarnings("deprecation")
public class BMSListener implements Listener {

	private Plugin plugin;
	
	BMSListener(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		
		event.setJoinMessage(plugin.getChatTheme().apply("§bPlayer §h" + player.getName() + " §bjoined the server"));
		
		if(!player.hasPlayedBefore())
			player.sendMessage(plugin.getChatTheme().apply("§bWelcome §h" + player.getName()));
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		
		event.setQuitMessage(plugin.getChatTheme().apply("§bPlayer §h" + player.getName() + " §bleft the server"));
	}
	
	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player player = event.getPlayer();
		
		event.setLeaveMessage(plugin.getChatTheme().applyWithoutTag("§bPlayer §h" + player.getName() + 
				" §bwas kicked because of §h" + event.getReason()));
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player player = event.getEntity();
		Player killer = player.getKiller();
		
		if(killer != null)
			Bukkit.broadcastMessage(plugin.getChatTheme().apply("§h" + player.getName() + " §bwas killed by §h" + 
					killer.getName()));
		else
			Bukkit.broadcastMessage(plugin.getChatTheme().apply("§h" + player.getName() + " §bdied"));
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		
		event.setCancelled(true);
		
		Bukkit.broadcastMessage(plugin.getChatTheme().applyWithoutTag("§c(§h" + player.getName() + "§c) >>§b " + 
				event.getMessage()));
	}
}
