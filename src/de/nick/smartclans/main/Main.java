package de.nick.smartclans.main;

import org.bukkit.plugin.java.JavaPlugin;

import de.nick.smartclans.commands.*;
import de.nick.smartclans.messages.MessageManager;

public class Main extends JavaPlugin{
	
	private static Main plugin;
	private MessageManager messages;
	
	public void onEnable() {
		//general
		plugin = this;
		messages = new MessageManager();
		
		//-----files-----*/
		//messages
		if(!messages.getFile().exists()) {
			messages.saveDefaults();
		}
		
		//commnds
		getCommand("clans").setExecutor(new ClansCommand());
		
		
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
}
