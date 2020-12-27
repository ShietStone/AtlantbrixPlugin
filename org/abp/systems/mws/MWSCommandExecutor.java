package org.abp.systems.mws;

import java.util.ArrayList;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.logging.PlayerLogger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class MWSCommandExecutor implements CommandExecutor, TabCompleter {

	private Plugin plugin;
	private MultiWorldSystem multiWorldSystem;
	
	MWSCommandExecutor(Plugin plugin, MultiWorldSystem multiWorldSystem) {
		this.plugin = plugin;
		this.multiWorldSystem = multiWorldSystem;
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
					playerLogger.log("§c/§bareaprotection §c[§hname§c]");
					playerLogger.log("§c/§bmultiworld §c[§busage§c/§blist§c]");
					playerLogger.log("§c/§bareaprotection §c[§badd§c/§bremove§c] [§hname§c]");
				} else if(args[0].equalsIgnoreCase("list")) {
					ArrayList<String> worlds = multiWorldSystem.getWorldList();
					
					if(worlds.size() == 0)
						playerLogger.log("§bThere are no worlds on the loading list");
					else {
						playerLogger.log("§bWorlds on the loading list:");
						
						for(String world : worlds)
							playerLogger.log("§h" + world);
					}} else{
					World world = Bukkit.getWorld(args[0]);
					
					if(world != null) {
						player.teleport(world.getSpawnLocation());
						playerLogger.log("§bTeleporting you to the spawn of world §h" + args[0]);
					} else
						playerLogger.log("§eWorld §h" + args[0] + " §enot found");
				}
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("add")) {
					if(!multiWorldSystem.exists(args[1])) {
						multiWorldSystem.add(args[1]);
						playerLogger.log("§bWorld §h" + args[1] + " §badded to the loading list");
					} else
						playerLogger.log("§eWorld §h" + args[1] + " §eis already on the loading list");
				} else if(args[0].equals("remove")) {
					if(multiWorldSystem.exists(args[1])) {
						multiWorldSystem.remove(args[1]);
						playerLogger.log("§bWorld §h" + args[1] + " §bremove from the loading list");
					} else
						playerLogger.log("§eWorld §h" + args[1] + " §eis bot on the loading list");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/multiworld usage §eto view the usage");
			}
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
				if("add".startsWith(arg0))
					result.add("add");
				if("remove".startsWith(arg0))
					result.add("remove");
				if("list".startsWith(arg0))
					result.add("list");
				
				ArrayList<String> worlds = multiWorldSystem.getWorldList();
				
				for(String world : worlds)
					if(world.toLowerCase().startsWith(arg0))
						result.add(world);
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("remove")) {
					String arg1 = args[1].toLowerCase();
					ArrayList<String> worlds = multiWorldSystem.getWorldList();
					
					for(String world : worlds)
						if(world.toLowerCase().startsWith(arg1));
				}
			}
		}
		
		return result;
	}
}
