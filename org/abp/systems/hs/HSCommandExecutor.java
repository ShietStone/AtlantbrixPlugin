package org.abp.systems.hs;

import java.util.ArrayList;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.logging.PlayerLogger;
import org.abp.systems.hs.HomeSystem.HomeContainer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class HSCommandExecutor implements CommandExecutor, TabCompleter {

	private Plugin plugin;
	private HomeSystem homeSystem;
	
	HSCommandExecutor(Plugin plugin, HomeSystem homeSystem) {
		this.plugin = plugin;
		this.homeSystem = homeSystem;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lable, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
			String uuid = player.getUniqueId().toString();
			HomeContainer homeContainer = homeSystem.getHomeContainer(uuid);
			
			if(args.length < 2) {
				String targetID = homeContainer != null ? homeContainer.getDefaultHomeID() : "";
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("usage")) {
						playerLogger.log("§bUsage:");
						playerLogger.log("§c/§bhome");
						playerLogger.log("§c/§bhome §c[§hname§c]");
						playerLogger.log("§c/§bhome §c[§busage§c/§blist§c/§bload§c/§bsave§c]");
						playerLogger.log("§c/§bhome §c[§bset§c/§bdelete§c/§bview§c] [§hname§c]");
						return true;
					} else if(args[0].equalsIgnoreCase("list")) {
						if(homeContainer != null) {
							ArrayList<String> homeIDs = homeContainer.listHomes();
							
							if(homeIDs.size() == 0)
								playerLogger.log("§bYou do not have any homes");
							else {
								playerLogger.log("§bYour homes:");
								
								for(String homeID : homeIDs)
									playerLogger.log("§h" + homeID);
							}
						} else
							playerLogger.log("§bYou do not have any homes");
						
						return true;
					} else if(args[0].equalsIgnoreCase("load")) {
						if(player.isOp())
							homeSystem.load(playerLogger, plugin.getDataBase());
						else
							playerLogger.log("§eYou are not allowed to do that");
						
						return true;
					} else if(args[0].equals("save")) {
						if(player.isOp())
							homeSystem.save(playerLogger, plugin.getDataBase());
						else
							playerLogger.log("§eYou are not allowed to do that");
						
						return true;
					}
					
					targetID = args[0];
				}
				
				if(homeContainer != null) {
					LocationData locationData = homeContainer.getHome(targetID);
					
					if(locationData != null) {
						Location location = locationData.toLocation();
						
						if(location != null) {
							playerLogger.log("§bSending you to home §h" + targetID);
							player.teleport(location);
						} else
							playerLogger.log("§eHome §h" + targetID + "§e cannot be loaded");
					} else
						playerLogger.log("§eHome §h" + targetID + "§e not found");
				} else
					playerLogger.log("§eYou do not have any homes");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("set")) {
					if(homeContainer == null) {
						homeContainer = new HomeContainer("default");
						homeSystem.setHomeContainer(uuid, homeContainer);
					}
					
					homeContainer.setHome(args[1], new LocationData(player.getLocation()));
					playerLogger.log("§bHome §h" + args[1] + " §bset to your location");
				} else if(homeContainer != null) {
					if(args[0].equalsIgnoreCase("delete")) {
						homeContainer.removeHome(args[1]);
						playerLogger.log("§bHome §h" + args[1] + " §bdeleted");
					} else if(args[0].equalsIgnoreCase("view")) {
						LocationData locationData = homeContainer.getHome(args[1]);
						
						if(locationData != null) {
							playerLogger.log("§bHome §h" + args[1] +"§b:");
							playerLogger.log("§bX: §h" + locationData.getX());
							playerLogger.log("§bY: §h" + locationData.getY());
							playerLogger.log("§bZ: §h" + locationData.getZ());
							playerLogger.log("§bWorld: §h" + locationData.getWorldName());
							playerLogger.log("§bDefault: §h" + homeContainer.getDefaultHomeID().equals(args[1]));
						} else
							playerLogger.log("§eHome §h" + args[1] + "§e not found");
					} else
						playerLogger.log("§eCommand not used correctly. Use §h/home usage §eto view the usage");
				} else
					playerLogger.log("§eYou do not have any homes");
			} else
				playerLogger.log("§eCommand not used correctly. Use §h/home usage §eto view the usage");
		} else
			plugin.log("§eNon-Player tried to execute a player command");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lable, String[] args) {
		List<String> result = new ArrayList<>();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			HomeContainer homeContainer = homeSystem.getHomeContainer(player.getUniqueId().toString());
			
			if(args.length == 1) {
				String arg0 = args[0].toLowerCase();
				
				if("usage".startsWith(arg0))
					result.add("usage");
				if("list".startsWith(arg0))
					result.add("list");
				if("load".startsWith(arg0))
					result.add("load");
				if("save".startsWith(arg0))
					result.add("save");
				if("set".startsWith(arg0))
					result.add("set");
				if("delete".startsWith(arg0))
					result.add("delete");
				if("view".startsWith(arg0))
					result.add("view");
				
				if(homeContainer != null) {
					ArrayList<String> homeList = homeContainer.listHomes();
					for(String home : homeList)
						if(home.toLowerCase().startsWith(arg0))
							result.add(home);
				}
			} else if(args.length == 2) {
				if((args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("set") || 
						args[0].equalsIgnoreCase("delete")) && homeContainer != null) {
					ArrayList<String> homeList = homeContainer.listHomes();
					for(String home : homeList)
						if(home.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(home);
				}
			}
		}
		
		return result;
	}
}
