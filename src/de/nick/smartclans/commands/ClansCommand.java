package de.nick.smartclans.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.messages.MessageManager;

public class ClansCommand implements CommandExecutor, TabCompleter{
	
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
		
		/*----NoClanSection----*/
		
		//clan create
		if(args[0].equalsIgnoreCase("create") && (args.length == 2)) {
			if(p.hasPermission("smartclans.create")) {
				if(data.isInClan(p)) {
					p.sendMessage(messages.get("player-already-in-Clan"));
					return false;
				}
				if(data.createClan(args[1].trim(), p)) {
					p.sendMessage(messages.getPrefix() + messages.getRaw("clan-created").replace("%clan%", args[1]).replace("%creator%", p.getName()));
				}else
					p.sendMessage(messages.get("clan-already-exist").replace("%clan%", args[1]));
			}else
				p.sendMessage(messages.get("no-permission"));
			return false;
		}
		/*----ClanSection----*/
		if(!data.isInClan(p)) {
			p.sendMessage(messages.get("player-not-in-Clan"));
			return false;
		}
			/*---ClanLeader---*/
			if(data.isLeader(p)) {
				//set clan description
				if(args[0].equalsIgnoreCase("set") && (args.length >= 2)) {
					if(args[1].equalsIgnoreCase("description") && (args.length > 3))  {
						StringBuilder text = new StringBuilder(256);
						for(int i = 0; i < args.length; i++) {
							if(i < 2) continue;
							text.append(args[i] + " ");
						}
						data.setClanData(data.getClan(p), "description", text.toString().trim());
						p.sendMessage(messages.get("clan-description-was-set"));
						return false;
					}
				}
				//add co leader
				if(args[0].equalsIgnoreCase("add") && (args.length > 2)) {
					if(args[1].equalsIgnoreCase("coleader") && (args.length == 3)) {
						Player target = Bukkit.getPlayer(args[2]);
						List<String> coleaders = data.getCoLeaders(data.getClan(p));
						//check if player online
						if(target == null) {
							p.sendMessage(messages.get("player-not-online").replace("%player%", args[2]));
							return false;
						}	
						//check if target in the same clan
						if(!data.getClan(target).equalsIgnoreCase(data.getClan(p))) {
							p.sendMessage(messages.get("player-not-in-same-clan").replace("%player%", target.getName()));
							return false;
						}
						//check if target already leader or co leader
						if(data.getPosition(p).equalsIgnoreCase("coleader") || data.getPosition(p).equalsIgnoreCase("leader")) {
							p.sendMessage(messages.get("player-already-coleader-or-leader").replace("%player%", target.getName()));
							return false;
						}
						coleaders.add(target.getUniqueId().toString());
						data.setClanData(data.getClan(p), "co-leaders", coleaders);
						data.setPlayerData(p, "position", "coleader");
						target.sendMessage(messages.get("you-are-now-coleader-of").replace("%clan%", data.getClan(p)));
						p.sendMessage(messages.get("co-leader-added").replace("%clan%", data.getClan(p)).replace("%coleader%", target.getName()));
						return false;
					}
						
				}
				
			}
		    /*---ClanCoLeader---*/
			
		//TODO wrong arguments
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<String>();
		//console + player
		
		
		if(!(s instanceof Player)) {
			switch (args.length) {
			default:
				break;
			}
			return completions;
		}
		//player
		Player p = (Player)s;
		
		//everytime
		
		//in Clan
		if(data.isInClan(p)) {
			switch (args.length) {
			case 1:
				if(data.isLeader(p)) completions.add("set");
				if(data.isLeader(p)) completions.add("add");
				break;
			case 2: 
				if(data.isLeader(p) && args[0].equalsIgnoreCase("set")) completions.add("description");
				if(data.isLeader(p) && args[0].equalsIgnoreCase("add")) completions.add("coleader");
				break;
			case 3:
				if(data.isLeader(p) && args[1].equalsIgnoreCase("coleader")) {
					for(Player target : Bukkit.getOnlinePlayers()) {
						completions.add(target.getName());
					}
				}
				break;
			default:
				break;
			}
			return completions;
		}
		
		//no in Clan
		switch (args.length) {
		case 1:
			if(s.hasPermission("smartclans.create")) completions.add("create");
			break;
		default:
			break;
		}
		return completions;
	}
	
	
	
}
