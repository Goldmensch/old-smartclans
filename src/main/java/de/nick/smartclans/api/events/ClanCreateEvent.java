/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClanCreateEvent extends Event{

	private final String clanname;
	private final Player p;
	private static final HandlerList HANDLERS = new HandlerList();
	
	public ClanCreateEvent(String clanname, Player p) {
		this.clanname = clanname;
		this.p = p;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
        return HANDLERS;
    }
	
	public String getClanname() {
		return clanname;
	}
	
	public Player getCreator() {
		return p;
	}
}
