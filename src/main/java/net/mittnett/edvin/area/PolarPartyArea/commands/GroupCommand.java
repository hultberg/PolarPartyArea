package net.mittnett.edvin.area.PolarPartyArea.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.mittnett.edvin.area.PolarPartyArea.BaseCommand;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.handlers.Broadcaster;
import net.mittnett.edvin.area.PolarPartyArea.handlers.GroupHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GroupCommand extends BaseCommand {
	
	private GroupHandler grHandler;
	private Broadcaster broadcaster;

	public GroupCommand(PolarPartyArea plugin) {
		super(plugin);
		this.setAccessLevel(1);
		this.setPlayerCommand(true);
		
		this.grHandler = plugin.getGroupHandler();
		this.broadcaster = plugin.getBroadcaster();
	}

	@Override
	public boolean onPlayerCommand(Player player, CommandSender sender,
			Command command, String label, String[] args) {
		List<String> everyoneCommands = new ArrayList<String>(Arrays.asList("hjelp", "ny", "invs", "godta", "avslå"));		
		int uid = this.userHandler.getUserId(player.getName());
		int grId = this.userHandler.getGroupId(uid);
		
		if (args.length == 0 || args[0].equalsIgnoreCase("hjelp")) {
			List<String> cmds = this.grHandler.getHelpCommands(uid);
			player.sendMessage(ChatColor.GOLD + "----------- Gruppesystem -----------");
			for (String line : cmds) {
				player.sendMessage(line);
			}
		} else {
			if (grId == -1 && !everyoneCommands.contains(args[0])) {
				player.sendMessage(ChatColor.RED + "Du må være i en gruppe for å bruke denne kommandoen.");
				return true;
			}
			
			if (args.length == 2 && args[0].equalsIgnoreCase("ny")) {
				if (args[1].length() > 20) { 
					player.sendMessage(ChatColor.RED + "Navnet på gruppen er for langt, maks 20 bokstaver.");
					return true;
				}				
				if (grId != -1) {
					player.sendMessage(ChatColor.RED + "Du er allerede i en gruppe.");
					return true;
				}
				
				int newId = this.grHandler.createGroup(args[1], uid);
				if (newId != 0) {
					player.sendMessage(ChatColor.GREEN + "Gruppen ble opprettet som " + ChatColor.WHITE + args[1] + ChatColor.GREEN + ".");
					this.userHandler.setNewGroup(uid, newId);
					this.userHandler.users.get(player.getName()).setGroupId(newId);
				} else {
					player.sendMessage(ChatColor.RED + "Noe gikk galt under opprettingen.");
				}
			} else if (args[0].equalsIgnoreCase("invs")) {
				ArrayList<ArrayList<String>> invs = this.grHandler.getInvites(uid);
				if (invs.size() == 0) {
					player.sendMessage(ChatColor.RED + "Inven invitasjoner ble funnet.");
					return true;
				}
				
				player.sendMessage(ChatColor.BLUE + "Invitasjoner: ");
				for (int i = 0; i <= invs.size(); i++) {
					int inviteId = Integer.parseInt(invs.get(i).get(0));
					int fromUid = Integer.parseInt(invs.get(i).get(1));
					String grName = invs.get(i).get(2);
					
					player.sendMessage(ChatColor.GRAY + "Fra: " + ChatColor.WHITE
							+ this.userHandler.getUsername(fromUid) + ChatColor.GRAY + ", gruppe: " + ChatColor.WHITE
							+ grName + ChatColor.GRAY + ".");
					player.sendMessage(ChatColor.GREEN + "Godta: /gr godta " + inviteId
							+ ChatColor.GRAY + " | "
							+ ChatColor.RED + "Avslå: /gr avslå " + inviteId);
					player.sendMessage("");
				}
				
				player.sendMessage(ChatColor.BLUE + "--------------------");
			} else if (args.length == 2 && (args[0].equalsIgnoreCase("avslå") || args[0].equalsIgnoreCase("godta"))) {
				int inviteId = 0;
				try {
					inviteId = Integer.parseInt(args[1]);
				} catch (NumberFormatException ex) {
					if (args[1].equalsIgnoreCase("alle")) {
						this.grHandler.deleteAllInvites(uid);
						player.sendMessage(ChatColor.GREEN + "Alle invitasjoner ble slettet.");
						return true;
					}
					
					player.sendMessage(ChatColor.RED + "Invitasjons ID må være tall.");
					return true;
				}
				
				String endMsg = "";
				if (args[0].equalsIgnoreCase("godta")) {
					if (grId != -1) {
						player.sendMessage(ChatColor.RED + "Du er allerede i en gruppe, forlat den før du godtar invitasjonen.");
						return true;
					}
					
					int invGrId = this.grHandler.inviteValid(inviteId, uid);
					if (invGrId != -1) {
						this.userHandler.setNewGroup(uid, invGrId);
						this.userHandler.users.get(player.getName()).setGroupId(invGrId);
						endMsg = ChatColor.GREEN + "Invitasjonen ble akseptert.";
					}					
				} else {
					endMsg = ChatColor.GREEN + "Invitasjonen ble slettet.";					
				}
				
				if (!this.grHandler.deleteInvite(inviteId)) {
					endMsg = ChatColor.RED + "En feil skjedde under sletting av invitasjonen, kontakt game-crew.";
				}
				
				player.sendMessage(endMsg);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("inv")) {
				int targetId = this.userHandler.getUserId(args[1]);
				if (targetId == -1) {
					player.sendMessage(ChatColor.RED + "Fant ikke brukeren " + ChatColor.WHITE + "'" + args[1] + "'");
					return true;
				}
				
				if (this.grHandler.inviteExists(targetId, grId)) {
					player.sendMessage(ChatColor.RED + "Brukeren har allerede blitt invitert til gruppen.");
					return true;
				}
				
				int inviteId = this.grHandler.sendInvite(uid, targetId, grId);
				if (inviteId == -1) {
					player.sendMessage(ChatColor.RED + "En feil skjedde, kontakt game-crew.");
					return true;
				}
				
				player.sendMessage(ChatColor.GREEN + "Invitasjonen ble sendt.");
				
				Player target = Bukkit.getPlayer(this.userHandler.getUserUUID(targetId));
				if (target != null) {
					target.sendMessage(ChatColor.DARK_GREEN + "Du er blitt invitert til gruppen " + ChatColor.WHITE
							+ this.grHandler.getGroupName(grId) + ChatColor.DARK_GREEN + " av " + ChatColor.WHITE + player.getName());
					target.sendMessage(ChatColor.GREEN + "For å godta skriv: " + ChatColor.WHITE
							+ "/gr godta " + inviteId);
					player.sendMessage(ChatColor.GREEN + "Brukeren er online og er blitt varslet.");
				} else {
					player.sendMessage(ChatColor.GREEN + "Brukeren er offline og blir varslet når han/hun logger på.");
				}
			} else if (args[0].equalsIgnoreCase("forlat")) {				
				if (this.grHandler.getGroupOwner(grId) == uid) {
					int members = this.grHandler.getMembers(grId).size();
					if (members > 1) {
						player.sendMessage(ChatColor.RED + "Du er eier og kan ikke forlate gruppa når det er medlemmer i den.");
						player.sendMessage(ChatColor.RED + "Endre eier eller kick alle medlemmer før du forlater den.");
						return true;
					}
					
					this.userHandler.users.get(player.getName()).setGroupId(-1);
					player.sendMessage(ChatColor.GREEN + "Du forlot gruppa og siden du var siste medlem ble gruppa slettet.");
				} else {
					player.sendMessage(ChatColor.GREEN + "Du forlot gruppa.");
					broadcaster.broadcastGroup(grId, player.getName(), "Forlot gruppa.");
				}
				
				this.grHandler.deleteGroup(grId);
				this.userHandler.setNewGroup(uid, -1);
			} else if (args.length == 2 && args[0].equalsIgnoreCase("kick")) {
				if (this.grHandler.getGroupOwner(grId) != uid) {
					player.sendMessage(ChatColor.RED + "Du er ikke eieren av gruppa.");
					return true;
				}
				
				int targetId = this.userHandler.getUserId(args[1]);
				if (targetId == -1) {
					player.sendMessage(ChatColor.RED + "Fant ikke brukeren " + ChatColor.WHITE + "'" + args[1] + "'");
					return true;
				}
				
				if (this.userHandler.getGroupId(targetId) != grId) {
					player.sendMessage(ChatColor.RED + "Brukeren er ikke medlem av gruppa.");
					return true;
				}
				
				this.userHandler.setNewGroup(targetId, -1);
				
				Player target = Bukkit.getPlayer(this.userHandler.getUserUUID(targetId));
				if (target != null) {
					this.userHandler.users.get(target.getName()).setGroupId(-1);
					target.sendMessage(ChatColor.RED + "Du er blitt kicket fra gruppa av " + player.getName());
				}
				
				broadcaster.broadcastGroup(grId, player.getName(), "Kicket " + this.userHandler.getUsername(targetId) + " fra gruppa.");
			} else if (args[0].equalsIgnoreCase("who")) {
				player.sendMessage(ChatColor.BLUE + "Medlemmer av gruppa: ");
				List<String> members = this.grHandler.getMembers(grId);
				String membersS = "";
				for (String mem : members) {
					Player tmp = Bukkit.getPlayer(this.userHandler.getUserUUID(mem));
					if (tmp != null) {
						membersS += ChatColor.GREEN + mem + ChatColor.WHITE + ", ";						
					} else {
						membersS += ChatColor.GRAY + mem + ChatColor.WHITE + ", ";
					}
				}
				membersS = membersS.substring(0, (membersS.length() - 2)) + ".";
				player.sendMessage("  " + membersS);
				player.sendMessage("  Nicks i grønt er pålogget, de i grått er avlogget.");
			} else if (args[0].equalsIgnoreCase("info")) {
				String owner = "deg";
				int ownerId = this.grHandler.getGroupOwner(grId);
				if (uid != ownerId) {
					owner = this.userHandler.getUsername(ownerId);
				}
				
				player.sendMessage(ChatColor.BLUE + "Informasjon om gruppa: ");
				player.sendMessage("  " + ChatColor.DARK_GREEN + "Navn: " + ChatColor.WHITE + this.grHandler.getGroupName(grId));
				player.sendMessage("  " + ChatColor.DARK_GREEN + "Eier: " + ChatColor.WHITE + owner);
				player.sendMessage("  " + ChatColor.DARK_GREEN + "Medlemmer: " + ChatColor.WHITE + this.grHandler.getMembers(grId).size());
				player.sendMessage("  For en liste over medlemmer, bruk /gr who");
			} else if (args.length == 2 && args[0].equalsIgnoreCase("loc")) {
				int targetId = this.userHandler.getUserId(args[1]);
				if (targetId == -1) {
					player.sendMessage(ChatColor.RED + "Fant ikke brukeren " + ChatColor.WHITE + "'" + args[1] + "'");
					return true;
				}

				Player target = Bukkit.getPlayer(this.userHandler.getUserUUID(targetId));
				if (target == null) {
					player.sendMessage(ChatColor.RED + "Brukeren " + ChatColor.WHITE + "'" + args[1] + "'" + ChatColor.RED + " er avlogget.");
					return true;
				}
				
				Location loc = target.getLocation();
				player.sendMessage(ChatColor.DARK_GREEN + "Brukeren " + ChatColor.WHITE + target.getName() + ChatColor.DARK_GREEN
						+ " befinner seg på " + ChatColor.WHITE.toString() + loc.getBlockX() + " " + loc.getBlockY() + " "
						+ loc.getBlockZ() + ChatColor.DARK_GREEN + " i " + ChatColor.WHITE + loc.getWorld().getName());
			} else if (args.length == 2 && args[0].equalsIgnoreCase("eier")) {
				int targetId = this.userHandler.getUserId(args[1]);
				if (targetId == -1) {
					player.sendMessage(ChatColor.RED + "Fant ikke brukeren " + ChatColor.WHITE + "'" + args[1] + "'");
					return true;
				}

				this.grHandler.setNewGroupOwner(grId, targetId);
				broadcaster.broadcastGroup(grId, player.getName(), "Satt " + this.userHandler.getUsername(targetId) + " som ny eier av gruppa.");
				
				Player target = Bukkit.getPlayer(this.userHandler.getUserUUID(targetId));
				if (target != null) {
					target.sendMessage(ChatColor.GREEN + "Du er satt som ny eier av gruppa.");
					return true;
				}
			}
		}
		
		return true;
	}

}
