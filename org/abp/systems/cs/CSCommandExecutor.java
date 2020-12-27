package org.abp.systems.cs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.abp.core.Plugin;
import org.abp.data.PlayerUtil;
import org.abp.logging.PlayerLogger;
import org.abp.systems.cs.ClanSystem.Clan;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class CSCommandExecutor implements CommandExecutor, TabCompleter {
	
	private Plugin plugin;
	private ClanSystem clanSystem;
	
	public CSCommandExecutor(Plugin plugin, ClanSystem clanSystem) {
		this.plugin = plugin;
		this.clanSystem = clanSystem;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player player = (Player) sender;
			PlayerLogger playerLogger = new PlayerLogger(player, plugin.getChatTheme());
			String uuid = player.getUniqueId().toString();
			
			if(args.length == 1) {
				if(args[0].equalsIgnoreCase("usage")) {
					playerLogger.log("§bUsage:");
					playerLogger.log("§c/§bclan §c[§busage§c/§blist§c/§blist-all§c/§bload§c/§bsave§c]");
					playerLogger.log("§c/§bclan §c[§bjoin§c/§bleave§c/§blist-members§c] [§hname§c]");
					playerLogger.log("§c/§bclan §c[§bpromote§c/§bdemote§c/§binvite§c] [§hname§c] [§hplayer§c]");
				} else if(args[0].equalsIgnoreCase("list")) {
					ArrayList<String> clans = clanSystem.listMemberedClans(uuid);
					
					if(clans.size() == 0)
						playerLogger.log("§bYou are not member of any clan");
					else {
						playerLogger.log("§bClans you are a member of:");
						
						for(String clan : clans)
							playerLogger.log("§h" + clan);
					}
				} else if(args[0].equalsIgnoreCase("list-all")) {
					ArrayList<String> clans = clanSystem.listClans();
					
					if(clans.size() == 0)
						playerLogger.log("§bThere are no clans");
					else {
						playerLogger.log("§bClans:");
						
						for(String clan : clans)
							playerLogger.log("§h" + clan);
					}
				} else if(args[0].equalsIgnoreCase("load")) {
					if(player.isOp())
						clanSystem.save(playerLogger, plugin.getDataBase());
					else
						playerLogger.log("§eYou are not allowed to do that");
				} else if(args[0].equalsIgnoreCase("save")) {
					if(player.isOp())
						clanSystem.save(playerLogger, plugin.getDataBase());
					else
						playerLogger.log("§eYou are not allowed to do that");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/clan usage §eto view the usage");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("join")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						if(player.isOp() || clan.isInvited(uuid)) {
							clan.setMemberStatus(uuid, false);
							clan.removeInvite(uuid);
							playerLogger.log("§bYou joined clan §h" + args[1]);
						} else
							playerLogger.log("§eYou are not invited to clan §h" + args[1]);
					} else {
						clan = new Clan();
						clan.setMemberStatus(uuid, true);
						clanSystem.setClan(args[1], clan);
						playerLogger.log("§bYou have created clan §h" + args[1] + " §band joined as admin");
					}
				} else if(args[0].equalsIgnoreCase("leave")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						clan.removeMember(uuid);
						
						if(clan.listMemberUUIDs().size() == 0)
							clanSystem.removeClan(args[1]);
						
						playerLogger.log("§bYou left clan §h" + args[1]);
					} else
						playerLogger.log("§eClan §h" + args[1] + " §ewas not found");
				} else if(args[0].equalsIgnoreCase("list-members")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						playerLogger.log("§bMembers of clan §h" + args[1] + "§b:");
						
						ArrayList<String> memberUUIDs = clan.listMemberUUIDs();
						
						for(String memberUUID : memberUUIDs)
							playerLogger.log("§h" + PlayerUtil.uuidToName(memberUUID) + " §b(§h" + 
						(clan.getMemberStatus(memberUUID) ? "admin" : "member") + "§b)");
					} else
						playerLogger.log("§cClan §h" + args[1] + "§ewas not found");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/clan usage §eto view the usage");		
			} else if(args.length == 3) {
				if(args[0].equalsIgnoreCase("promote")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						if(clan.getMemberStatus(uuid)) {
							String targetUUID = PlayerUtil.nameToUUID(args[2]);
							
							if(targetUUID != null) {
								if(clan.isMember(targetUUID)) {
									clan.setMemberStatus(targetUUID, true);
									playerLogger.log("§bPlayer §h" + args[2] + " §bpromoted in clan §h" + args[1]);
								} else
									playerLogger.log("§bPlayer §h" + args[2] + " §bis not a member of clan §h" + args[1]);
							} else
								playerLogger.log("§ePlayer §h" + args[2] + " §enot found");
						} else
							playerLogger.log("§eYou are not admin of clan §h" + args[1]);
					} else
						playerLogger.log("§eClan §h" + args[1] + " §enot found");
				} else if(args[0].equalsIgnoreCase("demote")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						if(clan.getMemberStatus(uuid)) {
							String targetUUID = PlayerUtil.nameToUUID(args[2]);
							
							if(targetUUID != null) {								
								if(clan.isMember(targetUUID)) {
									clan.setMemberStatus(targetUUID, false);
									playerLogger.log("§bPlayer §h" + args[2] + " §bdemoted in clan §h" + args[1]);
								} else
									playerLogger.log("§bPlayer §h" + args[2] + " §bis not a member of clan §h" + args[1]);
							} else
								playerLogger.log("§ePlayer §h" + args[2] + " §enot found");
						} else
							playerLogger.log("§eYou are not admin of clan §h" + args[1]);
					} else
						playerLogger.log("§eClan §h" + args[1] + " §enot found");
				} else if(args[0].equalsIgnoreCase("invite")) {
					Clan clan = clanSystem.getClan(args[1]);
					
					if(clan != null) {
						String targetUUID = PlayerUtil.nameToUUID(args[2]);
						
						if(targetUUID != null) {
							clan.addInvite(targetUUID);
							playerLogger.log("§bPlayer §h" + args[2] + " §binvited to clan §h" + args[1]);
						} else
							playerLogger.log("§ePlayer §h" + args[2] + " §enot found");
					} else
						playerLogger.log("§eClan §h" + args[1] + " §enot found");
				} else
					playerLogger.log("§eCommand not used correctly. Use §h/clan usage §eto view the usage");				
			} else
				playerLogger.log("§eCommand not used correctly. Use §h/clan usage §eto view the usage");	
		} else
			plugin.log("§eNon-Player tried to execute a player command");
		
		return true;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> result = new ArrayList<>();
		
		if(sender instanceof Player) {
			Player player = (Player) sender;
			String uuid = player.getUniqueId().toString();
			
			if(args.length == 1) {
				String arg0 = args[0].toLowerCase();
				
				if("usage".startsWith(arg0))
					result.add("usage");
				if("list".startsWith(arg0))
					result.add("list");
				if("list-all".startsWith(arg0))
					result.add("list-all");
				if("list-members".startsWith(arg0))
					result.add("list-members");
				if("load".startsWith(arg0))
					result.add("load");
				if("save".startsWith(arg0))
					result.add("save");
				if("join".startsWith(arg0))
					result.add("join");
				if("leave".startsWith(arg0))
					result.add("leave");
				if("promote".startsWith(arg0))
					result.add("promote");
				if("demote".startsWith(arg0))
					result.add("demote");
				if("invite".startsWith(arg0))
					result.add("invite");
			} else if(args.length == 2) {
				if(args[0].equalsIgnoreCase("join")) {
					String arg1 = args[1].toLowerCase();
					ArrayList<String> clans = clanSystem.listClans();
					
					for(String clan : clans)
						if(clan.toLowerCase().startsWith(arg1))
							result.add(clan);
				} else if(args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("promote") || 
						args[0].equalsIgnoreCase("demote") || args[0].equalsIgnoreCase("invite") || 
						args[0].equalsIgnoreCase("list-members")) {
					String arg1 = args[1].toLowerCase();
					ArrayList<String> clans = clanSystem.listMemberedClans(uuid);
					
					for(String clan : clans)
						if(clan.toLowerCase().startsWith(arg1))
							result.add(clan);	
				}
			} else if(args.length == 3) {
				if((args[0].equalsIgnoreCase("promote") || args[0].equalsIgnoreCase("demote") || 
						args[0].equalsIgnoreCase("invite")) && clanSystem.getClan(args[1]) != null) {
					String arg2 = args[2].toLowerCase();
					Collection<? extends Player> players = Bukkit.getOnlinePlayers();
					
					for(Player targetPlayer : players)
						if(targetPlayer.getName().toLowerCase().startsWith(arg2))
							result.add(targetPlayer.getName());
						
				}
			}
		}
		
		return result;
	}
}
