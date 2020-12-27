package org.abp.systems.ss;

import java.util.ArrayList;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.logging.PlayerLogger;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class SSCommandExecutor implements CommandExecutor, TabCompleter  {

	private Plugin plugin;
	private SpawnSystem spawnSystem;
	
	SSCommandExecutor(Plugin plugin, SpawnSystem spawnSystem) {
		this.plugin = plugin;
		this.spawnSystem = spawnSystem;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
			
			if(args.length < 2) {
				String targetID = spawnSystem.getDefaultSpawnID();
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("usage")) {
						playerLogger.log("§bUsage:");
						playerLogger.log("§c/§bspawn");
						playerLogger.log("§c/§bspawn §c[§hname§c]");
						playerLogger.log("§c/§bspawn §c[§busage§c/§blist§c/§bload§c/§bsave§c]");
						playerLogger.log("§c/§bspawn §c[§bset§c/§bdelete§c/§bview§c] [§hname§c]");
						return true;
					} else if(args[0].equalsIgnoreCase("list")) {
						ArrayList<String> spawnIDs = spawnSystem.listSpawns();
						
						if(spawnIDs.size() == 0)
							playerLogger.log("§bThere are no spawns");
						else {
							playerLogger.log("§bSpawns:");
							
							for(String spawnID : spawnIDs)
								playerLogger.log("§h" + spawnID);
						}
						
						return true;
					} else if(args[0].equalsIgnoreCase("load")) {
						if(player.isOp())
							spawnSystem.load(playerLogger, plugin.getDataBase());
						else
							playerLogger.log("§eYou are not allowed to do that");
						
						return true;
					} else if(args[0].equalsIgnoreCase("save")) {
						if(player.isOp())
							spawnSystem.save(playerLogger, plugin.getDataBase());
						else
							playerLogger.log("§eYou are not allowed to do that");
						
						return true;
					}
					
					targetID = args[0];
				}
				
				LocationData locationData = spawnSystem.getSpawn(targetID);
				
				if(locationData != null) {
					Location location = locationData.toLocation();
					
					if(location != null) {
						playerLogger.log("§bSending you to spawn §h" + targetID);
						player.teleport(location);
					} else
						playerLogger.log("§eSpawn §h" + targetID + "§e cannot be loaded");
				} else
					playerLogger.log("§eSpawn §h" + targetID + "§e not found");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("set")) {
					if(player.isOp()) {
						LocationData locationData = new LocationData(player.getLocation());
						spawnSystem.setSpawn(args[1], locationData);
						playerLogger.log("§bSpawn §h" + args[1] + "§b set to your location");
					} else
						playerLogger.log("§eYou are not allowed to do that");
				} else if(args[0].equalsIgnoreCase("delete")) {
					if(player.isOp()) {
						spawnSystem.removeSpawn(args[1]);
						playerLogger.log("§bSpawn §h" + args[1] + "§b deleted");
					} else
						playerLogger.log("§eYou are not allowed to do that");
				} else if(args[0].equalsIgnoreCase("view")) {
					LocationData locationData = spawnSystem.getSpawn(args[1]);
					
					if(locationData != null) {
						playerLogger.log("§bSpawn §h" + args[1] +"§b:");
						playerLogger.log("§bX: §h" + locationData.getX());
						playerLogger.log("§bY: §h" + locationData.getY());
						playerLogger.log("§bZ: §h" + locationData.getZ());
						playerLogger.log("§bWorld: §h" + locationData.getWorldName());
						playerLogger.log("§bDefault: §h" + spawnSystem.getDefaultSpawnID().equals(args[1]));
					} else
						playerLogger.log("§eSpawn §h" + args[1] + "§e not found");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/spawn usage §eto view the usage");			
			} else
				playerLogger.log("§eCommand not used correctly. Use §h/spawn usage §eto view the usage");
		} else
			plugin.log("§eNon-Player tried to execute a player command");
		
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> result = new ArrayList<>();
		
		if(sender instanceof Player) {
			if(args.length == 1) {
				String arg0 = args[0].toLowerCase();
				
				if("usage".startsWith(arg0))
					result.add("usage");
				if("list".startsWith(arg0))
					result.add("list");
				if("view".startsWith(arg0))
					result.add("view");
				if("load".startsWith(arg0))
					result.add("load");
				if("save".startsWith(arg0))
					result.add("save");
				if("set".startsWith(arg0))
					result.add("set");
				if("delete".startsWith(arg0))
					result.add("delete");
				
				ArrayList<String> spawnList = spawnSystem.listSpawns();
				for(String spawn : spawnList)
					if(spawn.toLowerCase().startsWith(arg0))
						result.add(spawn);
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("set") || 
						args[0].equalsIgnoreCase("delete")) {
					ArrayList<String> spawnList = spawnSystem.listSpawns();
					for(String spawn : spawnList)
						if(spawn.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(spawn);
				}
			}
		}
		
		return result;
	}
}
