/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.config;

import de.nick.smartclans.main.Main;

public class ConfigManager {

	Main main;
	
	public ConfigManager() {
		main = Main.getPlugin();
	}
	
	public void saveDefaults() {
		main.saveDefaultConfig();
	}
	
	public void reload() {
		main.reloadConfig();
	}
	
	public int getInt(String path) {
		return main.getConfig().getInt(path);
	}
	
	public boolean teamsEnable() {
		return main.getConfig().getBoolean("use-minecraft-teams");
	}
	
}
