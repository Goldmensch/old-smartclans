/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.main.Main;
import de.nick.smartclans.teams.TeamManager;

public class PlayerLeaveListener implements Listener{
	
	private ConfigManager config;
	private TeamManager teams;
	
	public PlayerLeaveListener() {
		config = Main.getConfigManager();
		teams = Main.getTeamsManager();
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		if(config.teamsEnable()) {
			teams.removeFromTeam(e.getPlayer());
		}
	}
}
