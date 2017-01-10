package com.mrpowergamerbr.relayittodiscord.listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.mrpowergamerbr.relayittodiscord.RelayItToDiscord;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;

public class ChatListener implements Listener {
	RelayItToDiscord m;

	public ChatListener(RelayItToDiscord m) {
		this.m = m;
		Bukkit.getPluginManager().registerEvents(this, m);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onChat(AsyncPlayerChatEvent e) {
		if (m.startRelaying) {
			if (m.getConfig().getBoolean("Advanced.IgnoreCancelled") && e.isCancelled()) {
				return;
			}
			
			String message = e.getMessage();
			if (m.getConfig().getBoolean("StripColors")) {
				// The super ultimate workaround(tm)
				message = ChatColor.translateAlternateColorCodes('&', message);
				message = ChatColor.stripColor(message);
			}
			String format = m.getConfig().getString("Format");
			format = format.replace("%content%", message);
			format = m.parse(format, e.getPlayer());
			
			DiscordMessage dm = DiscordMessage.builder()
					.username(m.parse(m.getConfig().getString("Username"), e.getPlayer())) // Player's name
					.content(format) // Player's message
					.avatarUrl(m.parse(m.getConfig().getString("AvatarUrl"), e.getPlayer())) // Avatar
					.build();
			
			m.temmie.sendMessage(dm);
		}
	}
}
