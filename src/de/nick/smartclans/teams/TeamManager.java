package de.nick.smartclans.teams;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;

import de.nick.smartclans.data.DataManager;

public class TeamManager {

	private Scoreboard scoreboard;
	private DataManager data;
	public TeamManager() {
		scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
		data = new DataManager();
	}
	
	public void addToTeam(Player p) {
		data.loadPlayer();
		if(data.isInClan(p)) {
			scoreboard.getTeam(data.getClan(p)).addEntry(p.getName());
		}
	}
	
	public void removeFromTeam(Player p) {
		if(scoreboard.getEntryTeam(p.getName()) == null) return;
		scoreboard.getEntryTeam(p.getName()).removeEntry(p.getName());
	}
	
	public void addTeam(String name) {
		scoreboard.registerNewTeam(name);
	}
	
	public void removeTeam(String name) {
		if(scoreboard.getTeam(name) == null) return;
		scoreboard.getTeam(name).unregister();
	}
	
	public void addPlaceholder(String name) {
		scoreboard.getTeam(name).addEntry("placeholder-smartclans-" + name);
	}
	
}
