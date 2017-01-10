package com.mrpowergamerbr.relayittodiscord;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.mrpowergamerbr.relayittodiscord.listeners.ChatListener;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;

import me.clip.placeholderapi.PlaceholderAPI;

public class RelayItToDiscord extends JavaPlugin {
	public TemmieWebhook temmie;
	public boolean startRelaying;
	public boolean usePlaceholderApi;

	public void onEnable() {
		saveDefaultConfig();
		setupRelay();
		new ChatListener(this);
	}

	public void setupRelay() {
		startRelaying = false;

		if (getConfig().getString("WebhookLink").equals("Webhook Link")) {
			getLogger().info("Hey, is this your first time using RelayItToDiscord?");
			getLogger().info("If yes, then you need to add your Webhook URL to the");
			getLogger().info("config.yml!");
			getLogger().info("");
			getLogger().info("...so go there and do that.");
			getLogger().info("After doing that, use /relayreload");
			return;
		}
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && getConfig().getBoolean("UsePlaceholderAPI")) {
			usePlaceholderApi = true;
		}
		temmie = new TemmieWebhook(getConfig().getString("WebhookLink"));
		startRelaying = true;
	}

	public String parse(String message, Player p) {
		// Whoops, the server doesn't have PlaceholderAPI!
		// What we do... WHAT WE DO?
		// Oh well, let's add at least some simple placeholders
		message = message.replace("%player_name%", p.getName());
		message = message.replace("%player_uuid%", p.getUniqueId().toString());
		message = message.replace("%player_displayname%", p.getDisplayName());
		message = message.replace("%stripped_player_displayname%", ChatColor.stripColor(p.getDisplayName()));
		
		if (usePlaceholderApi) {
			return NoClassDefFoundWorkaround.parsePlaceholders(message, p);
		} else {
			return message;
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("relayreload")) { // If the player typed /basic then do the following...
			if (sender.hasPermission("relayittodiscord.reload")) {
				reloadConfig();
				setupRelay();
				sender.sendMessage("§aRelayItToDiscord reloaded!");
			} else {
				sender.sendMessage("§cNo permission!");
			}
			return true;

		}
		return false;
	}
}

class NoClassDefFoundWorkaround {
	public static String parsePlaceholders(String message, Player p) {
		return PlaceholderAPI.setPlaceholders(p, message);
	}
}