package org.abp.core;

import java.io.File;

import org.abp.chat.ChatTheme;
import org.abp.chat.ChatUtil;
import org.abp.data.DataBase;
import org.abp.loading.DataBaseLoader;
import org.abp.loading.FileLoader;
import org.abp.logging.Logger;
import org.abp.systems.aps.AreaProtectionSystem;
import org.abp.systems.bms.BetterMessageSystem;
import org.abp.systems.cls.ChestLockSystem;
import org.abp.systems.cs.ClanSystem;
import org.abp.systems.hs.HomeSystem;
import org.abp.systems.mws.MultiWorldSystem;
import org.abp.systems.pls.PortalLinkSystem;
import org.abp.systems.ss.SpawnSystem;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin implements Logger {
	
	private static final String DATA_BASE_PATH = "ABPDataBase.dtb";
	
	private DataBase dataBase;
	private ChatTheme chatTheme;
	private SpawnSystem spawnSystem;
	private AreaProtectionSystem areaProtectionSystem;
	private HomeSystem homeSystem;
	private ClanSystem clanSystem;
	private ChestLockSystem chestLockSystem;
	private MultiWorldSystem multiWorldSystem;
	private PortalLinkSystem portalLinkSystem;
	private BetterMessageSystem betterMessageSystem;
	
	public Plugin() {
		
	}
	
	@Override
	public void onEnable() {
		chatTheme = new ChatTheme("§c[§hABP§c]§b ", ChatUtil.COLOR_GRAY, ChatUtil.COLOR_YELLOW, ChatUtil.COLOR_RED, 
				ChatUtil.COLOR_DARK_GRAY);
		
		log("Plugin enabling...");
		
		loadDataBase();
		
		spawnSystem = new SpawnSystem(this);
		spawnSystem.load(this, dataBase);
		areaProtectionSystem = new AreaProtectionSystem(this);
		areaProtectionSystem.load(this, dataBase);
		homeSystem = new HomeSystem(this);
		homeSystem.load(this, dataBase);
		clanSystem = new ClanSystem(this);
		clanSystem.load(this, dataBase);
		chestLockSystem = new ChestLockSystem(this, clanSystem);
		chestLockSystem.load(this, dataBase);
		multiWorldSystem = new MultiWorldSystem(this);
		multiWorldSystem.load(this, dataBase);
		portalLinkSystem = new PortalLinkSystem(this);
		portalLinkSystem.load(this, dataBase);
		betterMessageSystem = new BetterMessageSystem(this);
		betterMessageSystem.load(this, dataBase);
		
		log("Plugin enabled");
	}
	
	@Override
	public void onDisable() {
		log("Plugin disabling");
		
		spawnSystem.save(this, dataBase);
		areaProtectionSystem.save(this, dataBase);
		homeSystem.save(this, dataBase);
		clanSystem.save(this, dataBase);
		chestLockSystem.save(this, dataBase);
		multiWorldSystem.save(this, dataBase);
		portalLinkSystem.save(this, dataBase);
		betterMessageSystem.save(this, dataBase);
		saveDataBase();
		
		log("Plugin disabled");
	}

	@Override
	public void log(String message) {
		getLogger().info(chatTheme.removeTheme(message));
	}

	public void loadDataBase() {
		log("Loading data base...");
		
		String dataBaseSource = FileLoader.loadFile(new File(DATA_BASE_PATH), null);
		
		if(dataBaseSource == null)
			log("Data base file \"" + DATA_BASE_PATH + "\" not found. Using empty data base");
		
		dataBase = DataBaseLoader.load(dataBaseSource);
		log("Data base loaded");
	}
	
	public void saveDataBase() {
		log("Saving data base...");
		
		FileLoader.saveFile(new File(DATA_BASE_PATH), DataBaseLoader.save(dataBase));
		
		log("Data base saved");
	}
	
	public DataBase getDataBase() {
		return dataBase;
	}
	
	public ChatTheme getChatTheme() {
		return chatTheme;
	}
}
