/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.api.data;

import org.bukkit.entity.Player;

import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.main.Main;

public class PlayerAPI {

	DataManager data;
	
	public PlayerAPI() {
		data = Main.getDataManager();
	}
	
	public String getClannameAsString(Player p) {
		return data.getClan(p);
	}
	
	public boolean isInClan(Player p) {
		return data.isInClan(p);
	}
	
	public String getPosition(Player p) {
		return data.getPosition(p);
	}
	
}
