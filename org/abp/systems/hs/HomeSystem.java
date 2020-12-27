package org.abp.systems.hs;

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

public class HomeSystem implements ApplicationSystem {

	private static String TABLE_NAME = "HomeSystemTable";
	private static final String ATTRIBUTE_NAME_UUID = "UUID";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	private static final String ATTRIBUTE_NAME_DEFAULT = "Default";
	private static final String ATTRIBUTE_NAME_X = "X";
	private static final String ATTRIBUTE_NAME_Y = "Y";
	private static final String ATTRIBUTE_NAME_Z = "Z";
	private static final String ATTRIBUTE_NAME_WORLD_NAME = "WorldName";
	
	private HashMap<String, HomeContainer> homeContainerMap;
	
	public HomeSystem(Plugin plugin) {
		homeContainerMap = new HashMap<>();
		
		plugin.getCommand("home").setExecutor(new HSCommandExecutor(plugin, this));
		plugin.getCommand("home").setTabCompleter(new HSCommandExecutor(plugin, this));
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading home system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int uuidIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_UUID);
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				int defaultIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_DEFAULT);
				int xIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X);
				int yIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y);
				int zIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z);
				int worldNameIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_WORLD_NAME);
				
				if(uuidIndex >= 0 && idIndex >= 0 && defaultIndex >= 0 && xIndex >= 0 && yIndex >= 0 && zIndex >= 0 &&
						worldNameIndex >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});
					
					for(String[] dataSet : dataSets) {
						HomeContainer homeContainer = homeContainerMap.get(dataSet[uuidIndex]);
						
						if(homeContainer == null) {
							homeContainer = new HomeContainer("default");
							homeContainerMap.put(dataSet[uuidIndex], homeContainer);
						}
						
						float x = NumberUtil.toFloat(dataSet[xIndex], 0);
						float y = NumberUtil.toFloat(dataSet[yIndex], 0);
						float z = NumberUtil.toFloat(dataSet[zIndex], 0);
						
						homeContainer.setHome(dataSet[idIndex], new LocationData(x, y, z, dataSet[worldNameIndex]));
						
						if(NumberUtil.toBoolean(dataSet[defaultIndex], false))
							homeContainer.defaultHomeID = dataSet[idIndex];
					}
					
					logger.log("§bHome system loaded");
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
		
		logger.log("§bHome system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_UUID, ATTRIBUTE_NAME_ID, 
					ATTRIBUTE_NAME_DEFAULT, ATTRIBUTE_NAME_X, ATTRIBUTE_NAME_Y, ATTRIBUTE_NAME_Z, 
					ATTRIBUTE_NAME_WORLD_NAME});
			Set<String> keySet = homeContainerMap.keySet();
			
			for(String uuid : keySet) {
				HomeContainer homeContainer = homeContainerMap.get(uuid);
				Set<String> keySet2 = homeContainer.homeMap.keySet();
				
				for(String id : keySet2) {
					LocationData locationData = homeContainer.homeMap.get(id);
				
					dataTable.addDataSet(new String[] {uuid, id, (id.equals(homeContainer.getDefaultHomeID())) + "", 
							locationData.getX() + "", locationData.getY() + "", locationData.getZ() + "", 
							locationData.getWorldName()});
				}
			}
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bHome system saved");
		} else
			logger.log("§eData base is null");
	}
	
	public void setHomeContainer(String uuid, HomeContainer homeContainer) {
		if(uuid != null && homeContainer != null)
			homeContainerMap.put(uuid, homeContainer);
	}
	
	public HomeContainer getHomeContainer(String uuid) {
		return homeContainerMap.get(uuid);
	}
	
	public static class HomeContainer {
		
		private HashMap<String, LocationData> homeMap;
		private String defaultHomeID;
		
		public HomeContainer(String defaultHomeID) {
			homeMap = new HashMap<>();
			
			if(defaultHomeID != null)
				this.defaultHomeID = defaultHomeID;
			else
				this.defaultHomeID = "";
		}
		
		public void setHome(String name, LocationData locationData) {
			if(name != null && locationData != null)
				homeMap.put(name, locationData);
		}
		
		public void removeHome(String name) {
			homeMap.remove(name);
		}
		
		public LocationData getHome(String name) {
			return homeMap.get(name);
		}
		
		public ArrayList<String> listHomes() {
			ArrayList<String> homes = new ArrayList<>();
			Set<String> keySet = homeMap.keySet();
			
			for(String homeID : keySet)
				homes.add(homeID);
			
			return homes;
		}
		
		public String getDefaultHomeID() {
			return defaultHomeID;
		}
	}
}
