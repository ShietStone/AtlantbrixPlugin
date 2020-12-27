package org.abp.systems.cs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.data.DataBase.DataTable;
import org.abp.data.DataBase.SearchCondition;
import org.abp.data.NumberUtil;
import org.abp.logging.Logger;
import org.abp.systems.ApplicationSystem;

public class ClanSystem implements ApplicationSystem {

	private static String TABLE_NAME = "ClanSystemTable";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	private static final String ATTRIBUTE_NAME_UUID = "UUID";
	private static final String ATTRIBUTE_NAME_STATUS = "Status";

	private HashMap<String, Clan> clanMap;
	
	public ClanSystem(Plugin plugin) {
		clanMap = new HashMap<>();
		
		plugin.getCommand("clan").setExecutor(new CSCommandExecutor(plugin, this));
		plugin.getCommand("clan").setTabCompleter(new CSCommandExecutor(plugin, this));
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading clan system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				int uuidIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_UUID);
				int statusIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_STATUS);
				
				if(idIndex >= 0 && uuidIndex >= 0 && statusIndex >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});
					
					for(String[] dataSet : dataSets) {
						Clan clan = clanMap.get(dataSet[idIndex]);
						
						if(clan == null) {
							clan = new Clan();
							clanMap.put(dataSet[idIndex], clan);
						}
						
						clan.members.put(dataSet[uuidIndex], NumberUtil.toBoolean(dataSet[statusIndex], false));
					}
					
					logger.log("§bClan system loaded");
				} else
					logger.log("§eTable has missing columns");
			} else
				logger.log("§eTable not found");
		} else
			logger.log("§eData base is null");
	}

	@Override
	public void save(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bClan system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_UUID, 
					ATTRIBUTE_NAME_STATUS});
			Set<String> keySet = clanMap.keySet();
			
			for(String id : keySet) {
				HashMap<String, Boolean> clan = clanMap.get(id).members;
				Set<String> keySet2 = clan.keySet();
				
				for(String uuid : keySet2)
					dataTable.addDataSet(new String[] {id, uuid, "" + clan.get(uuid)});
			}
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bClan system saved");
		} else
			logger.log("§eData base is null");
	}
	
	public void setClan(String name, Clan clan) {
		if(name != null && clan != null)
			clanMap.putIfAbsent(name, clan);
	}
	
	public void removeClan(String name) {
		clanMap.remove(name);
	}
	
	public Clan getClan(String name) {
		return clanMap.get(name);
	}
	
	public ArrayList<String> listClans() {
		ArrayList<String> clans = new ArrayList<>();
		Set<String> keySet = clanMap.keySet();
		
		for(String clanID : keySet)
			clans.add(clanID);
		
		return clans;
	}
	
	public ArrayList<String> listMemberedClans(String uuid) {
		ArrayList<String> clans = new ArrayList<>();
		Set<String> keySet = clanMap.keySet();
		
		for(String clanID : keySet)
			if(clanMap.get(clanID).isMember(uuid))
				clans.add(clanID);
		
		return clans;
	}
	
	public static class Clan {
		
		private HashMap<String, Boolean> members;
		private ArrayList<String> invites;

		public Clan() {
			members = new HashMap<>();
			invites = new ArrayList<>();
		}
		
		public void setMemberStatus(String uuid, boolean status) {
			if(uuid != null)
				members.put(uuid, status);
		}
		
		public void removeMember(String uuid) {
			members.remove(uuid);
		}
		
		public void addInvite(String uuid) {
			if(uuid != null)
				invites.add(uuid);
		}
		
		public void removeInvite(String uuid) {
			invites.remove(uuid);
		}
		
		public boolean isInvited(String uuid) {
			return invites.contains(uuid);
		}
		
		public boolean getMemberStatus(String uuid) {
			Boolean status = members.get(uuid);
			
			if(status != null)
				return status;
			else
				return false;
		}
		
		public boolean isMember(String uuid) {
			return members.containsKey(uuid);
		}
		
		public ArrayList<String> listMemberUUIDs() {
			ArrayList<String> memberUUIDs = new ArrayList<>();
			
			for(String uuid : members.keySet())
				memberUUIDs.add(uuid);
			
			return memberUUIDs;
		}
	}
}
