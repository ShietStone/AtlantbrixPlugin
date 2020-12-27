package org.abp.test;

import java.util.ArrayList;

import org.abp.data.DataBase;
import org.abp.data.DataBase.DataTable;
import org.abp.loading.DataBaseLoader;
import org.abp.data.LocationData;
import org.abp.data.NumberUtil;

public class Test {
	
	public static void main(String[] args) {
		ArrayList<Clan> clanList = new ArrayList<>();
		ArrayList<Home> homeList = new ArrayList<>();
		
		loadClanSystem("testClan/a4704930-19b4-482e-a59b-ff46b5704997!true#Unicorns/18e1fcad-aa6c-4864-8834-8a7ef99ff60d!true/7e2073bf-1956-4457-b97d-d47a854dcc57!false/55b1d0c6-a0f0-485b-9522-7864aa2bd5ff!false#Brixenhaus/18e1fcad-aa6c-4864-8834-8a7ef99ff60d!true/7e2073bf-1956-4457-b97d-d47a854dcc57!false/8f9b5a1b-e298-4742-b67f-3e8ee0b3aef9!false#MORIA/c45821cb-dd34-43cc-9236-53e2cd2ecd31!false#Jumbo_Schreiner_Kult/5eed1406-54a0-49e3-8b1f-48d24fea9a41!true/9bb4d4b9-e9a8-4217-aa34-16f49c7bc629!false/c45821cb-dd34-43cc-9236-53e2cd2ecd31!false#Fire/55b1d0c6-a0f0-485b-9522-7864aa2bd5ff!true/c0f3d8a4-5e7a-4f82-a5b6-9355ef268974!true#TheAssassins/b15954c8-7b72-4b65-8a27-19c6e494ab90!true#Spasskasse/0c6b5a82-e6b0-49ea-b02b-b30fd3d67f46!true#Fire_Owner/55b1d0c6-a0f0-485b-9522-7864aa2bd5ff!true#", clanList);
		loadHomeSytem("a4704930-19b4-482e-a59b-ff46b5704997/-309.103.384.world/null/-93.63.220.world/null/null!8f9b5a1b-e298-4742-b67f-3e8ee0b3aef9/null/-1342.65.-1032.world/null/null/null!18e1fcad-aa6c-4864-8834-8a7ef99ff60d/-490.48.-780.world/-197.50.18.world/-371.64.-1742.world/-360.64.-1751.world/-3590.71.-452.FarmWorld!8962b06f-373e-4816-a11a-09bc456ec5e8/-2482.62.224.FarmWorld/24.75.460.world_nether/268.78.583.world_nether/-934.75.2281.world/80.246.-66.world_nether!05f793da-4af2-4417-8c24-10b89c2bcbff/null/-149.69.100.world/-645.25.-165.world_nether/-1755.151.2600.world/-11426.136.3272.world_the_end!55b1d0c6-a0f0-485b-9522-7864aa2bd5ff/-104.38.1054.world/80.240.-64.world_nether/-155.68.67.world/-889.71.2265.world/-145.19.1055.world!b15954c8-7b72-4b65-8a27-19c6e494ab90/null/260.14.170.world_nether/-453.64.-235.world/-65.65.374.world/-322.63.-14.world!c45821cb-dd34-43cc-9236-53e2cd2ecd31/-288.64.-7.world/-369.63.180.world/-943.84.226.world/-215.49.5.world/2.1.222.world_the_end!9bb4d4b9-e9a8-4217-aa34-16f49c7bc629/-275.63.-24.world/null/230.57.8.world_the_end/-928.84.218.world/-337.11.26.world_nether!5eed1406-54a0-49e3-8b1f-48d24fea9a41/-308.64.-13.world/-172.64.40.world/-453.64.-235.world/15.71.264.world/-937.84.228.world!c0f3d8a4-5e7a-4f82-a5b6-9355ef268974/105.88.1325.world/-3449.79.-88.world/-219.67.15.world/-52.70.1203.world/84.44.231.world_nether!0c6b5a82-e6b0-49ea-b02b-b30fd3d67f46/-105.56.235.world/-196.50.19.world/-308.81.-1777.world/1.1.221.world_the_end/-11925.65.4688.world!e7799837-aa1e-44fa-9e8b-0de8ca7acef4/-218.68.42.world/-929.71.2244.world/3049.90.-2113.FarmWorld/-299.117.-975.FarmWorld/-1441.11.-504.FarmWorld!b68094d1-532d-4c66-990d-2726f9a5fb16/-163.74.281.world/null/-239.93.498.world/null/null!103e2e7a-b3d3-4890-9b94-ccb48dae70bb/-147.72.440.world/null/null/null/null!30bb1f67-1a2d-488f-a881-2e8a877b4a30/-127.64.44.world/null/null/null/null!3766ab81-385c-4f44-b27d-d72cae4c319d/-151.64.-28.world/-1551.66.1974.world/1.1.221.world_the_end/-474.68.-245.world/-2843.63.2193.world!", homeList);
		
		DataTable clanTable = new DataTable(new String[] {"ID", "UUID", "Status"});
		DataTable homeTable = new DataTable(new String[] {"UUID", "ID", "Default", "X", "Y", "Z", "WorldName"});
		
		for(Clan clan : clanList) {
			for(ClanMember clanMember : clan.clanMemberList)
				clanTable.addDataSet(new String[] {clan.name, clanMember.uuid, "" + clanMember.clanAdmin});
		}
		
		for(Home home : homeList) {
			for(int i = 0; i < home.locations.length; i++) {
				LocationData locationData = home.locations[i];
				if(locationData != null)
					homeTable.addDataSet(new String[] {home.uuid, "" + i, "false", "" + ((float) locationData.getX()), "" + ((float) locationData.getY()), "" + ((float) locationData.getZ()), locationData.getWorldName()});
			}
		}
		
		DataBase base = new DataBase();
		base.setTable("ClanSystemTable", clanTable);
		base.setTable("HomeSystemTable", homeTable);
		System.out.println(DataBaseLoader.save(base));
	}
	
	private static void loadClanSystem(String source, ArrayList<Clan> clanList) {
		if(source == null)
			return;
		
		String[] clanEntires = source.split("\\#");
		
		for(String clanEntry : clanEntires) {
			if(clanEntry != null) {
				String[] clanParts = clanEntry.split("\\/");
				
				Clan clan = new Clan(clanParts[0]);
				
				for(int i = 1; i < clanParts.length; i++) {
					if(clanParts[i] == null)
						continue;
					
					String[] memberParts = clanParts[i].split("\\!");
					clan.clanMemberList.add(new ClanMember(memberParts[0], memberParts[1].equals("true")));
				}
				
				clanList.add(clan);
			}
		}
	}
	
	public static void loadHomeSytem(String source, ArrayList<Home> homeList) {
		if(source == null)
			return;
		
		String[] homeEntries = source.split("\\!");
		
		for(String homeEntry : homeEntries) {
			if(homeEntry != null) {
				String[] homeParts = homeEntry.split("\\/");
				
				LocationData[] locations = new LocationData[homeParts.length - 1];
				
				for(int i = 0; i < locations.length; i++) {
					String homePart = homeParts[i + 1];
					
					locations[i] = null;
					
					if(!homePart.equals("null")) {
						String[] locationParts = homePart.split("\\.");
						
						if(locationParts.length == 4) {
							locations[i] = new LocationData(NumberUtil.toInt(locationParts[0], 0), NumberUtil.toInt(locationParts[1], 0), NumberUtil.toInt(locationParts[2], 0),locationParts[3]);
						}
					}
				}
				
				homeList.add(new Home(homeParts[0], locations));
			}
		}
	}
	
	public static class Clan {
		
		public String name;
		ArrayList<ClanMember> clanMemberList;
		
		Clan(String name) {
			this.name = name;
			clanMemberList = new ArrayList<>();
		}
	}
	
	private static class ClanMember {
				
		String uuid;
		boolean clanAdmin;
		
		ClanMember(String uuid, boolean clanAdmin) {
			this.uuid = uuid;
			this.clanAdmin = clanAdmin;
		}
	}
	
	public static class Home {
		
		public String uuid;
		public LocationData[] locations;
		
		public Home(String uuid, LocationData[] locations) {
			this.uuid = uuid;
			this.locations = locations;
		}
	}
}
