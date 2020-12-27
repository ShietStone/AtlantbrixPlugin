package org.abp.systems.pls;

import java.util.ArrayList;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.data.LocationData;
import org.abp.data.NumberUtil;
import org.abp.logging.PlayerLogger;
import org.abp.systems.pls.PortalLinkSystem.PortalLink;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class PLSCommandExecutor implements CommandExecutor, TabCompleter {
	
	private Plugin plugin;
	private PortalLinkSystem portalLinkSystem;
	
	public PLSCommandExecutor(Plugin plugin, PortalLinkSystem portalLinkSystem) {
		this.plugin = plugin;
		this.portalLinkSystem = portalLinkSystem;
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
					playerLogger.log("§c/§bportallink §c[§busage§c/§blist§c/§bload§c/§bsave§c]");
					playerLogger.log("§c/§bportallink §c[§bdelete§c/§bview§c] [§hname§c]");
					playerLogger.log("§c/§bportallink §c[§bset§c] [§hname§c] [§bA§c/§bB§c] [§hsize§c]");
				} else if(args[0].equalsIgnoreCase("list")) {
					ArrayList<String> portalLinkIDs = portalLinkSystem.listPortalLinks();
					
					if(portalLinkIDs.size() == 0)
						playerLogger.log("§bThere are no portal links");
					else {
						playerLogger.log("§bPortal links:");
						
						for(String portalLinkID : portalLinkIDs)
							playerLogger.log("§h" + portalLinkID);
					}
				} else if(args[0].equalsIgnoreCase("load"))
					portalLinkSystem.load(playerLogger, plugin.getDataBase());
				else if(args[0].equalsIgnoreCase("save"))
					portalLinkSystem.save(playerLogger, plugin.getDataBase());
				else
					playerLogger.log("§eCommand not used correctly. Use §h/portallink usage §eto view the usage");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("delete")) {
					portalLinkSystem.removePortalLink(args[1]);
					playerLogger.log("§bPortal link §h" + args[1] + "§b deleted");
				} else if(args[0].equalsIgnoreCase("view")) {
					PortalLink portalLink = portalLinkSystem.getPortalLink(args[1]);
					
					if(portalLink != null) {
						playerLogger.log("Portal link §h" + args[1] + "§b:");
						playerLogger.log("§bAX: §h" + portalLink.getA().getX());
						playerLogger.log("§bAY: §h" + portalLink.getA().getY());
						playerLogger.log("§bAZ: §h" + portalLink.getA().getZ());
						playerLogger.log("§bASize: §h" + portalLink.getSizeA());
						playerLogger.log("§bAWorld: §h" + portalLink.getA().getWorldName());
						playerLogger.log("§bBX: §h" + portalLink.getB().getX());
						playerLogger.log("§bBY: §h" + portalLink.getB().getY());
						playerLogger.log("§bBZ: §h" + portalLink.getB().getZ());
						playerLogger.log("§bBSize: §h" + portalLink.getSizeB());
						playerLogger.log("§bBWorld: §h" + portalLink.getB().getWorldName());
					} else
						playerLogger.log("§ePortal link §h" + args[1] + "§e not found");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/portallink usage §eto view the usage");
			} else if(args.length == 4 && args[0].equalsIgnoreCase("set")) {
				PortalLink portalLink = portalLinkSystem.getPortalLink(args[1]);
				int size = NumberUtil.toInt(args[3], 0);
				
				if(portalLink != null) {
					LocationData a = portalLink.getA();
					LocationData b = portalLink.getB();
					int sizeA = portalLink.getSizeA();
					int sizeB = portalLink.getSizeB();
					
					if(args[2].equalsIgnoreCase("a")) {
						a = new LocationData(player.getLocation());
						sizeA = size;
					} else if(args[2].equalsIgnoreCase("b")) {
						b = new LocationData(player.getLocation());
						sizeB = size;
					} else {
						playerLogger.log("§eCommand not used correctly. Use §h/portallink usage §eto view the usage");
						return true;
					}
					
					portalLinkSystem.setPortalLink(args[1], new PortalLink(a, sizeA, b, sizeB));
					playerLogger.log("§bLocation §h" + (args[2].equalsIgnoreCase("a") ? "A" : "B") + "§b of portal "
							+ "link §h" + args[1] + " §bset to your location");
				} else {
					portalLinkSystem.setPortalLink(args[1], new PortalLink(
							new LocationData(player.getLocation()), size, new LocationData(player.getLocation()), size));
					
					playerLogger.log("§bPortal link §h" + args[1] + "§b created and set to your location");
				}
			} else
				playerLogger.log("§eCommand not used correctly. Use §h/portallink usage §eto view the usage");
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
					ArrayList<String> protectedAreaIDs = portalLinkSystem.listPortalLinks();
					for(String protectedAreaID : protectedAreaIDs)
						if(protectedAreaID.toLowerCase().startsWith(args[1].toLowerCase()))
							result.add(protectedAreaID);
				}		
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("set") && args[2].length() == 0) {
					result.add("A");
					result.add("B");
				}
			} else if(args.length == 4)
				if(args[3].length() == 0)
					result.add("0");
		}
		
		return result;
	}
}
