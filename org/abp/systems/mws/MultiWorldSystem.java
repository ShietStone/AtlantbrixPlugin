package org.abp.systems.mws;

import java.util.ArrayList;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.data.DataBase.DataTable;
import org.abp.data.DataBase.SearchCondition;
import org.abp.logging.Logger;
import org.abp.systems.ApplicationSystem;
import org.bukkit.WorldCreator;

public class MultiWorldSystem implements ApplicationSystem {

	private static final String TABLE_NAME = "MultiWorldSystemTable";
	private static final String ATTRIBUTE_NAME_ID = "ID";
	
	private Plugin plugin;
	private ArrayList<String> worldList;
	
	public MultiWorldSystem(Plugin plugin) {
		this.plugin = plugin;
		worldList = new ArrayList<>();
		
		plugin.getCommand("multiworld").setExecutor(new MWSCommandExecutor(plugin, this));
		plugin.getCommand("multiworld").setTabCompleter(new MWSCommandExecutor(plugin, this));
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		if(logger == null)
			return;
		
		logger.log("§bLoading multi morld system...");
		
		if(dataBase != null) {
			DataTable dataTable = dataBase.getTable(TABLE_NAME);
			
			if(dataTable != null) {
				int idIndex = dataTable.getAttributeIndex(ATTRIBUTE_NAME_ID);
				
				if(idIndex >= 0) {
					ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});

					for(String[] dataSet : dataSets) {
						String id = dataSet[idIndex];
						worldList.add(id);
						plugin.getServer().createWorld(new WorldCreator(id));
					}
					
					logger.log("§bMulti world system loaded");
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
		
		logger.log("§bMulti world system saving...");
		
		if(dataBase != null) {
			DataTable dataTable = new DataTable(new String[] {ATTRIBUTE_NAME_ID});
			
			for(String id : worldList)
				dataTable.addDataSet(new String[] {id});
			
			dataBase.setTable(TABLE_NAME, dataTable);
			
			logger.log("§bMutli world system saved");
		} else
			logger.log("§eData base is null");
	}
	
	public void add(String name) {
		if(name != null)
			worldList.add(name);
	}
	
	public void remove(String name) {
		worldList.remove(name);
	}
	
	public boolean exists(String name) {
		return worldList.contains(name);
	}
	
	public ArrayList<String> getWorldList() {
		return worldList;
	}
}
