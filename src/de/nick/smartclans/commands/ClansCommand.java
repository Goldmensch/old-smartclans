/*Copyright (C) <2020> <Nick Hensel>
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, version 3.

This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.

You should have received a copy of the GNU General Public License along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package de.nick.smartclans.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.nick.smartclans.config.ConfigManager;
import de.nick.smartclans.data.DataManager;
import de.nick.smartclans.main.Main;
import de.nick.smartclans.messages.MessageManager;

public class ClansCommand implements CommandExecutor, TabCompleter{
	
	private MessageManager messages;
	private DataManager data;
	private HashMap<String, String> invites;
	private ConfigManager config;
	
	public ClansCommand() {
		messages = new MessageManager();
		data = new DataManager();
		config = new ConfigManager();
		invites = new HashMap<String, String>();
		data.loadClans();
		data.loadPlayer();
	}
	
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			for(String msg : getHelp(s)) {
				s.sendMessage(msg);
			}
			return false;
		}
		/*-----ConsoleAndPlayerSection----*/
		//reload
		if(args[0].equalsIgnoreCase("reload") && (args.length == 1)) {
			if(s.hasPermission("smartclans.reload")) {
				data.loadClans();
				messages.load();
				data.loadPlayer();
				if(!new File(Main.getPlugin().getDataFolder(), "config.yml").exists()) {
					Main.getPlugin().saveDefaultConfig();
				}else
					config.reload();
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
		if(!data.isInClan(p)) {
			//clan create
			if(args[0].equalsIgnoreCase("create") && (args.length == 2)) {
				if(p.hasPermission("smartclans.create")) {
					if(data.isInClan(p)) {
						p.sendMessage(messages.get("you-already-in-Clan"));
						return false;
					}
					if(args[1].trim().length() > config.getInt("max-clanname-lenght")) {
						p.sendMessage(messages.get("clanname-to-long").replace("%lenght%", String.valueOf(config.getInt("max-clanname-lenght"))));
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
		
			//accept invite
			if(args[0].equalsIgnoreCase("accept") && (args.length == 1)) {
				if(invites.containsKey(p.getName())) {
					data.addMember(invites.get(p.getName()), p);
					invites.remove(p.getName());
					p.sendMessage(messages.get("you-joined").replace("%clan%", data.getClan(p)));
					for(String uuid : data.getMembers(data.getClan(p))) {
						Player target = Bukkit.getPlayer(uuid);
						if(target == null) continue;
						target.sendMessage(messages.get("player-joined").replace("%player%", p.getName()));
					}
					return false;
				}else
					p.sendMessage(messages.get("no-invitation"));
				return false;
			}
			
			//decline invite
			if(args[0].equalsIgnoreCase("decline") && (args.length == 1)) {
				if(invites.containsKey(p.getName())) {
					p.sendMessage(messages.get("decline-invite").replace("%clan%", invites.get(p.getName())));
					invites.remove(p.getName());
					return false;
				}else
					p.sendMessage(messages.get("no-invitation"));
				return false;
			}
			
			//Section - end
			for(String msg : getHelp(s)) {
				p.sendMessage(msg);
			}
			return false;
		}
		/*----ClanSection----*/
		
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
			
			if(args[0].equalsIgnoreCase("leave") && (args.length == 1)) {
				if(p.hasPermission("smartclans.leave")) {
					if(data.isLeader(p)) {
						p.sendMessage(messages.get("you-cant-leave"));
						return false;
					}
					data.removeMember(data.getClan(p), p);
					p.sendMessage(messages.get("you-left").replace("%clan%", p.getName()));
					for(String uuid : data.getMembers(data.getClan(p))) {
						Player target = Bukkit.getPlayer(uuid);
						if(target == null) continue;
						target.sendMessage(messages.get("player-left").replace("%player%", p.getName()));
					}
					return false;
				}else
					p.sendMessage(messages.get("no-permission"));
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
				
				//add coleader
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
						data.setPlayerData(target, "position", "coleader");
						target.sendMessage(messages.get("you-are-now-coleader-of").replace("%clan%", data.getClan(p)));
						p.sendMessage(messages.get("co-leader-added").replace("%clan%", data.getClan(p)).replace("%coleader%", target.getName()));
						return false;
					}
				}
					
				//remove coleader
				if(args[0].equalsIgnoreCase("remove") && (args.length >= 2)) {
					if(args[1].equalsIgnoreCase("coleader") && (args.length == 3)) {
						Player target = Bukkit.getPlayer(args[2]);
						if(target == null) {
							p.sendMessage(messages.get("player-not-online").replace("%player%", args[2]));
							return false;
						}
						if(data.isInClan(target)) {
							if(!data.getClan(target).equalsIgnoreCase(data.getClan(p))) {
								p.sendMessage(messages.get("player-not-in-same-clan").replace("%player%", target.getName()));
								return false;
							}
						}else {
							p.sendMessage(messages.get("player-not-in-clan").replace("%player%", target.getName()));
							return false;
						}
						if(!data.getPosition(p).equalsIgnoreCase("coleader")) {
							p.sendMessage(messages.get("player-not-coleader").replace("%player%", target.getName()));
							return false;
						}
						List<String> coleaders = data.getCoLeaders(data.getClan(p));
						coleaders.remove(target.getUniqueId().toString());
						data.setClanData(data.getClan(p), "co-leaders", coleaders);
						data.setPlayerData(target, "position", "member");
						p.sendMessage(messages.get("coleader-removed").replace("%player%", target.getName()));
						return false;
					}
				}
				
			}
		    /*---ClanCoLeader---*/
			if(data.isCoLeader(p)) {
				//member invite
				if(args[0].equalsIgnoreCase("invite") && (args.length == 2)) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null) {
						p.sendMessage(messages.get("player-not-online").replace("%player%", args[1]));
						return false;
					}	
					if(data.isInClan(target)) {
						p.sendMessage(messages.get("player-already-in-clan").replace("%player%", target.getName()));
						return false;
					}
					if(invites.containsKey(target.getName())) {
						p.sendMessage(messages.get("player-already-invited").replace("%player%", p.getName()).replace("%clan%", invites.get(target.getName())));
						return false;
					}
					invites.put(target.getName(), data.getClan(p));
					Bukkit.getScheduler().runTaskLater(Main.getPlugin(), new Runnable() {
						
						@Override
						public void run() {
							if(invites.containsKey(target.getName())) {
								target.sendMessage(messages.get("invite-expired").replace("%clan%", invites.get(target.getName())));
								invites.remove(target.getName());
							}
							
						}
					}, config.getInt("invite-expires-after") * 60 * 20);
					target.sendMessage(messages.get("you-invited-to").replace("%clan%", data.getClan(p)));
					p.sendMessage(messages.get("you-invited-other").replace("%player%", target.getName()));
					return false;
				}
				//togglefriendlyfire
				if(args[0].equalsIgnoreCase("toggle") && (args.length >= 2)) {
					if(args[1].equalsIgnoreCase("friendlyfire") && (args.length == 2)) {
						if(Boolean.valueOf(data.getClanData(data.getClan(p), "friendlyfire").toString()) == true) {
							data.setClanData(data.getClan(p), "friendlyfire", false);
							data.getTeamManager().setFriendlyFire(data.getClan(p), false);
							p.sendMessage(messages.get("friendlyfire-disable"));
						}else {
							data.setClanData(data.getClan(p), "friendlyfire", true);
							data.getTeamManager().setFriendlyFire(data.getClan(p), true);
							p.sendMessage(messages.get("friendlyfire-enable"));
						}
						return false;
					}
				}
				//kick members
				if(args[0].equalsIgnoreCase("kick") && (args.length == 2)) {
					Player target = Bukkit.getPlayer(args[1]);
					if(target == null) {
						p.sendMessage(messages.get("player-not-online").replace("%player%", args[2]));
						return false;
					}
					if(data.isInClan(target)) {
						if(!data.getClan(target).equalsIgnoreCase(data.getClan(p))) {
							p.sendMessage(messages.get("player-not-in-same-clan").replace("%player%", target.getName()));
							return false;
						}
					}else {
						p.sendMessage(messages.get("player-not-in-clan").replace("%player%", target.getName()));
						return false;
					}
					if(data.isCoLeader(p)) {
						p.sendMessage(messages.get("you-cannot-kick-player").replace("%player%", target.getName()));
						return false;
					}
					data.removeMember(data.getClan(p), target);
					data.setPlayerData(p, "clan", null);
					data.setPlayerData(p, "position", null);
					p.sendMessage(messages.get("player-kicked").replace("%player%", target.getName()));
					return false;
				}
			}
			
		for(String msg : getHelp(s)) {
			p.sendMessage(msg);
		}
		return false;
	}
	
	public List<String> getClanInfo(String clan) {
		List<String> info = new ArrayList<String>();
		List<String> membernames = new ArrayList<String>();
		List<String> coleadernames = new ArrayList<String>();
		String friendlyfire;
		
		for(String uuid : data.getCoLeaders(clan)) {
			coleadernames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
		}
		
		for(String uuid : data.getMembers(clan)) {
			membernames.add(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName());
		}
		
		if(Boolean.valueOf(data.getClanData(clan, "friendlyfire").toString()) == true) {
			friendlyfire = "§cenable";
		}else {
			friendlyfire = "§adisable";
		}
		
		
		
		info.add(messages.getPrefix() + "§6----------------ClansInfo---------------");
		info.add(messages.getPrefix() + "§8name: §7" + clan);
		info.add(messages.getPrefix() + "§8description: §7" + data.getClanData(clan, "description"));
		info.add(messages.getPrefix() + "§8leader: §7" + Bukkit.getOfflinePlayer(UUID.fromString(data.getClanData(clan, "leader").toString())).getName());
		info.add(messages.getPrefix() + "§8friendlyfire: " + friendlyfire);
		info.add(messages.getPrefix() + "§8coleader: §7" + coleadernames);
		info.add(messages.getPrefix() + "§8members: §7" + membernames);
		return info;
	}
	
	public List<String> getHelp(CommandSender s) {
		List<String> help = new ArrayList<String>();
		help.add(messages.getPrefix() + "§6----------------ClansHelp---------------");
		
		/*console + player*/
		help.add(messages.getPrefix() + "§8/clans help");
		if(s.hasPermission("smartclans.reload")) help.add(messages.getPrefix() + "§8/clans reload");
		//console
		if(!(s instanceof Player)) {
			if(s.hasPermission("smartclans.info.others")) help.add(messages.getPrefix() + "§8/clans info <clanname>");
		}
		
		/*player*/
		if(!(s instanceof Player)) {
			help.add(messages.getPrefix() + "§6----------------ClansHelp---------------");
			return help;
		}
		Player p = (Player)s;
		
		if(s.hasPermission("smartclans.info.others")) {
			help.add(messages.getPrefix() + "§8/clans info (clananname)");
		}else if(s.hasPermission("smartclans.info") && data.isInClan(p)) {
			help.add(messages.getPrefix() + "§8/clans info");
		}
		
		/*not in clan*/
		if(!data.isInClan(p)) {
			if(s.hasPermission("smartclans.create")) help.add(messages.getPrefix() + "§8/clans create <clanname>");
			help.add(messages.getPrefix() + "§8/clans decline");
			help.add(messages.getPrefix() + "§8/clans accept");
			help.add(messages.getPrefix() + "§6----------------ClansHelp---------------");
			return help;
		}
		
		/*in clan*/
		
			/*clanleader*/
			if(data.isLeader(p)) {
				if(s.hasPermission("smartclans.delete")) help.add(messages.getPrefix() + "§8/clans delete <clanname>");
				help.add(messages.getPrefix() + "§8/clans add coleader <playername>");
				help.add(messages.getPrefix() + "§8/clans set description <description>");
				help.add(messages.getPrefix() + "§8/clans remove coleader <playername>");
			}
			/*clancoleader*/
			if(data.isCoLeader(p)) {
				help.add(messages.getPrefix() + "§8/clans invite <playername>");
				help.add(messages.getPrefix() + "§8/clans toggle friendlyfire");
			}
			help.add(messages.getPrefix() + "§6----------------ClansHelp---------------");
			return help;
	}

	/*tabcomplete*/
	@Override
	public List<String> onTabComplete(CommandSender s, Command cmd, String label, String[] args) {
		List<String> completions = new ArrayList<String>();
		/*console + player*/
		switch (args.length) {
		case 1:
			if("help".startsWith(args[0])) completions.add("help");
			if(s.hasPermission("smartclans.reload") && "relaod".startsWith(args[0])) completions.add("reload");
			if(s.hasPermission("smartclans.info") && "info".startsWith(args[0])) completions.add("info");
			break;

		default:
			break;
		}
		
		/*player*/
		if(!(s instanceof Player)) {
			switch (args.length) {
			default:
				break;
			}
			return completions;
		}
		Player p = (Player)s;
		
		/*in Clan*/
		if(data.isInClan(p)) {
			switch (args.length) {
			case 1:
				if(data.isLeader(p) && "set".startsWith(args[0])) completions.add("set");
				if(data.isLeader(p) && "add".startsWith(args[0])) completions.add("add");
				if(data.isLeader(p) && "delete".startsWith(args[0])) completions.add("delete");
				if(data.isCoLeader(p) && "invite".startsWith(args[0])) completions.add("invite");
				if("leave".startsWith(args[0]) && p.hasPermission("smartclans.leave")) completions.add("leave");
				if(data.isLeader(p) && "remove".startsWith(args[0])) completions.add("remove");
				if(data.isCoLeader(p) && "toogle".startsWith(args[0])) completions.add("toggle");
				break;
			case 2: 
				if(data.isLeader(p) && args[0].equalsIgnoreCase("set") && "description".startsWith(args[1])) completions.add("description");
				if(data.isLeader(p) && args[0].equalsIgnoreCase("add") && "coleader".startsWith(args[1])) completions.add("coleader");
				if(data.isCoLeader(p) && args[0].equalsIgnoreCase("invite")) {
					for(Player target : Bukkit.getOnlinePlayers()) {
						if(data.isInClan(target)  && target.getName().startsWith(args[1])) continue;
						completions.add(target.getName());
					}
				}
				if(data.isLeader(p) && args[0].equalsIgnoreCase("remove") && "coleader".startsWith(args[1])) completions.add("coleader");
				if(data.isCoLeader(p) && args[0].equalsIgnoreCase("toggle") && "friendlyfire".startsWith(args[1])) completions.add("friendlyfire");
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
		
		/*not in Clan*/
		switch (args.length) {
		case 1:
			if(s.hasPermission("smartclans.create") && "create".startsWith(args[0])) completions.add("create");
			if("decline".startsWith(args[0])) completions.add("decline");
			if("accept".startsWith(args[0])) completions.add("accept");
			break;
		default:
			break;
		}
		return completions;
	}
	
	
	
}
