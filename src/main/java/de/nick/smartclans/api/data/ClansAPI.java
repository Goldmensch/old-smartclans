/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.api.data;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.main.Main;

public class ClansAPI {
	
	DataManager data;
	
	public ClansAPI() {
		data = Main.getDataManager();
	}
	
	public List<String> getMembersAsUUIDList(String clan) {
		return data.getMembers(clan);
	}
	
	public String getLeaderAsUUID(String clan) {
		return data.getClanData(clan, "leader").toString();
	}
	
	public OfflinePlayer getLeaderAsOfflinePlayer(String clan) {
		return Bukkit.getOfflinePlayer(UUID.fromString(data.getClanData(clan, "leader").toString()));
	}
	
	public String getClanDescription(String clan) {
		return data.getClanData(clan, "description").toString();
	}
	
	public List<String> getBannedAsUUIDList(String clan) {
		return data.getBanned(clan);
	}
	
	public List<String> getCoLeadersAsUUIDList(String clan) {
		return data.getBanned(clan);
	}
	
	public boolean getFriendlyFire(String clan) {
		return Boolean.valueOf(data.getClanData(clan, "friendlyfire").toString());
	}

	public boolean exists(String clan) {
		return data.existClan(clan);
	}
	
}
