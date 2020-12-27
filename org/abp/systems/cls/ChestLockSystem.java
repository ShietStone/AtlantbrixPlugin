package org.abp.systems.cls;

import org.abp.core.Plugin;
import org.abp.data.DataBase;
import org.abp.logging.Logger;
import org.abp.systems.ApplicationSystem;
import org.abp.systems.cs.ClanSystem;
import org.bukkit.Bukkit;

public class ChestLockSystem implements ApplicationSystem {

	public ChestLockSystem(Plugin plugin, ClanSystem clanSystem) {
		Bukkit.getPluginManager().registerEvents(new CLSListener(plugin, clanSystem), plugin);
	}

	@Override
	public void load(Logger logger, DataBase dataBase) {
		logger.log("§bChest lock system active");
	}

	@Override
	public void save(Logger logger, DataBase dataBase) {
		logger.log("§bChest lock system inactive");
	}
}
