package de.nick.smartclans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.teams.TeamManager;

public class PlayerJoinListener implements Listener {

	private ConfigManager config;
	private TeamManager teams;
	public PlayerJoinListener() {
		config = new ConfigManager();
		teams = new TeamManager();
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if(config.teamsEnable()) {
			teams.addToTeam(e.getPlayer());
		}
	}
}
