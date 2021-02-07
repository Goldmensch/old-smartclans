package de.nick.smartclans.main;

import java.io.File;

import org.bukkit.plugin.java.JavaPlugin;

import de.nick.smartclans.commands.*;
import de.nick.smartclans.messages.MessageManager;

public class Main extends JavaPlugin{
	
	private static Main plugin;
	private MessageManager messages;
	
	public void onEnable() {
		//start
		
		//general
		plugin = this;
		messages = new MessageManager();
		
		/*-----files-----*/
		if(!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		if(!messages.getFile().exists()) {
 		messages = new MessageManager();
			messages.saveDefaults();
		}
		messages.saveDefaults();
		//commnds
		ClansCommand clanscommand = new ClansCommand();
		getCommand("clans").setExecutor(clanscommand);
		getCommand("clans").setTabCompleter(clanscommand);
		
		
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
}
