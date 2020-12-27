package org.abp.systems.ss;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.data.DataBase.DataTable;
import org.abp.data.DataBase.SearchCondition;
import org.abp.logging.Logger;
import org.abp.data.LocationData;
import org.abp.data.NumberUtil;
import org.abp.systems.ApplicationSystem;

public class SpawnSystem implements ApplicationSystem {

	private static final String TABLE_NAME = "SpawnSystemTable";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	private static final String ATTRIBUTE_NAME_DEFAULT = "Default";
	private static final String ATTRIBUTE_NAME_X = "X";
	private static final String ATTRIBUTE_NAME_Y = "Y";
	private static final String ATTRIBUTE_NAME_Z = "Z";
	private static final String ATTRIBUTE_NAME_WORLD_NAME = "WorldName";
	
	private HashMap<String, LocationData> spawnMap;
	private String defaultSpawnID;
	
	public SpawnSystem(Plugin plugin) {
		spawnMap = new HashMap<>();
		defaultSpawnID = "default";
		
		plugin.getCommand("spawn").setExecutor(new SSCommandExecutor(plugin, this));
		plugin.getCommand("spawn").setTabCompleter(new SSCommandExecutor(plugin, this));
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading spawn system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				int defaultIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_DEFAULT);
				int xIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X);
				int yIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y);
				int zIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z);
				int worldNameIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_WORLD_NAME);
				
				if(idIndex >= 0 && defaultIndex >= 0 && xIndex >= 0 && yIndex >= 0 && zIndex >= 0 &&
						worldNameIndex >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});
					
					for(String[] dataSet : dataSets) {
						float x = NumberUtil.toFloat(dataSet[xIndex], 0);
						float y = NumberUtil.toFloat(dataSet[yIndex], 0);
						float z = NumberUtil.toFloat(dataSet[zIndex], 0);
						
						spawnMap.put(dataSet[idIndex], new LocationData(x, y, z, dataSet[worldNameIndex]));
						
						if(NumberUtil.toBoolean(dataSet[defaultIndex], false))
							defaultSpawnID = dataSet[idIndex];
					}
					
					logger.log("§bSpawn system loaded");
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
		
		logger.log("§bSpawn system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_DEFAULT, 
					ATTRIBUTE_NAME_X, ATTRIBUTE_NAME_Y, ATTRIBUTE_NAME_Z, ATTRIBUTE_NAME_WORLD_NAME});
			Set<String> keySet = spawnMap.keySet();
			
			for(String id : keySet) {
				LocationData locationData = spawnMap.get(id);
				
				dataTable.addDataSet(new String[] {id, (id.equals(defaultSpawnID)) + "", locationData.getX() + "", 
						locationData.getY() + "", locationData.getZ() + "", locationData.getWorldName()});
			}
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bSpawn system saved");
		} else
			logger.log("§eData base is null");
	}
	
	public void setSpawn(String name, LocationData locationData) {
		if(name != null && locationData != null)
			spawnMap.put(name, locationData);
	}
	
	public void removeSpawn(String name) {
		spawnMap.remove(name);
	}
	
	public LocationData getSpawn(String name) {
		return spawnMap.get(name);
	}
	
	public ArrayList<String> listSpawns() {
		ArrayList<String> spawns = new ArrayList<>();
		Set<String> keySet = spawnMap.keySet();
		
		for(String spawnID : keySet)
			spawns.add(spawnID);
		
		return spawns;
	}
	
	public String getDefaultSpawnID() {
		return defaultSpawnID;
	}
}
