package de.nick.smartclans.api.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClanCreateCompleteEvent extends Event{

	private final String clanname;
	private final Player p;
	private static final HandlerList HANDLERS = new HandlerList();
	
	public ClanCreateCompleteEvent(String clanname, Player p) {
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
