package de.nick.smartclans.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.nick.smartclans.messages.MessageManager;

public class ClansCommand implements CommandExecutor{
	
	private MessageManager messages;
	
	public ClansCommand() {
		messages = new MessageManager();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		/*-----ConsoleSection----*/
		
		/*----PlayerSection-----*/
		if(!(s instanceof Player)) {
			s.sendMessage(messages.get("command-exe-no-player"));
			return false;
		}
		
		
		return false;
	}
	
}
