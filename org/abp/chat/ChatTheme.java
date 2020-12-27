package org.abp.chat;

public class ChatTheme {

	private String tag;
	private String base;
	private String highlight;
	private String error;
	private String contrast;
	
	public ChatTheme(String tag, String base, String highlight, String error, String contrast) {
		if(tag != null)
			this.tag = tag;
		else
			this.tag = "";
		
		if(base != null)
			this.base = base;
		else
			this.base = ChatUtil.COLOR_GRAY;
		
		if(highlight != null)
			this.highlight = highlight;
		else
			this.highlight = ChatUtil.COLOR_YELLOW;
		
		if(error != null)
			this.error = error;
		else
			this.error = ChatUtil.COLOR_RED;
		
		if(contrast != null)
			this.contrast = contrast;
		else
			this.contrast = ChatUtil.COLOR_DARK_GRAY;
	}
	
	public String getTag() {
		return tag;
	}
	
	public String getBase() {
		return base;
	}
	
	public String getHighlight() {
		return highlight;
	}
	
	public String getError() {
		return error;
	}
	
	public String getContrast() {
		return contrast;
	}
	
	public String apply(String string) {
		if(string != null)
			return applyWithoutTag(tag + string);
		else
			return null;
	}
	
	public String applyWithoutTag(String string) {
		if(string != null) {
			StringBuilder stringBuilder = new StringBuilder();
			
			boolean code = false;
			for(int index = 0; index < string.length(); index++) {
				char c = string.charAt(index);
				
				if(code) {
					switch(c) {
						case 'b':
							stringBuilder.append(base);
							break;
						case 'h':
							stringBuilder.append(highlight);
							break;
						case 'e':
							stringBuilder.append(error);
							break;
						case 'c':
							stringBuilder.append(contrast);
							break;
						default:
							break;
					}
					
					code = false;
				} else if(c == '§')
					code = true;
				else
					stringBuilder.append(c);
			}
			
			return stringBuilder.toString();
		} else
			return null;
	}
	
	public String removeTheme(String string) {
		if(string.startsWith(tag))
			string = string.substring(tag.length());
		
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
