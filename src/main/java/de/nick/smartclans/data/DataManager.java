/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.nick.smartclans.api.events.AddCoLeaderEvent;
import de.nick.smartclans.api.events.ClanCreateCompleteEvent;
import de.nick.smartclans.api.events.ClanCreateEvent;
import de.nick.smartclans.api.events.ClanDeleteEvent;
import de.nick.smartclans.api.events.MemberAddEvent;
import de.nick.smartclans.api.events.MemberRemoveEvent;
import de.nick.smartclans.api.events.RemoveCoLeaderEvent;
import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.main.Main;
import de.nick.smartclans.plugins.luckperms.LuckpermsManager;
import de.nick.smartclans.teams.TeamManager;

public class DataManager {

	private File clanfile;
	private YamlConfiguration clanconfig;
	
	private File playerfile;
	private YamlConfiguration playerconfig;
	
	private ConfigManager config;
	private TeamManager teams;
	
	private LuckpermsManager luckperms;
	
	private HashMap<String, YamlConfiguration> clansconfigs;
	private HashMap<String, File> clansfiles;

	private List<String> clannames;
	
	public void loadClans() {
		config = Main.getConfigManager();
		teams = Main.getTeamsManager();
		clansconfigs = new HashMap<String, YamlConfiguration>();
		clansfiles = new HashMap<String, File>();
		clannames = new ArrayList<>();
		Bukkit.getConsoleSender().sendMessage("[" + Main.getPlugin().getDescription().getPrefix() + "] start loading the clan configs...");
		File dir = new File(Main.getPlugin().getDataFolder() + File.separator + "data" + File.separator + "clans");
		File[] files = dir.listFiles();
		if(files == null) {
			Bukkit.getConsoleSender().sendMessage("[" + Main.getPlugin().getDescription().getPrefix() +  "] 0 §rclans loaded.");
			return;
		}
		for(int i = 0; i < files.length; i++) {
			clansconfigs.put(files[i].getName().toLowerCase(), YamlConfiguration.loadConfiguration(files[i]));
			clansfiles.put(files[i].getName().toLowerCase(), files[i]);
			clannames.add(files[i].getName().replace(".yml", ""));
		}
		Bukkit.getConsoleSender().sendMessage("[" + Main.getPlugin().getDescription().getPrefix() +  "] " + files.length + " §rclans loaded.");
	}
	
	public boolean createClan(String clanname, Player leader) {	
		clanfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data" + File.separator + "clans", clanname + ".yml");
		clanconfig = YamlConfiguration.loadConfiguration(clanfile);	
		if(existClan(clanname)) return false;
		Bukkit.getPluginManager().callEvent(new ClanCreateEvent(clanname, leader));
		clanconfig.set("name", clanname);
		clanconfig.set("description", "!empty");
		clanconfig.set("public", config.getStandartPublic());
		clanconfig.set("leader", leader.getUniqueId().toString());
		clanconfig.set("co-leaders", new ArrayList<String>());
		clanconfig.set("friendlyfire", true);
		List<String> members = clanconfig.getStringList("members");
		members.add(leader.getUniqueId().toString());
		clanconfig.set("members", members);
		clanconfig.set("banned", new ArrayList<String>());
		saveClanFile(clanfile);
		clansconfigs.put(clanfile.getName().toLowerCase(), clanconfig);
		clansfiles.put(clanfile.getName().toLowerCase(), clanfile);
		setPlayerData(leader, "clan", clanname);
		setPlayerData(leader, "position", "leader");
		if(config.teamsEnable()) {
			teams.addTeam(clanname);
			teams.addToTeam(leader);
			teams.addPlaceholder(clanname);
		}
		//luckperms
		if(config.luckpermsEnable("general")) {
			luckperms = Main.getLuckpermsMananger();
			luckperms.addPlayerToLeader(leader);
		}
		Bukkit.getPluginManager().callEvent(new ClanCreateCompleteEvent(clanname, leader));
		return true;
	}
	
	public void setPlayerData(Player p, String path, Object value) {
		playerconfig.set(p.getUniqueId().toString() + "." + path, value);
		savePlayerData();
	}
	
	public void savePlayerData() {
			try {
				playerconfig.save(playerfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
	}

	public void saveClanFile(File file) {
		try {
			clanconfig.save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isInClan(Player p) {
		if(p == null) {
			return false;
		}
		if(playerconfig.contains(p.getUniqueId().toString() + ".clan")) {
			return true;
		}else
			return false;
	}
	
	public boolean isLeader(Player p) {
		if(playerconfig.getString(p.getUniqueId().toString() + ".position").equalsIgnoreCase("leader")) {
			return true;
		}else
			return false;
	}
	
	public String getClan(Player p) {
		return playerconfig.getString(p.getUniqueId().toString() + ".clan");
	}
	
	public void setClanData(String clan, String path, Object value) {
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		clanfile = clansfiles.get(clan.toLowerCase() + ".yml");
		clanconfig.set(path, value);
		saveClanFile(clanfile);
	}
	
	public Object getClanData(String clan, String path) {
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		return clanconfig.get(path);
	}
	
	public List<String> getCoLeaders(String clan) {
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		return clanconfig.getStringList("co-leaders");
	}
	
	public String getPosition(Player p) {
		return playerconfig.getString(p.getUniqueId().toString() + ".position");
	}
	
	public void deleteClan(String clan) {
		teams.removeTeam(clan);
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		clanfile = clansfiles.get(clan.toLowerCase() + ".yml");
		for(String uuid : clanconfig.getStringList("members")) {
			playerconfig.set(uuid + ".clan", null);
			playerconfig.set(uuid + ".position", null);
		}
		savePlayerData();
		clansconfigs.remove(clan.toLowerCase() + ".yml");
		clansfiles.remove(clan.toLowerCase() + ".yml");
		clanfile.delete();
		Bukkit.getPluginManager().callEvent(new ClanDeleteEvent(clan));
	}
	
	public void loadPlayer() {
		playerfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data", "playerdata.yml");
		playerconfig = YamlConfiguration.loadConfiguration(playerfile);
	}
	
	public List<String> getMembers(String clan) {
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		return clanconfig.getStringList("members");
	}
	
	public boolean existClan(String clan) {
		if(clansconfigs.containsKey(clan.toLowerCase() + ".yml")) {
			return true;
		}else
			return false;
	}
	
	public boolean isCoLeader(Player p) {
		if(getPosition(p).equalsIgnoreCase("coleader") || getPosition(p).equalsIgnoreCase("leader")) {
			return true;
		}else
			return false;
	}
	
	public void addMember(String clan, Player member) {
		if(config.teamsEnable()) {
			teams.addToTeam(member);
		}
		List<String> members = getMembers(clan);
		members.add(member.getUniqueId().toString());
		setClanData(clan, "members", members);
		setPlayerData(member, "clan", clan);
		setPlayerData(member, "position", "member");
		teams.addToTeam(member);
		Bukkit.getPluginManager().callEvent(new MemberAddEvent(clan, member));
	}
	
	public void removeMember(String clan, Player member) {
		if(config.teamsEnable()) {
			teams.removeFromTeam(member);
		}
		removeCoLeader(clan, member);
		List<String> members = getMembers(clan);
		members.remove(member.getUniqueId().toString());
		setClanData(clan, "members", members);
		setPlayerData(member, "clan", null);
		setPlayerData(member, "position", null);
		teams.removeFromTeam(member);
		Bukkit.getPluginManager().callEvent(new MemberRemoveEvent(clan, member));
	}
	
	public TeamManager getTeamManager() {
		return teams;
		
	}
	
	public void addBan(String clan, Player p) {
		List<String> banned = getBanned(clan);
		banned.add(p.getUniqueId().toString());
		setClanData(clan, "banned", banned);
	}
	
	public List<String> getBanned(String clan) {
		clanconfig = clansconfigs.get(clan.toLowerCase() + ".yml");
		
		return clanconfig.getStringList("banned");
	}
	
	public void removeBan(String clan, Player p) {
		List<String> banned = getBanned(clan);
		banned.remove(p.getUniqueId().toString());
		setClanData(clan, "banned", banned);
	}
	
	public void addCoLeader(String clan, Player p) {
		List<String> coleaders = getCoLeaders(clan);
		coleaders.add(p.getUniqueId().toString());
		setClanData(clan, "co-leaders", coleaders);
		setPlayerData(p, "position", "coleader");
		Bukkit.getPluginManager().callEvent(new AddCoLeaderEvent(clan, p));
	}
	
	public void removeCoLeader(String clan, Player p) {
		List<String> coleaders = getCoLeaders(clan);
		coleaders.remove(p.getUniqueId().toString());
		setClanData(clan, "co-leaders", coleaders);
		setPlayerData(p, "position", "member");
		Bukkit.getPluginManager().callEvent(new RemoveCoLeaderEvent(clan, p));
	}
	
	public boolean isPublic(String clan) {
		return (boolean) getClanData(clan, "public");
	}

	public String getName(String clan) {
		return (String) getClanData(clan, "name");
	}

	public List<String> getClannames() {
		return clannames;
	}
}
