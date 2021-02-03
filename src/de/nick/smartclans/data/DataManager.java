package de.nick.smartclans.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.nick.smartclans.main.Main;

public class DataManager {

	private File clanfile;
	private YamlConfiguration clanconfig;
	
	private File playerfile;
	private YamlConfiguration playerconfig;
	
	public DataManager() {
		playerfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data", "playerdata.yml");
		playerconfig = YamlConfiguration.loadConfiguration(playerfile);
	}
	
	public boolean createClan(String clanname, Player leader) {
		clanfile = new File(Main.getPlugin().getDataFolder() + File.separator + "data" + File.separator + "clans", clanname + ".yml");
		clanconfig = YamlConfiguration.loadConfiguration(clanfile);
		
		if(clanfile.exists()) return false;
		clanconfig.set("name", clanname);
		clanconfig.set("description", "!empty");
		clanconfig.set("leader", leader.getUniqueId().toString());
		clanconfig.set("co-leader", new ArrayList<String>());
		List<String> members = clanconfig.getStringList("members");
		members.add(leader.getUniqueId().toString());
		clanconfig.set("members", members);
		clanconfig.set("banned", new ArrayList<String>());
		saveClanFile(clanfile);
		savePlayerData();
		setPlayerData(leader, clanname);
		savePlayerData();
		return true;
	}
	
	public void setPlayerData(Player p, String clan) {
		playerconfig.set(p.getUniqueId().toString() + ".clan", clan);
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
}
