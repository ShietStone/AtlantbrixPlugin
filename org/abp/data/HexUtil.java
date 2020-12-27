package org.abp.data;

public class HexUtil {

	public static String fromHex(String string) {
		if(string == null)
			return "";
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int index = 1; index < string.length(); index += 2)
			stringBuilder.append((char) (fromHexSimple(string.charAt(index - 1)) * 16 + fromHexSimple(string.charAt(index))));
		
		return stringBuilder.toString();
	}
	
	public static String toHex(String string) {
		if(string == null)
			return "";
		
		StringBuilder stringBuilder = new StringBuilder();
		
		for(int index = 0; index < string.length(); index++) {
			int c = string.charAt(index);
			stringBuilder.append(toHexSimple(c / 16));
			stringBuilder.append(toHexSimple(c % 16));
		}
		
		return stringBuilder.toString();
	}
	
	public static int fromHexSimple(char c) {
		switch(c) {
			case '1':
				return 1;
			case '2':
				return 2;
			case '3':
				return 3;
			case '4':
				return 4;
			case '5':
				return 5;
			case '6':
				return 6;
			case '7':
				return 7;
			case '8':
				return 8;
			case '9':
				return 9;
			case 'A':
				return 10;
			case 'B':
				return 11;
			case 'C':
				return 12;
			case 'D':
				return 13;
			case 'E':
				return 14;
			case 'F':
				return 15;
			case 'a':
				return 10;
			case 'b':
				return 11;
			case 'c':
				return 12;
			case 'd':
				return 13;
			case 'e':
				return 14;
			case 'f':
				return 15;	
			default:
				return 0;
		}
	}

	public static char toHexSimple(int i) {
		switch(i) {
			case 1:
				return '1';
			case 2:
				return '2';
			case 3:
				return '3';
			case 4:
				return '4';
			case 5:
				return '5';
			case 6:
				return '6';
			case 7:
				return '7';
			case 8:
				return '8';
			case 9:
				return '9';
			case 10:
				return 'a';
			case 11:
				return 'b';
			case 12:
				return 'c';
			case 13:
				return 'd';
			case 14:
				return 'e';
			case 15:
				return 'f';
			default:
				return '0';
		}
	}
}
