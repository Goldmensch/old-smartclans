package de.nick.smartclans.main;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import de.nick.smartclans.commands.*;
import de.nick.smartclans.messages.MessageManager;

public class Main extends JavaPlugin{
	
	private static Main plugin;
	private MessageManager messages;
	
	public void onEnable() {
		//start
		Bukkit.getConsoleSender().sendMessage("§6SmartClans §bby goldmensch has been §aactivated!");
		
		//general
		plugin = this;
		messages = new MessageManager();
		
		/*-----files-----*/
/*dev>>		if(!messages.getFile().exists()) {
			messages.saveDefaults();
		} */
		messages.saveDefaults();
		
		//commnds
		getCommand("clans").setExecutor(new ClansCommand());
		getCommand("clans").setTabCompleter(new ClansCommand());
		
		
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
}
