package org.abp.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class DataBase {

	private HashMap<String, DataTable> dataTableMap;
	
	public DataBase() {
		dataTableMap = new HashMap<>();
	}
	
	public void addTable(String name, String[] layout) {
		if(name != null && layout != null)
			dataTableMap.put(name, new DataTable(layout));
	}
	
	public void removeTable(String name) {
		dataTableMap.remove(name);
	}
	
	public void setTable(String name, DataTable dataTable) {
		if(name != null && dataTable != null)
			dataTableMap.put(name, dataTable);
	}
	
	public DataTable getTable(String name) {
		return dataTableMap.get(name);
	}
	
	public boolean tableExists(String name) {
		return dataTableMap.get(name) != null;
	}
	
	public ArrayList<String> getAllTableNames() {
		ArrayList<String> tableNames = new ArrayList<>();
		
		for(String name : dataTableMap.keySet())
			tableNames.add(name);
		
		return tableNames;
	}
	
	public static class DataTable {
		
		private String[] dataSetLayout;
		private ArrayList<String[]> dataSetList;
		
		public DataTable(String[] dataSetLayout) {
			if(dataSetLayout == null)
				this.dataSetLayout = new String[0];
			else
				this.dataSetLayout = dataSetLayout;
			
			dataSetList = new ArrayList<>();
		}
		
		public void addDataSet(String[] dataSet) {
			if(dataSet != null && dataSet.length == dataSetLayout.length)
				dataSetList.add(Arrays.copyOf(dataSet, dataSet.length));
		}
		
		public ArrayList<String[]> getDataSet(SearchCondition[] dataSearch) {
			ArrayList<String[]> result = new ArrayList<>();
			
			if(dataSearch == null)
				dataSearch = new SearchCondition[0];
			
			int[] attributeIndices = new int[dataSearch.length];
			
			for(int index = 0; index < dataSearch.length; index++) {
				SearchCondition searchCondition = dataSearch[index];
				
				if(searchCondition != null) {
					int attributeIndex = getAttributeIndex(dataSearch[index].attributeName);
					
					if(attributeIndex >= 0)
						attributeIndices[index] = attributeIndex;
					else
						return result;
				} else
					return result;
			}
		
			outerLoop:
			for(String[] dataSet : dataSetList) {
				for(int index = 0; index < attributeIndices.length; index++) 
					if(!dataSet[attributeIndices[index]].equals(dataSearch[index].attributeValue))
						continue outerLoop;
				
				result.add(Arrays.copyOf(dataSet, dataSet.length));
			}
			
			return result;
		}
		
		
		public int getAttributeIndex(String name) {
			for(int index = 0; index < dataSetLayout.length; index++)
				if(dataSetLayout[index].equals(name))
					return index;
			return -1;
		}
		
		public String[] getDataSetLayout() {
			return Arrays.copyOf(dataSetLayout, dataSetLayout.length);
		}
	}
	
	public static class SearchCondition {
		
		private String attributeName;
		private String attributeValue;
		
		public SearchCondition(String attributeName, String attributeValue) {
			this.attributeName = attributeName;
			this.attributeValue = attributeValue;
		}
		
		public String getAttributeName() {
			return attributeName;
		}
		
		public String getAttributeValue() {
			return attributeValue;
		}
	}
}
