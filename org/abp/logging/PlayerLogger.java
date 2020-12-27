package org.abp.logging;

import org.abp.chat.ChatTheme;
import org.bukkit.entity.Player;

public class PlayerLogger implements Logger {

	private Player player;
	private ChatTheme chatTheme;
	
	public PlayerLogger(Player player, ChatTheme chatTheme) {
		this.player = player;
		this.chatTheme = chatTheme;
	}
	
	@Override
	public void log(String message) {
		if(player != null && chatTheme != null)
			player.sendMessage(chatTheme.apply(message));
	}
}
