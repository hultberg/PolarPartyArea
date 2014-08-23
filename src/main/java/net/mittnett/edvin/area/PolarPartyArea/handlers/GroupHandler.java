package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

public class GroupHandler extends BaseHandler {

	private MySQL mysql;
	
	private PreparedStatement deleteGroup;
	private PreparedStatement deleteInvite;
	private PreparedStatement deleteAllInvites;
	private PreparedStatement getInvite;
	private PreparedStatement getInvites;
	private PreparedStatement getGroupById;

	public GroupHandler(PolarPartyArea plugin) {	
		super(plugin);
		
		this.mysql = plugin.getMysqlconnection();
	}
	
	public void prepare() {
		Connection conn = this.mysql.mysqlConn;
		try {
			this.deleteGroup = conn.prepareStatement("DELETE FROM `groups` WHERE `group_id`=?");
			this.deleteInvite = conn.prepareStatement("DELETE FROM `group_invites` WHERE `invite_id`=?");
			this.deleteAllInvites = conn.prepareStatement("DELETE FROM `group_invites` WHERE `to_uid`=?");
			this.getInvite = conn.prepareStatement("SELECT `group_id` FROM `group_invites` WHERE `invite_id`=? AND `to_uid`=?");
			this.getInvites = conn.prepareStatement("SELECT group_invites.invite_id,group_invites.from_uid,groups.group_name FROM `group_invites`,`groups` WHERE `to_uid` = ? AND group_invites.group_id = groups.group_id");
			this.getGroupById = conn.prepareStatement("SELECT `group_name`,`group_owner` FROM `groups` WHERE `group_id`=?");
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while preparing grouphandler.", ex);
		}
	}

	public void cleanup() {
		try {
			this.deleteGroup.close();
			this.deleteInvite.close();
			this.deleteAllInvites.close();
			this.getInvite.close();
			this.getInvites.close();
			this.getGroupById.close();
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while cleaning grouphandler.", ex);
		}
	}
	
	/**
	 * Creates a new group, make sure that user is not a member of any other before creating one.
	 * @param name
	 * @param creator
	 * @return
	 */
	public int createGroup(String name, String creator) {
		return this.createGroup(name, this.userHandler.getUserId(creator));
	}
	
	/**
	 * Creates a new group, make sure that user is not a member of any other before creating one.
	 * @param name
	 * @param creator
	 * @return
	 */
	public int createGroup(String name, int creator) {
		return this.mysql.insert("INSERT INTO `groups`(group_name,group_owner)VALUES('" + name + "', " + creator + ")");
	}
	
	/**
	 * Deletes a group from database.
	 * @param groupId
	 * @return
	 */
	public boolean deleteGroup(int groupId) {
		try {
			this.deleteGroup.setInt(1, groupId);
			this.deleteGroup.executeUpdate();
			return true;
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while deleting a new group.", ex);
			return false;
		}
	}
	
	/**
	 * Inserts a new invite into database.
	 * @param fromUser
	 * @param toUser
	 * @param toGroup
	 * @return invite id
	 */
	public int sendInvite(int fromUser, int toUser, int toGroup) {
		return this.mysql.insert("INSERT INTO `group_invites`(from_uid,to_uid,group_id)VALUES(" + fromUser + ", " + toUser + ", " + toGroup + ")");
	}
	
	public ArrayList<ArrayList<String>>  getInvites(int uid) {
		ArrayList<ArrayList<String>> invites = new ArrayList<ArrayList<String>>();
		try {
			this.getInvites.setInt(1, uid);
			ResultSet rs = this.getInvites.executeQuery();
			while (rs.next()) {
				invites.add(new ArrayList<String>(Arrays.asList(rs.getString(1), rs.getString(2), rs.getString(4))));
			}
			rs.close();
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while getting invites.", ex);
		}
		
		return invites;
	}
	
	/**
	 * Delete an invite.
	 * @param inviteId
	 * @return
	 */
	public boolean deleteInvite(int inviteId) {
		try {
			this.deleteInvite.setInt(1, inviteId);
			this.deleteInvite.executeUpdate();
			return true;
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while deleting invite.", ex);
			return false;
		}
	}
	
	/**
	 * Delete an invite.
	 * @param userId
	 * @return
	 */
	public boolean deleteAllInvites(int userId) {
		try {
			this.deleteAllInvites.setInt(1, userId);
			this.deleteAllInvites.executeUpdate();
			return true;
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while deleting all invites.", ex);
			return false;
		}
	}
	
	/**
	 * Checks if an invite is valid.
	 * @param inviteId
	 * @param userId
	 * @return
	 */
	public int inviteValid(int inviteId, int userId) {
		int grId = -1;
		try {
			this.getInvite.setInt(1, inviteId);
			this.getInvite.setInt(2, userId);
			ResultSet rs = this.getInvite.executeQuery();
			while (rs.next()) {
				grId = rs.getInt(1);
			}
			
			return grId;
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception got while checking invite.", ex);
			return -1;
		}
	}
	
	/**
	 * Checks if user has been invited.
	 * @param userId
	 * @param grId
	 * @return
	 */
	public boolean inviteExists(int userId, int grId) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			ps = this.mysql.mysqlConn.prepareStatement("SELECT `invite_id` FROM `group_invites` WHERE `to_uid` = ? AND `group_id` = ?");
			ps.setInt(1, userId);
			ps.setInt(2, grId);
			rs = ps.executeQuery();
			while (rs.next()) {
				result = true;
			}
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
			}
		}
		
		return result;
	}
	
	/**
	 * Get owner of group, if group dosen't exists then -1 will be returned.
	 * @param groupId
	 * @return
	 */
	public int getGroupOwner(int groupId) {
		int owner = -1;
		try {
			this.getGroupById.setInt(1, groupId);
			ResultSet rs = this.getGroupById.executeQuery();
			while (rs.next()) {
				owner = rs.getInt(2);
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			return -1;
		}
		
		return owner;
	}
	
	public boolean setNewGroupOwner(int groupId, int newOwner) {
		return this.mysql.update("UPDATE `groups` SET `group_owner` = " + newOwner + " WHERE `group_id` = " + groupId);
	}
	
	public String getGroupName(int groupId) {
		String name = "";
		try {
			this.getGroupById.setInt(1, groupId);
			ResultSet rs = this.getGroupById.executeQuery();
			while (rs.next()) {
				name = rs.getString(1);
			}
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		}
		
		return name;
	}
	
	public List<String> getMembers(int gid) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		ArrayList<String> result = new ArrayList<String>();
		try {
			ps = this.mysql.mysqlConn.prepareStatement("SELECT `nick` FROM `users` WHERE `group` = ?");
			ps.setInt(1, gid);
			rs = ps.executeQuery();
			while (rs.next()) {
				result.add(rs.getString(1));
			}
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
			}
		}
		
		return result;
	}
	
	public ArrayList<String> getHelpCommands(int uid) {
		int grId = this.userHandler.getGroupId(uid);
		ArrayList<String> cmds = new ArrayList<String>();
		cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr hjelp" + ChatColor.WHITE + " - Viser alle kommandoer.");
		cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr ny " + ChatColor.GRAY + "[navn]" + ChatColor.WHITE + " - Lag en ny gruppe.");
		cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr invs " + ChatColor.GRAY + "(sidetall)" + ChatColor.WHITE + " - Se dine invitasjoner.");
		cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr godta " + ChatColor.GRAY + "[id]" + ChatColor.WHITE + " - Godta en invitasjon.");
		cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr avslå " + ChatColor.GRAY + "[id/alle]" + ChatColor.WHITE + " - Avslå en invitasjon, skriv alle om du vil avslå alle.");
		if (grId != -1) {
			cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr forlat " + ChatColor.WHITE + " - Forlat gruppen.");
			cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr inv " + ChatColor.GRAY + "[brukernavn]" + ChatColor.WHITE + " - Inviter en bruker.");
			cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr info " + ChatColor.WHITE + " - Viser info om gruppen.");
			cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr who " + ChatColor.WHITE + " - Viser medlemmer av gruppen.");
			cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr loc " + ChatColor.GRAY + "[brukernavn]" + ChatColor.WHITE + " - Viser hvor en spiller er.");
			if (this.getGroupOwner(grId) == uid) {
				cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr eier " + ChatColor.GRAY + "[brukernavn]" + ChatColor.WHITE + " - Skifter eier av gruppa.");	
				cmds.add(ChatColor.DARK_GREEN + "/" + ChatColor.GOLD + "gr kick " + ChatColor.GRAY + "[brukernavn]" + ChatColor.WHITE + " - Kicker en spiller ut av gruppa.");				
			}
		}
		
		return cmds;
	}

}
