/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.messages;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import de.nick.smartclans.main.Main;

public class MessageManager {

	private File file;
	private YamlConfiguration config;
	private HashMap<String, Object> defaults;
	
	public MessageManager() {
		file = new File(Main.getPlugin().getDataFolder(), "messages.yml");
		config = YamlConfiguration.loadConfiguration(file);
		defaults = new HashMap<String, Object>();
	}
	
	public void addDefaults() {
		defaults.put("prefix", "§6[Clans] §r");
		defaults.put("command-exe-no-player", "§cThis command must be executed by a player.");
		defaults.put("no-permission", "§cYou don't have permissions to do this.");
		defaults.put("clan-created", "§aThe clan %clan% was created by %creator%.");
		defaults.put("clan-already-exist", "§cThe clan %clan% already exists.");
		defaults.put("you-already-in-Clan", "§cYou are already in a clan.");
		defaults.put("you-not-in-Clan", "§cYou are not in a Clan.");
		defaults.put("clan-description-was-set", "§aThe clan description was set.");
		defaults.put("co-leader-added", "§aThe co-leader %coleader% has been added to %clan%.");
		defaults.put("player-not-online", "§cThe player %player% isn't online.");
		defaults.put("player-not-in-same-clan", "§cThe player %player% isn't in the same Clan as you.");
		defaults.put("you-are-now-coleader-of", "§aYou're now a coleader of %clan%.");
		defaults.put("player-already-coleader-or-leader", "§cThe player %player% is already a leader or co leader.");
		defaults.put("confirmname-not-match", "§cConfirmation failed! Clanname does not match.");
		defaults.put("clan-deleted", "§aThe clan %clan% has been deleted.");
		defaults.put("plugin-reloaded", "§aPlugin successfully reloaded.");
		defaults.put("clan-not-exist", "§cThis Clan doesn't exist.");
		defaults.put("player-already-in-clan", "§cThe player %player% is already in a clan.");
		defaults.put("you-invited-to", "§bYou have been invited to the clan %clan%.");
		defaults.put("you-invited-other", "§aYou have invited %player% to the clan.");
		defaults.put("no-invitation", "§cYou don't have an invitation.");
		defaults.put("you-joined", "§aYou joined the clan %clan%.");
		defaults.put("player-joined", "The player %player% has joined the clan.");
		defaults.put("decline-invite", "§cYou have declined the invitation to join clan %clan%.");
		defaults.put("you-left", "§aYou left the %clan% clan.");
		defaults.put("player-left", "§cThe player %player% has left the clan");
		defaults.put("player-already-invited", "§cThe player %player% is already invited to the clan %clan%.");
		defaults.put("invite-expired", "§cThe clan invitation to %clan% has just expired.");
		defaults.put("you-cant-leave", "§cYou can't leave the clan!");
		defaults.put("clanname-to-long", "§cThe clanname must not be longer than %lenght%.");
	}
	
	public void saveDefaults() {
		config.set("version", 1);
		addDefaults();
		for(String path : defaults.keySet()) {
			config.set(path, defaults.get(path));
		}
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getPrefix() {
		return config.getString("prefix");
	}
	
	public String get(String path) {
		return getPrefix() + config.getString(path);
	}
	
	public File getFile() {
		return file;
	}
	
	public String getRaw(String path) {
		return config.getString(path);
	}
	
	public void load() {
		if(!file.exists()) {
			saveDefaults();
			return;
		}
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
		
	}
	
}
