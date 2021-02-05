package de.nick.smartclans.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.nick.smartclans.main.Main;

public class DataManager {

	private File clanfile;
	private YamlConfiguration clanconfig;
	
	private File playerfile;
	private YamlConfiguration playerconfig;
	
	private HashMap<String, YamlConfiguration> clansconfigs;
	private HashMap<String, File> clansfiles;
	
	public DataManager() {
		playerfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data", "playerdata.yml");
		playerconfig = YamlConfiguration.loadConfiguration(playerfile);
	}
	
	public void loadClans() {
		File dir = new File(Main.getPlugin().getDataFolder() + File.separator + "data" + File.separator + "clans");
		File[] files = dir.listFiles();
		if(files == null) return;
		clansconfigs = new HashMap<String, YamlConfiguration>();
		clansfiles = new HashMap<String, File>();
		for(int i = 0; i < files.length; i++) {
			clansconfigs.put(files[i].getName(), YamlConfiguration.loadConfiguration(files[i]));
			clansfiles.put(files[i].getName(), files[i]);
		}
	}
	
	public boolean createClan(String clanname, Player leader) {	
		clanfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data" + File.separator + "clans", clanname + ".yml");
		clanconfig = YamlConfiguration.loadConfiguration(clanfile);	
		if(clanfile.exists()) return false;	
		clanconfig.set("name", clanname);
		clanconfig.set("description", "!empty");
		clanconfig.set("leader", leader.getUniqueId().toString());
		clanconfig.set("co-leaders", new ArrayList<String>());
		List<String> members = clanconfig.getStringList("members");
		members.add(leader.getUniqueId().toString());
		clanconfig.set("members", members);
		clanconfig.set("banned", new ArrayList<String>());
		saveClanFile(clanfile);
		clansconfigs.put(clanfile.getName(), clanconfig);
		clansfiles.put(clanfile.getName(), clanfile);
		setPlayerData(leader, "clan", clanname);
		setPlayerData(leader, "position", "leader");
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
		clanconfig = clansconfigs.get(clan + ".yml");
		clanfile = clansfiles.get(clan + ".yml");
		clanconfig.set(path, value);
		saveClanFile(clanfile);
	}
	
	public Object getClanData(String clan, String path) {
		clanconfig = clansconfigs.get(clan + ".yml");
		return clanconfig.get(path);
	}
	
	public List<String> getCoLeaders(String clan) {
		clanconfig = clansconfigs.get(clan + ".yml");
		return clanconfig.getStringList("co-leaders");
	}
	
	public String getPosition(Player p) {
		return playerconfig.getString(p.getUniqueId().toString() + ".position");
	}
	
	public void deleteClan(String clan) {
		clanconfig = clansconfigs.get(clan + ".yml");
		clanfile = clansfiles.get(clan + ".yml");
		for(String uuid : clanconfig.getStringList("members")) {
			playerconfig.set(uuid + ".clan", null);
			playerconfig.set(uuid + ".position", null);
		}
		savePlayerData();
		clansconfigs.remove(clan + ".yml");
		clansfiles.remove(clan + ".yml");
		clanfile.delete();
	}
}
