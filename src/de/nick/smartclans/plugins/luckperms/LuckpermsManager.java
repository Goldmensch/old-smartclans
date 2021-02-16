/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/
package de.nick.smartclans.plugins.luckperms;

import java.util.concurrent.ExecutionException;

import org.bukkit.entity.Player;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.main.Main;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.node.types.PermissionNode;

public class LuckpermsManager {
	
	private ConfigManager config;
	
	public LuckpermsManager() {
		config = new ConfigManager();
	}
	
	public void setupLeaderGroup() {
		Group group = null;
		try {
			group = Main.getLuckperms().getGroupManager().createAndLoadGroup(config.getluckpermsGroupName("leader")).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		for(String perm : config.getluckperms("leader")) {
			PermissionNode node = PermissionNode.builder(perm).build();
			if(!group.getNodes().contains(node)) group.data().add(node);
		}
		Main.getLuckperms().getGroupManager().saveGroup(group);
	}
	
	public void addPlayerToLeader(Player p) {
		Group group = Main.getLuckperms().getGroupManager().getGroup(config.getluckpermsGroupName("leader"));
		User user = Main.getLuckperms().getUserManager().getUser(p.getUniqueId());
		InheritanceNode node = InheritanceNode.builder(group).build();
		user.data().add(node);
		Main.getLuckperms().getUserManager().saveUser(user);
	}
	
	public void removePlayerFromLeader(Player p) {
		Group group = Main.getLuckperms().getGroupManager().getGroup(config.getluckpermsGroupName("leader"));
		User user = Main.getLuckperms().getUserManager().getUser(p.getUniqueId());
		InheritanceNode node = InheritanceNode.builder(group).build();
		user.data().remove(node);
		Main.getLuckperms().getUserManager().saveUser(user);
	}
	
}
