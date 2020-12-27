package org.abp.systems.bms;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.logging.Logger;
import org.abp.systems.ApplicationSystem;
import org.bukkit.Bukkit;

public class BetterMessageSystem implements ApplicationSystem {
	
	public BetterMessageSystem(Plugin plugin) {
		Bukkit.getPluginManager().registerEvents(new BMSListener(plugin), plugin);
	}
	
	@Override
	public void load(Logger logger, DataBase dataBase) {
		logger.log("§bBetter message system active");
	}

	@Override
	public void save(Logger logger, DataBase dataBase) {
		logger.log("§bBetter message system unactive");
	}
}
