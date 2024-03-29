/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.api;

import de.nick.smartclans.api.data.ClansAPI;
import de.nick.smartclans.api.data.PlayerAPI;

public class SmartclansAPI {

	private ClansAPI clanapi;
	private PlayerAPI playerapi;
	
	public SmartclansAPI() {
		clanapi = new ClansAPI();
		playerapi = new PlayerAPI();
	}
	
	public ClansAPI getClanapi() {
		return clanapi;
	}
	
	public PlayerAPI getPlayerapi() {
		return playerapi;
	}
	
	
	
}
