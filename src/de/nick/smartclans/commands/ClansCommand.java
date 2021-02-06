package de.nick.smartclans.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
		data.loadClans();
		data.loadPlayer();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			//TODO wrong arguments
			return false;
		}
		/*-----ConsoleAndPlayerSection----*/
		//reload
		if(args[0].equalsIgnoreCase("reload") && (args.length == 1)) {
			if(s.hasPermission("smartclans.reload")) {
				data.loadClans();
				messages.load();
				data.loadPlayer();
				s.sendMessage(messages.get("plugin-reloaded"));
			}else
				s.sendMessage(messages.get("no-permission"));
			return false;
		}
		//help
		if(args[0].equalsIgnoreCase("help")) {
			for(String msg : getHelp(s)) {
				s.sendMessage(msg);
			}
			return false;
		}
		//info
		if(args[0].equalsIgnoreCase("info") && (args.length == 2)) {
			if(s.hasPermission("smartclans.info.others")) {
				if(data.existClan(args[1])) {
					for(String msg : getClanInfo(args[1])) {
						s.sendMessage(msg);
					}
				}else
					s.sendMessage(messages.get("clan-not-exist"));
			}else
				s.sendMessage(messages.get("no-permission"));
			return false;
		}
		/*----PlayerSection-----*/
		if(!(s instanceof Player)) {
			for(String msg : getHelp(s)) {
				s.sendMessage(msg);
			}
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
			for(String msg : getHelp(s)) {
				p.sendMessage(msg);
			}
			return false;
		}
		    /*---members---*/
			if(args[0].equalsIgnoreCase("info") && (args.length == 1)) {
				if(s.hasPermission("smartclans.info")) {
					for(String msg : getClanInfo(data.getClan(p))) {
						p.sendMessage(msg);
					}
				}else
					p.sendMessage(messages.get("no-permission"));
				return false;
			}
		
			/*---ClanLeader---*/
			if(data.isLeader(p)) {
				
				//set clan description
				if(args[0].equalsIgnoreCase("set") && (args.length >= 2)) {
					if(args[1].equalsIgnoreCase("description") && (args.length > 2))  {
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
				//delete clan
				if(args[0].equalsIgnoreCase("delete") && (args.length == 2)) {
					if(p.hasPermission("smartclans.delete")) {
						if(args[1].equalsIgnoreCase(data.getClan(p))) {
							p.sendMessage(messages.get("clan-deleted").replace("%clan%", data.getClan(p)));
							data.deleteClan(data.getClan(p));
						}else
							p.sendMessage(messages.get("confirmname-not-match"));
					}else
						p.sendMessage(messages.get("no-permission"));
					return false;
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
			
		for(String msg : getHelp(s)) {
			p.sendMessage(msg);
		}
		return false;
	}
	
	public List<String> getClanInfo(String clan) {
		List<String> info = new ArrayList<String>();
		List<String> membernames = new ArrayList<String>();
		List<String> coleadernames = new ArrayList<String>();
		
		for(String uuid : data.getCoLeaders(clan)) {
			coleadernames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
		}
		
		for(String uuid : data.getMember(clan)) {
			membernames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
		}
		
		info.add(messages.getPrefix() + "§6----------------ClansInfo---------------");
		info.add(messages.getPrefix() + "§8name: §7" + clan);
		info.add(messages.getPrefix() + "§8description: §7" + data.getClanData(clan, "description"));
		info.add(messages.getPrefix() + "§8leader: §7" + Bukkit.getOfflinePlayer(UUID.fromString(data.getClanData(clan, "leader").toString())).getName());
		info.add(messages.getPrefix() + "§8coleader: §7" + coleadernames);
		info.add(messages.getPrefix() + "§8members: §7" + membernames);
		return info;
	}
	
	public List<String> getHelp(CommandSender s) {
		List<String> help = new ArrayList<String>();
		help.add(messages.getPrefix() + "§6----------------ClansHelp---------------");
		
		//console + player
		help.add(messages.getPrefix() + "§8/clans help");
		if(s.hasPermission("smartclans.reload")) help.add(messages.getPrefix() + "§8/clans reload");
		//console
		if(!(s instanceof Player)) {
			if(s.hasPermission("smartclans.info.others")) help.add(messages.getPrefix() + "§8/clans info <clanname>");
		}
		
		//player
		if(!(s instanceof Player)) return help;
		Player p = (Player)s;
		
		if(s.hasPermission("smartclans.info.others")) {
			help.add(messages.getPrefix() + "§8/clans info (clananname)");
		}else if(s.hasPermission("smartclans.info") && data.isInClan(p)) {
			help.add(messages.getPrefix() + "§8/clans info");
		}
		
		//not in clan
		if(!data.isInClan(p)) {
			if(s.hasPermission("smartclans.create")) help.add(messages.getPrefix() + "§8/clans create <clanname>");
			return help;
		}
		
		//in clan
		
			//clanleader
			if(data.isLeader(p)) {
				if(s.hasPermission("smartclans.delete")) help.add(messages.getPrefix() + "§8/clans delete <clanname>");
					help.add(messages.getPrefix() + "§8/clans add coleader <playername>");
					help.add(messages.getPrefix() + "§8/clean set description <description>");
			}
			//TODO clancoleader
			return help;
	}

	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<String>();
		//console + player
		switch (args.length) {
		case 1:
			if("help".startsWith(args[0])) completions.add("help");
			if(s.hasPermission("smartclans.reload")) completions.add("reload");
			if(s.hasPermission("smartclans.info") && "info".startsWith(args[0])) completions.add("info");
			break;

		default:
			break;
		}
		
		//player
		if(!(s instanceof Player)) {
			switch (args.length) {
			default:
				break;
			}
			return completions;
		}
		Player p = (Player)s;
		
		//in Clan
		if(data.isInClan(p)) {
			switch (args.length) {
			case 1:
				if(data.isLeader(p) && "set".startsWith(args[0])) completions.add("set");
				if(data.isLeader(p) && "add".startsWith(args[0])) completions.add("add");
				if(data.isLeader(p) && "delete".startsWith(args[0])) completions.add("delete");
				break;
			case 2: 
				if(data.isLeader(p) && args[0].equalsIgnoreCase("set") && "description".startsWith(args[1])) completions.add("description");
				if(data.isLeader(p) && args[0].equalsIgnoreCase("add") && "coleader".startsWith(args[1])) completions.add("coleader");
				break;
			case 3:
				if(data.isLeader(p) && args[1].equalsIgnoreCase("coleader")) {
					for(Player target : Bukkit.getOnlinePlayers()) {
						if(!target.getName().startsWith(args[2])) continue;
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
			if(s.hasPermission("smartclans.create") && "create".startsWith(args[0])) completions.add("create");
			break;
		default:
			break;
		}
		return completions;
	}
	
	
	
}
