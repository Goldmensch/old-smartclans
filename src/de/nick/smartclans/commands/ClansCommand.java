package de.nick.smartclans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.messages.MessageManager;

public class ClansCommand implements CommandExecutor{
	
	private MessageManager messages;
	private DataManager data;
	
	public ClansCommand() {
		messages = new MessageManager();
		data = new DataManager();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			//TODO wrong arguments
			return false;
		}
		/*-----ConsoleSection----*/
		
		/*----PlayerSection-----*/
		if(!(s instanceof Player)) {
			s.sendMessage(messages.get("command-exe-no-player"));
			return false;
		}
		Player p = (Player)s;
		
		if(args[0].equalsIgnoreCase("create") && (args.length == 2)) {
			if(p.hasPermission("easyclans.create")) {
				if(data.isInClan(p)) {
					p.sendMessage(messages.get("player-already-in-Clan"));
					return false;
				}
				if(data.createClan(args[1], p)) {
					p.sendMessage(messages.getPrefix() + messages.getRaw("clan-created").replace("%clan%", args[1]).replace("%creator%", p.getName()));
				}else
					p.sendMessage(messages.get("clan-already-exist").replace("%clan%", args[1]));
			}else
				p.sendMessage(messages.get("no-permission"));
			return false;
		}
		
		//TODO wrong arguments
		return false;
	}
	
}
