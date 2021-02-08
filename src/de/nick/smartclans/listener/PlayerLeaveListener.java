package de.nick.smartclans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.teams.TeamManager;

public class PlayerLeaveListener implements Listener{
	
	private ConfigManager config;
	private TeamManager teams;
	
	public PlayerLeaveListener() {
		config = new ConfigManager();
		teams = new TeamManager();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		if(config.teamsEnable()) {
			teams.removeFromTeam(e.getPlayer());
		}
	}
}
