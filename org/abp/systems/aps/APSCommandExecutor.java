package org.abp.systems.aps;

import java.util.ArrayList;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.logging.PlayerLogger;
import org.abp.systems.aps.AreaProtectionSystem.ProtectedArea;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class APSCommandExecutor implements CommandExecutor, TabCompleter {

	private Plugin plugin;
	private AreaProtectionSystem areaProtectionSystem;
	
	APSCommandExecutor(Plugin plugin, AreaProtectionSystem areaProtectionSystem) {
		this.plugin = plugin;
		this.areaProtectionSystem = areaProtectionSystem;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
			
			if(!player.isOp()) {
				playerLogger.log("§eYou are not allowed to do that");
				return true;
			}
			
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("usage")) {
					playerLogger.log("§bUsage:");
					playerLogger.log("§c/§bareaprotection §c[§busage§c/§blist§c/§bload§c/§bsave§c]");
					playerLogger.log("§c/§bareaprotection §c[§bdelete§c/§bview§c] [§hname§c]");
					playerLogger.log("§c/§bareaprotection §c[§bset§c] [§hname§c] [§bA§c/§bB§c]");
				} else if(args[0].equalsIgnoreCase("list")) {
					ArrayList<String> protectedAreaIDs = areaProtectionSystem.listProtectedAreas();
					
					if(protectedAreaIDs.size() == 0)
						playerLogger.log("§bThere are no protected areas");
					else {
						playerLogger.log("§bProtected areas:");
						
						for(String protectedAreaID : protectedAreaIDs)
							playerLogger.log("§h" + protectedAreaID);
					}
				} else if(args[0].equalsIgnoreCase("load"))
					areaProtectionSystem.load(playerLogger, plugin.getDataBase());
				else if(args[0].equalsIgnoreCase("save"))
					areaProtectionSystem.save(playerLogger, plugin.getDataBase());
				else
					playerLogger.log("§eCommand not used correctly. Use §h/areaprotection usage §eto view the usage");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("delete")) {
					areaProtectionSystem.removeProtectedArea(args[1]);
					playerLogger.log("§bProtected area §h" + args[1] + "§b deleted");
				} else if(args[0].equalsIgnoreCase("view")) {
					ProtectedArea protectedArea = areaProtectionSystem.getProtectedArea(args[1]);
					
					if(protectedArea != null) {
						playerLogger.log("Protected area §h" + args[1] + "§b:");
						playerLogger.log("§bAX: §h" + protectedArea.getA().getX());
						playerLogger.log("§bAY: §h" + protectedArea.getA().getY());
						playerLogger.log("§bAZ: §h" + protectedArea.getA().getZ());
						playerLogger.log("§bBX: §h" + protectedArea.getB().getX());
						playerLogger.log("§bBY: §h" + protectedArea.getB().getY());
						playerLogger.log("§bBZ: §h" + protectedArea.getB().getZ());
						playerLogger.log("§bWorld: §h" + protectedArea.getA().getWorldName());
					} else
						playerLogger.log("§eProtected area §h" + args[1] + "§e not found");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/areaprotection usage §eto view the usage");
			} else if(args.length == 3 && args[0].equalsIgnoreCase("set")) {
				ProtectedArea protectedArea = areaProtectionSystem.getProtectedArea(args[1]);
				
				if(protectedArea != null) {
					LocationData a = protectedArea.getA();
					LocationData b = protectedArea.getB();
					
					if(args[2].equalsIgnoreCase("a"))
						a = new LocationData(player.getLocation());
					else if(args[2].equalsIgnoreCase("b"))
						b = new LocationData(player.getLocation());
					else {
						playerLogger.log("§eCommand not used correctly. Use §h/areaprotection usage §eto view the usage");
						return true;
					}
					
					if(a.getWorldName().equals(b.getWorldName())) {
						areaProtectionSystem.setProtectedArea(args[1], new ProtectedArea(a, b));
						playerLogger.log("§bLocation §h" + (args[2].equalsIgnoreCase("a") ? "A" : "B") + "§b of protected "
								+ "area §h" + args[1] + " §bset to your location");
					} else
						playerLogger.log("§eThe locations need to be in the same world");
				} else {
					areaProtectionSystem.setProtectedArea(args[1], new ProtectedArea(
							new LocationData(player.getLocation()), new LocationData(player.getLocation())));
					
					playerLogger.log("§bProtected area §h" + args[1] + "§b created and set to your location");
				}
			} else
				playerLogger.log("§eCommand not used correctly. Use §h/areaprotection usage §eto view the usage");
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
				if("load".startsWith(arg0))
					result.add("load");
				if("save".startsWith(arg0))
					result.add("save");
				if("delete".startsWith(arg0))
					result.add("delete");
				if("view".startsWith(arg0))
					result.add("view");
				if("set".startsWith(arg0))
					result.add("set");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("set") || 
						args[0].equalsIgnoreCase("delete")) {
					ArrayList<String> protectedAreaIDs = areaProtectionSystem.listProtectedAreas();
					for(String protectedAreaID : protectedAreaIDs)
						if(protectedAreaID.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(protectedAreaID);
				}		
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("set") && args[2].length() == 0) {
					result.add("A");
					result.add("B");
				}
			}		
		}
		
		return result;
	}
}
