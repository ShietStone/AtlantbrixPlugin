package org.abp.systems.pls;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.data.DataBase.DataTable;
import org.abp.data.DataBase.SearchCondition;
import org.abp.data.LocationData;
import org.abp.data.NumberUtil;
import org.abp.logging.Logger;
import org.abp.systems.ApplicationSystem;
import org.bukkit.Bukkit;

public class PortalLinkSystem implements ApplicationSystem {

	private static final String TABLE_NAME = "PortalLinkSystemTable";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	private static final String ATTRIBUTE_NAME_X1 = "X1";
	private static final String ATTRIBUTE_NAME_Y1 = "Y1";
	private static final String ATTRIBUTE_NAME_Z1 = "Z1";
	private static final String ATTRIBUTE_NAME_S1 = "S1";
	private static final String ATTRIBUTE_NAME_WORLD_NAME1 = "WorldName1";
	private static final String ATTRIBUTE_NAME_X2 = "X2";
	private static final String ATTRIBUTE_NAME_Y2 = "Y2";
	private static final String ATTRIBUTE_NAME_Z2 = "Z2";
	private static final String ATTRIBUTE_NAME_S2 = "S2";
	private static final String ATTRIBUTE_NAME_WORLD_NAME2 = "WorldName2";
	
	private HashMap<String, PortalLink> portalLinkMap;
	
	public PortalLinkSystem(Plugin plugin) {
		portalLinkMap = new HashMap<>();
		
		plugin.getCommand("portallink").setExecutor(new PLSCommandExecutor(plugin, this));
		plugin.getCommand("portallink").setTabCompleter(new PLSCommandExecutor(plugin, this));
		Bukkit.getPluginManager().registerEvents(new PLSListener(plugin, this), plugin);
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading portal link system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				int x1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X1);
				int y1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y1);
				int z1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z1);
				int s1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_S1);
				int worldName1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_WORLD_NAME1);
				int x2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X2);
				int y2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y2);
				int z2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z2);
				int s2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_S2);
				int worldName2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_WORLD_NAME2);
				
				if(idIndex >= 0 && x1Index >= 0 && y1Index >= 0 && z1Index >= 0 && x2Index >= 0 && y2Index >= 0 && 
						z2Index >= 0 && worldName1Index >= 0 && worldName2Index >= 0 && s1Index >= 0 && s2Index >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});

					for(String[] dataSet : dataSets) {
						float x1 = NumberUtil.toFloat(dataSet[x1Index], 0);
						float y1 = NumberUtil.toFloat(dataSet[y1Index], 0);
						float z1 = NumberUtil.toFloat(dataSet[z1Index], 0);
						int s1 = NumberUtil.toInt(dataSet[s1Index], 0);
						float x2 = NumberUtil.toFloat(dataSet[x2Index], 0);
						float y2 = NumberUtil.toFloat(dataSet[y2Index], 0);
						float z2 = NumberUtil.toFloat(dataSet[z2Index], 0);
						int s2 = NumberUtil.toInt(dataSet[s2Index], 0);
						LocationData a = new LocationData(x1, y1, z1, dataSet[worldName1Index]);
						LocationData b = new LocationData(x2, y2, z2, dataSet[worldName2Index]);
						
						portalLinkMap.put(dataSet[idIndex], new PortalLink(a, s1, b, s2));
					}
					
					logger.log("§bPortal link system loaded");
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
		
		logger.log("§bPortal link system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_X1, ATTRIBUTE_NAME_Y1, 
					ATTRIBUTE_NAME_Z1, ATTRIBUTE_NAME_S1, ATTRIBUTE_NAME_WORLD_NAME1, ATTRIBUTE_NAME_X2, 
					ATTRIBUTE_NAME_Y2, ATTRIBUTE_NAME_Z2, ATTRIBUTE_NAME_S2, ATTRIBUTE_NAME_WORLD_NAME2});
			Set<String> keySet = portalLinkMap.keySet();
			
			for(String id : keySet) {
				PortalLink portalLink = portalLinkMap.get(id);
				LocationData a = portalLink.a;
				LocationData b = portalLink.b;
				
				dataTable.addDataSet(new String[] {id, a.getX() + "", a.getY() + "", a.getZ() + "", 
						portalLink.getSizeA() + "", a.getWorldName(), b.getX() + "", b.getY() + "", b.getZ() + "", 
						portalLink.getSizeB() + "", b.getWorldName()});
			}
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bPortal link system saved");
		} else
			logger.log("Data base is null");
	}
	
	public void setPortalLink(String name, PortalLink portalLink) {
		if(name != null && portalLink != null)
			portalLinkMap.put(name, portalLink);
	}
	
	public void removePortalLink(String name) {
		portalLinkMap.remove(name);
	}
	
	public PortalLink getPortalLink(String name) {
		return portalLinkMap.get(name);
	}
	
	public ArrayList<String> listPortalLinks() {
		ArrayList<String> portalLinks = new ArrayList<>();
		Set<String> keySet = portalLinkMap.keySet();
		
		for(String portalLinkID : keySet)
			portalLinks.add(portalLinkID);
		
		return portalLinks;
	}
	
	public LocationData getLinkedLocation(LocationData locationData) {
		Set<String> portalLinkIDs = portalLinkMap.keySet();
		
		for(String portalLinkID : portalLinkIDs) {
			PortalLink portalLink = portalLinkMap.get(portalLinkID);
			float distanceA = Math.max(Math.abs(locationData.getX() - portalLink.getA().getX()), 
					Math.max(Math.abs(locationData.getY() - portalLink.getA().getY()), 
					Math.abs(locationData.getZ() - portalLink.getA().getZ())));
			float distanceB = Math.max(Math.abs(locationData.getX() - portalLink.getB().getX()), 
					Math.max(Math.abs(locationData.getY() - portalLink.getB().getY()), 
					Math.abs(locationData.getZ() - portalLink.getB().getZ())));
			
			if(distanceA <= portalLink.getSizeA())
				return portalLink.getB();
			if(distanceB <= portalLink.getSizeB())
				return portalLink.getA();
		}
		
		return null;
	}
	
	public static class PortalLink {
		
		private LocationData a;
		private int sizeA;
		private LocationData b;
		private int sizeB;
		
		public PortalLink(LocationData a, int sizeA, LocationData b, int sizeB) {
			if(a != null)
				this.a = a;
			else
				this.a = new LocationData(0, 0, 0, "");
			
			this.sizeA = sizeA;
			
			if(b != null)
				this.b = b;
			else
				this.b = new LocationData(0, 0, 0, "");
			
			this.sizeB = sizeB;
		}
		
		public LocationData getA() {
			return a;
		}
		
		public int getSizeA() {
			return sizeA;
		}
		
		public LocationData getB() {
			return b;
		}
		
		public int getSizeB() {
			return sizeB;
		}
	}
}
