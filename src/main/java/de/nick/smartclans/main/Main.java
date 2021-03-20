/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.main;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.io.Files;

import de.nick.smartclans.commands.*;
import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.listener.PlayerJoinListener;
import de.nick.smartclans.listener.PlayerLeaveListener;
import de.nick.smartclans.messages.MessageManager;
import de.nick.smartclans.metrics.Metrics;
import de.nick.smartclans.plugins.luckperms.LuckpermsManager;
import de.nick.smartclans.teams.TeamManager;
import de.nick.smartclans.update.PluginVersionChecker;
import net.luckperms.api.LuckPerms;

public class Main extends JavaPlugin{
	
	private static Main plugin;
	private static MessageManager messages;
	private static LuckPerms luckperms;
	private static ConfigManager config;
	private static DataManager data;
	private static TeamManager teams;
	private static LuckpermsManager luckpermsmanager;
	
	public void onEnable() {
		
		//general
		plugin = this;
		messages = new MessageManager();
		config = new  ConfigManager();
		data = new DataManager();
		teams = new TeamManager();
		luckpermsmanager = new LuckpermsManager();
		
		//update checker
		if(config.UpdateCheckerEnable()) {
			new PluginVersionChecker(89074).getVersion(version -> {
            	if (this.getDescription().getVersion().equalsIgnoreCase(version)) {
                	Bukkit.getConsoleSender().sendMessage("[SmartClans] §aThere is not a new update available.");
            	} else {
                	Bukkit.getConsoleSender().sendMessage("[SmartClans] §cThere is a new update available.");
            	}
        	});
		}
		
		/*-----files-----*/
		if(!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		if(!messages.getFile().exists()) {
			messages.saveDefaults();
		}
		/*--checkversions--*/
		if(messages.getFileVersion() != messages.getVersion()) {
			Bukkit.getConsoleSender().sendMessage("[SmartClans] §4Old Message File, updating...");
			HashMap<String, String> current_data = messages.getAsHashMap();
			messages.addDefaults();
			for(String key : messages.getDefaults().keySet()) {
				if(current_data.containsKey(key)) continue;
				messages.addValue(key, messages.getDefaults().get(key));
			}
			messages.getConfig().set("version-dont-modify-me", messages.getVersion());
			messages.save();
		}
		if(config.getVersion() != 2) {
			Bukkit.getConsoleSender().sendMessage("[SmartClans] §4Old Config File, updating...");
			File current_config = new File(getDataFolder() + File.separator + "config.yml");
			File backup__config = new File(getDataFolder() + File.separator + "pre_" + getDescription().getVersion() + "_config.yml");
			try {
				Files.copy(current_config, backup__config);
			} catch (IOException e) {
				e.printStackTrace();
			}
			current_config.delete();
			config.saveDefaults();
		}
		
		/*-----Plugins-----*/
		loadPlugins();
		
		//commnds
		ClansCommand clanscommand = new ClansCommand();
		getCommand("clans").setExecutor(clanscommand);
		getCommand("clans").setTabCompleter(clanscommand);
		getCommand("clanchat").setExecutor(new ClanChatCommand());
		
		//events
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoinListener(), plugin);
		pm.registerEvents(new PlayerLeaveListener(), plugin);
		
		/*-----bstats-----*/
		initalBstats();
	}
	
	public static Main getPlugin() {
		return plugin;
	}
	
	public static LuckPerms getLuckperms() {
		return luckperms;
	}
	
	public void loadPlugins() {
		//luckperms
		if(config.luckpermsEnable("general")) {
			RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
			if (provider != null) {
				luckperms = provider.getProvider();   
			}
		}		
	}
	
	private void initalBstats() {
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(plugin, 10354);
	}
	
	public static ConfigManager getConfigManager() {
		return config;
	}
	
	public static MessageManager getMessagesManager() {
		return messages;
	}
	
	public static DataManager getDataManager() {
		return data;
	}
	
	public static TeamManager getTeamsManager() {
		return teams;
	}
	
	public static LuckpermsManager getLuckpermsMananger() {
		return luckpermsmanager;
	}	
}
