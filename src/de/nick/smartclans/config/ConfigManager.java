package de.nick.smartclans.config;

import de.nick.smartclans.main.Main;

public class ConfigManager {

	Main main;
	
	public ConfigManager() {
		main = Main.getPlugin();
	}
	
	public void saveDefaults() {
		main.saveDefaultConfig();
	}
	
	public void reload() {
		main.reloadConfig();
	}
	
	public int getInt(String path) {
		return main.getConfig().getInt(path);
	}
	
	public boolean teamsEnable() {
		return main.getConfig().getBoolean("use-minecraft-teams");
	}
	
}
