/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.main;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.nick.smartclans.commands.*;
import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.listener.PlayerJoinListener;
import de.nick.smartclans.listener.PlayerLeaveListener;
import de.nick.smartclans.messages.MessageManager;
import net.luckperms.api.LuckPerms;

public class Main extends JavaPlugin{
	
	private static Main plugin;
	private MessageManager messages;
	private static LuckPerms luckperms;
	private ConfigManager config;
	
	public void onEnable() {
		//start
		
		//general
		plugin = this;
		messages = new MessageManager();
		config = new ConfigManager();		
		
		/*-----files-----*/
		if(!new File(getDataFolder(), "config.yml").exists()) {
			saveDefaultConfig();
		}
		if(!messages.getFile().exists()) {
			messages.saveDefaults();
		}
		/*--checkversions--*/
		if(messages.getVersion() != 1) {
			Bukkit.getConsoleSender().sendMessage("[SmartClans] §4Old Message File, please update!");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		if(config.getVersion() != 2) {
			Bukkit.getConsoleSender().sendMessage("[SmartClans] §4Old Config File, please update!");
			Bukkit.getPluginManager().disablePlugin(plugin);
			return;
		}
		
		/*-----Plugins-----*/
		loadPlugins();
		
		//commnds
		ClansCommand clanscommand = new ClansCommand();
		getCommand("clans").setExecutor(clanscommand);
		getCommand("clans").setTabCompleter(clanscommand);
		
		//events
		PluginManager pm = Bukkit.getPluginManager();
		pm.registerEvents(new PlayerJoinListener(), plugin);
		pm.registerEvents(new PlayerLeaveListener(), plugin);
		
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
	
}
