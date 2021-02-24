/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.nick.smartclans.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.main.Main;
import de.nick.smartclans.messages.MessageManager;

public class ClanChatCommand implements CommandExecutor{

	MessageManager messages;
	DataManager data;
	ConfigManager config;
	
	public ClanChatCommand() {
		messages = Main.getMessagesManager();
		data = Main.getDataManager();
		config = Main.getConfigManager();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command  cmd, String label, String[] args) {
		if(!config.clanchatEnable()) {
			s.sendMessage(messages.get("clanchat-disabled"));
			return false;
		}
		
		if(!(s instanceof Player)) {
			s.sendMessage(messages.get("command-exe-no-player"));
			return false;
		}
		if(args.length == 0) {
			s.sendMessage(messages.getPrefix() + "§cusage: /cc <nachricht>");
			return false;
		}
		Player p = (Player)s;
		StringBuilder msg = new StringBuilder();
		msg.append(messages.getRaw("clanchat-prefix").replace("%name%", p.getName()));
		for(int i = 0; i < args.length; i++) {
			msg.append(args[i]);
			msg.append(" ");
		}
		if(data.isInClan(p)) {
			Bukkit.getScheduler().runTaskAsynchronously(Main.getPlugin(), new Runnable() {
				
				@Override
				public void run() {
					for(String uuid : data.getMembers(data.getClan(p))) {
						Player target = Bukkit.getPlayer(UUID.fromString(uuid));
						if(target == null) continue;
						target.sendMessage(msg.toString());
					}
				}
			});
			return false;
		}else {
			p.sendMessage(messages.get("you-not-in-Clan"));
			return false;
		}
	}

}
