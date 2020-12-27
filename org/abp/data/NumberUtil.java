package org.abp.data;

public class NumberUtil {

	public static int toInt(String string, int backup) {
		try {
			return Integer.parseInt(string);
		} catch(Exception exception) {
			return backup;
		}
	}
	
	public static float toFloat(String string, float backup) {
		try {
			return Float.parseFloat(string);
		} catch(Exception exception) {
			return backup;
		}
	}
	
	public static boolean toBoolean(String string, boolean backup) {
		try {
			return Boolean.parseBoolean(string);
		} catch(Exception exception) {
			return backup;
		}
	}
}
