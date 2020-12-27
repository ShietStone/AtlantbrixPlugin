package org.abp.loading;

import java.util.ArrayList;

import org.abp.data.DataBase;
import org.abp.data.HexUtil;
import org.abp.data.DataBase.DataTable;
import org.abp.data.DataBase.SearchCondition;

public class DataBaseLoader {

	public static DataBase load(String source) {
		DataBase dataBase = new DataBase();
		
		if(source == null)
			return dataBase;
		
		source = source.replace("\r", "");
		String[] lines = source.split("\n");
		DataTable dataTable = null;
		String tableName = null;
		
		for(String line : lines) {
			if(line.length() == 0)
				continue;
			
			if(line.startsWith("[") && line.endsWith("]")) {
				
				if(dataTable != null && tableName != null)
					dataBase.setTable(tableName, dataTable);
				
				tableName = fromCleanString(line.substring(1, line.length() - 1));
				dataTable = null;
			} else if(tableName != null) {
				String[] dataSet = line.substring(0, line.length() - 1).split("\\;");
				
				if(dataTable != null)
					dataTable.addDataSet(dataSet);
				else
					dataTable = new DataTable(dataSet);
			}
		}
		
		if(dataTable != null && tableName != null)
			dataBase.setTable(tableName, dataTable);
		
		return dataBase;
	}
	
	public static String save(DataBase dataBase) {
		if(dataBase == null)
			return "";
		
		StringBuilder stringBuilder = new StringBuilder();
		ArrayList<String> tableNames = dataBase.getAllTableNames();
		
		for(String tableName : tableNames) {
			DataTable dataTable = dataBase.getTable(tableName);
			String[] dataSetLayout = dataTable.getDataSetLayout();
			ArrayList<String[]> dataSets = dataTable.getDataSet(new SearchCondition[] {});
			
			stringBuilder.append("[" + toCleanString(tableName) + "]\n");
			
			for(String attributeName : dataSetLayout)
				stringBuilder.append(toCleanString(attributeName) + ";");
			
			for(String[] dataSet : dataSets) {
				stringBuilder.append("\n");
				
				for(String dataValue : dataSet)
					stringBuilder.append(toCleanString(dataValue) + ";");
			}
			
			stringBuilder.append("\n");
		}
		
		return stringBuilder.toString();
	}
	
	private static String toCleanString(String string) {
		if(string == null)
			return "null";
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			
			if((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9') || c == '.' || c == '_' ||
					c  == '-' || c == ',' || c == '/' || c == '"' || c == '\'' || c == '#')
				stringBuilder.append(c);
			else
				stringBuilder.append('\\' + HexUtil.toHex("" + c));
		}
		
		return stringBuilder.toString();
	}
	
	private static String fromCleanString(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			
			if(c == '\\')
				if(index < string.length() - 2)
					stringBuilder.append(HexUtil.fromHex(string.substring(index + 1, index + 3)));
				else
					break;
			else
				stringBuilder.append(c);
		}
		
		return stringBuilder.toString();
	}
}