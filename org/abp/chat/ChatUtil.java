package org.abp.chat;

public class ChatUtil {

	public static final String COLOR_AQUA = "§b";
	public static final String COLOR_BLACK = "§0";
	public static final String COLOR_BLUE = "§9";
	public static final String COLOR_DARK_AQUA = "§3";
	public static final String COLOR_DARK_BLUE = "§1";
	public static final String COLOR_DARK_GRAY = "§8";
	public static final String COLOR_DARK_GREEN = "§2";
	public static final String COLOR_DARK_PURPLE = "§5";
	public static final String COLOR_DARK_RED = "§4";
	public static final String COLOR_GOLD = "§6";
	public static final String COLOR_GRAY = "§7";
	public static final String COLOR_GREEN = "§a";
	public static final String COLOR_LIGHT_PURPLE = "§d";
	public static final String COLOR_RED = "§c";
	public static final String COLOR_WHITE = "§f";
	public static final String COLOR_YELLOW = "§e";
	
	public static final String FORMAT_OBFUSCATED = "§k";
	public static final String FORMAT_BOLD = "§l";
	public static final String FORMAT_STRIKETHROUGH = "§m";
	public static final String FORMAT_UNDERLINE = "§n";
	public static final String FORMAT_ITALIC = "§o";
	public static final String FORMAT_RESET = "§r";
	
	public static String removeCodes(String string) {
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int index = 0; index < string.length(); index++) {
			char c = string.charAt(index);
			
			if(c == '§')
				index++;
			else
				stringBuilder.append(c);
		}
		
		return stringBuilder.toString();
	}
}
