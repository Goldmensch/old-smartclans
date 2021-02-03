package de.nick.smartclans.messages;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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
	}
	
	public void saveDefaults() {
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
	
	
	
}
