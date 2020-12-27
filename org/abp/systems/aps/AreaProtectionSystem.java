package org.abp.systems.aps;

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

public class AreaProtectionSystem implements ApplicationSystem {

	private static final String TABLE_NAME = "AreaProtectionSystemTable";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	private static final String ATTRIBUTE_NAME_X1 = "X1";
	private static final String ATTRIBUTE_NAME_Y1 = "Y1";
	private static final String ATTRIBUTE_NAME_Z1 = "Z1";
	private static final String ATTRIBUTE_NAME_X2 = "X2";
	private static final String ATTRIBUTE_NAME_Y2 = "Y2";
	private static final String ATTRIBUTE_NAME_Z2 = "Z2";
	private static final String ATTRIBUTE_NAME_WORLD_NAME = "WorldName";
	
	private HashMap<String, ProtectedArea> protectedAreaMap;
	
	public AreaProtectionSystem(Plugin plugin) {
		protectedAreaMap = new HashMap<>();
		
		plugin.getCommand("areaprotection").setExecutor(new APSCommandExecutor(plugin, this));
		plugin.getCommand("areaprotection").setTabCompleter(new APSCommandExecutor(plugin, this));
		Bukkit.getPluginManager().registerEvents(new APSListener(plugin, this), plugin);
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading area protection system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				int x1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X1);
				int y1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y1);
				int z1Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z1);
				int x2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_X2);
				int y2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Y2);
				int z2Index = dataTable.getAttributeIndex(ATTRIBUTE_NAME_Z2);
				int worldNameIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_WORLD_NAME);
				
				if(idIndex >= 0 && x1Index >= 0 && y1Index >= 0 && z1Index >= 0 && x2Index >= 0 && y2Index >= 0 && 
						z2Index >= 0 && worldNameIndex >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});

					for(String[] dataSet : dataSets) {
						float x1 = NumberUtil.toFloat(dataSet[x1Index], 0);
						float y1 = NumberUtil.toFloat(dataSet[y1Index], 0);
						float z1 = NumberUtil.toFloat(dataSet[z1Index], 0);
						float x2 = NumberUtil.toFloat(dataSet[x2Index], 0);
						float y2 = NumberUtil.toFloat(dataSet[y2Index], 0);
						float z2 = NumberUtil.toFloat(dataSet[z2Index], 0);
						LocationData a = new LocationData(x1, y1, z1, dataSet[worldNameIndex]);
						LocationData b = new LocationData(x2, y2, z2, dataSet[worldNameIndex]);
						
						protectedAreaMap.put(dataSet[idIndex], new ProtectedArea(a, b));
					}
					
					logger.log("§bArea protection system loaded");
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
		
		logger.log("§bArea protection system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_ID, ATTRIBUTE_NAME_X1, ATTRIBUTE_NAME_Y1, 
					ATTRIBUTE_NAME_Z1, ATTRIBUTE_NAME_X2, ATTRIBUTE_NAME_Y2, ATTRIBUTE_NAME_Z2, 
					ATTRIBUTE_NAME_WORLD_NAME});
			Set<String> keySet = protectedAreaMap.keySet();
			
			for(String id : keySet) {
				ProtectedArea protectedArea = protectedAreaMap.get(id);
				LocationData a = protectedArea.a;
				LocationData b = protectedArea.b;
				
				dataTable.addDataSet(new String[] {id, a.getX() + "", a.getY() + "", a.getZ() + "", b.getX() + "", 
						b.getY() + "", b.getZ() + "", a.getWorldName()});
			}
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bArea protection system saved");
		} else
			logger.log("Data base is null");
	}
	
	public void setProtectedArea(String name, ProtectedArea protectedArea) {
		if(name != null && protectedArea != null)
			protectedAreaMap.put(name, protectedArea);
	}
	
	public void removeProtectedArea(String name) {
		protectedAreaMap.remove(name);
	}
	
	public ProtectedArea getProtectedArea(String name) {
		return protectedAreaMap.get(name);
	}
	
	public ArrayList<String> listProtectedAreas() {
		ArrayList<String> protectedAreas = new ArrayList<>();
		Set<String> keySet = protectedAreaMap.keySet();
		
		for(String protectedAreaID : keySet)
			protectedAreas.add(protectedAreaID);
		
		return protectedAreas;
	}
	
	public boolean inProtectedArea(LocationData locationData) {
		if(locationData != null) {
			Set<String> keySet = protectedAreaMap.keySet();
			
			for(String id : keySet) {
				ProtectedArea protectedArea = protectedAreaMap.get(id);
				float minX = (float) Math.floor(Math.min(protectedArea.getA().getX(), protectedArea.getB().getX()));
				float maxX = (float) Math.ceil(Math.max(protectedArea.getA().getX(), protectedArea.getB().getX()));
				float minY = (float) Math.floor(Math.min(protectedArea.getA().getY(), protectedArea.getB().getY()));
				float maxY = (float) Math.ceil(Math.max(protectedArea.getA().getY(), protectedArea.getB().getY()));
				float minZ = (float) Math.floor(Math.min(protectedArea.getA().getZ(), protectedArea.getB().getZ()));
				float maxZ = (float) Math.ceil(Math.max(protectedArea.getA().getZ(), protectedArea.getB().getZ()));
				
				if(locationData.getX() >= minX && locationData.getY() >= minY && locationData.getZ() >= minZ && 
						locationData.getX() <= maxX && locationData.getY() <= maxY && locationData.getZ() <= maxZ)
					return true;
			}
		}
		
		return false;
	}
	
	public static class ProtectedArea {
		
		private LocationData a;
		private LocationData b;
		
		public ProtectedArea(LocationData a, LocationData b) {
			if(a != null)
				this.a = a;
			else
				this.a = new LocationData(0, 0, 0, "");
			
			if(b != null)
				this.b = b;
			else
				this.b = new LocationData(0, 0, 0, "");
		}
		
		public LocationData getA() {
			return a;
		}
		
		public LocationData getB() {
			return b;
		}
	}
}
