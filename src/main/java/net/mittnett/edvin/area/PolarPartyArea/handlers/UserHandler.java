package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

public class UserHandler extends BaseHandler {

	public HashMap<String, PlayerData> users;
	private Connection mysql;
	private MySQL mysqlc;
	private ConfigurationHandler config;
	
	public PreparedStatement insert;
	public PreparedStatement getBySUID;
	public PreparedStatement getByUUID;
	public PreparedStatement getByName;
	
	public String userFilesPath;
	
	private String commonDb;
	
	public UserHandler(PolarPartyArea plugin) {
		super(plugin);
		this.users = new HashMap<String, PlayerData>();
		
		this.mysqlc = plugin.getMysqlconnection();
		this.mysql = mysqlc.mysqlConn;
		this.config = plugin.getConfigHandler();
		
		this.userFilesPath = plugin.getDataFolder().getAbsolutePath() + "/playerdata/";
		File tmp = new File(this.userFilesPath);
		if (!tmp.exists())
			tmp.mkdirs();	
		
		this.commonDb = "`" + this.config.getCommonDatabase() + "`.";
	}
	
	public void prepare() {
		try {
			this.getBySUID = this.mysql.prepareStatement("SELECT `mc_uid`,`nick`,`access`,`banned`,`group` FROM " + this.commonDb + "`users` WHERE `server_uid` = ?");
			this.getByUUID = this.mysql.prepareStatement("SELECT `server_uid`,`nick`,`access`,`banned`,`group` FROM " + this.commonDb + "`users` WHERE `mc_uid` = ?");
			this.getByName = this.mysql.prepareStatement("SELECT `server_uid`,`mc_uid`,`access`,`banned`,`group` FROM " + this.commonDb + "`users` WHERE `nick` = ?");
			this.insert = this.mysql.prepareStatement("INSERT INTO " + this.commonDb + "`users`(`mc_uid`,`nick`)VALUES(?,?)");
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}

	public void cleanup() {
		
		for (Player p : Bukkit.getOnlinePlayers()) {
			this.logout(p);
		}
		
		try {
			this.getBySUID.close();
			this.getByUUID.close();
			this.getByName.close();
			this.insert.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
	}
	
	public void loginUser(Player p) {
		if (!userExists(p.getName())) {
			registerUser(p.getName(), p.getUniqueId());
		}
		
		this.users.put(p.getName(), this.getPlayerData(p.getUniqueId().toString()));
		
		PlayerData pd = this.users.get(p.getName());
		if (pd != null) {
			this.validatePlayerData(pd, p);			
		} else {
			// NO!
			PolarPartyArea.log.severe("Playerfile for " + p.getName() + " was null when trying to validate...");
			
		}
	}
	
	public void registerUser(String username, UUID uuid) {
		try {
			this.insert.setString(1, uuid.toString());
			this.insert.setString(2, username);
			this.insert.executeUpdate();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	public void logout(Player p) {
		logout(p.getName(), p.getUniqueId().toString());
	}
	
	public void logout(String username, String mcuid) {
		try {
			// Save player file.
			PolarPartyArea.log.info("Saving " + username + " (UUID: " + mcuid + ") player file.");
			PolarPartyArea.save(new File(userFilesPath, mcuid + ".bin"), this.users.get(username));
			
			this.users.remove(username);
		} catch (Exception ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Can't save user file.", ex);
		}
	}
	
	/**
	 * Checks if username exists in database.
	 * @param username
	 * @return boolean
	 */
	public boolean userExists(String username) {
		boolean exists = false;
		try {
			this.getByName.setString(1, username);
			ResultSet rs = this.getByName.executeQuery();
			while (rs.next()) {
				exists = true;
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return false;
		}
		
		return exists;
	}
	
	/**
	 * Validates PlayerData concerning nick, access and group.
	 * @param pd
	 */
	public void validatePlayerData(PlayerData pd, Player p) {
		try {
			this.getByUUID.setString(1, p.getUniqueId().toString());
			ResultSet rs = this.getByUUID.executeQuery();
			while (rs.next()) {
				if (rs.getInt(3) != pd.getAccess()) {
					pd.setAccess(rs.getInt(3));
				}
				
				if (!rs.getString(2).equalsIgnoreCase(p.getName())) {
					pd.setUsername(p.getName());
					// Here we must update database too...
					this.updateUserDb(rs.getInt(1), "nick", p.getName());
				}
				
				if (rs.getInt(5) != pd.getGroupId()) {
					pd.setGroupId(rs.getInt(5));
				}
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public PlayerData getPlayerData(String mcuid) {
		PlayerData pd = new PlayerData();
		
		try {
			// Load player file.
			pd = (PlayerData) PolarPartyArea.load(new File(userFilesPath, mcuid + ".bin"));			
		} catch (Exception ex) {
			// new user or file dosen't exists.
			pd = this.getPlayerDataDb(mcuid);
		}
		
		if (pd == null) {
			PolarPartyArea.log.warning("Playerfile for " + mcuid + " was null, deleting local file and getting from DB.");
			new File(userFilesPath, mcuid + ".bin").renameTo(new File(userFilesPath, mcuid + "_" + (int) (System.currentTimeMillis() / 1000) + "_nulled.bin"));
			pd = this.getPlayerDataDb(mcuid);
		}
		
		return pd;
	}
	
	public void updateAccess(int newAccess, int uid) {
		this.updateUserDb(uid, "access", newAccess);
	}
	
	public void updateUserDb(int userId, String column, int value) {
		this.mysqlc.update("UPDATE " + this.commonDb + "`users` SET `" + column +"` = " + value + " WHERE `server_uid` = " + userId);
	}
	
	public void updateUserDb(int userId, String column, String value) {
		this.mysqlc.update("UPDATE " + this.commonDb + "`users` SET `" + column +"` = " + value + " WHERE `server_uid` = " + userId);		
	}
	
	public PlayerData getCacheUser(String username) {
		return this.users.get(username);
	}
	
	/**
	 * Get playerdata by mysql
	 * @param mcuid
	 * @return
	 */
	public PlayerData getPlayerDataDb(String mcuid) {
		PlayerData pd = new PlayerData();
		
		try {
			this.getByUUID.setString(1, mcuid);
			ResultSet rs = this.getByUUID.executeQuery();
			while (rs.next()) {
				pd.setMcUid(mcuid);
				pd.setServerId(rs.getInt(1));
				pd.setUsername(rs.getString(2));
				pd.setAccess(rs.getInt(3));
				pd.setBanned(rs.getInt(4) == 1);
				pd.setGroupId(rs.getInt(5));
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return pd;
		}
		
		return pd;
	}
	
	/**
	 * get username
	 * @param String username
	 * @return boolean
	 */
	public int getUserId(String username) {
		int uid = -1;
		
		/* For online */
		for (String uname : this.users.keySet()) {
			if (uname.equalsIgnoreCase(username)) {
				PlayerData pd = this.users.get(uname);
				return pd.getServerId();
			}
		}
		
		/* For offline */
		try {
			this.getByName.setString(1, username);
			ResultSet rs = this.getByName.executeQuery();
			while (rs.next()) {
				uid = rs.getInt(1);
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return -1;
		}
		
		return uid;
	}
	
	/**
	 * get uuid
	 * @param Integer uid
	 * @return boolean
	 */
	public String getUserStringUUID(int uid) {
		String uuid = null;
		
		/* For online */
		for (String uname : this.users.keySet()) {
			PlayerData pd = this.users.get(uname);
			if (pd.getServerId() == uid) {
				return pd.getMcUid();
			}
		}
		
		/* For offline */
		try {
			this.getBySUID.setInt(1, uid);
			ResultSet rs = this.getBySUID.executeQuery();
			while (rs.next()) {
				uuid = rs.getString(1);
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return null;
		}
		
		return uuid;
	}
	
	public String getUserStringUUID(String username) {
		return this.getUserStringUUID(this.getUserId(username));
	}
	
	public UUID getUserUUID(String username) {
		return UUID.fromString(this.getUserStringUUID(this.getUserId(username)));
	}
	
	public UUID getUserUUID(int uid) {
		return UUID.fromString(this.getUserStringUUID(uid));
	}
	
	/**
	 * Get userid
	 * @param int
	 * @return boolean
	 */
	public String getUsername(int uid) {
		String username = null;
		
		if (uid == -1) 
			return username;		
		
		/* For online */
		for (String uname : this.users.keySet()) {
			PlayerData pd = this.users.get(uname);
			if (pd.getServerId() == uid) {
				return pd.getUsername();
			}
		}
		
		/* For offline */
		try {
			this.getBySUID.setInt(1, uid);
			ResultSet rs = this.getBySUID.executeQuery();
			while (rs.next()) {
				username = rs.getString(2);
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return null;
		}
		
		return username;
	}
	
	/**
	 * Get access level
	 * @param int
	 * @return boolean
	 */
	public int getAccessLevel(int uid, boolean forceDb) {
		int level = 0;
		
		if (uid == -1) 
			return 0;		
		
		/* For online */
		if (!forceDb) {
			for (String uname : this.users.keySet()) {
				PlayerData pd = this.users.get(uname);
				if (pd.getServerId() == uid) {
					return pd.getAccess();
				}
			}
		}
		
		/* For offline */
		try {
			this.getBySUID.setInt(1, uid);
			ResultSet rs = this.getBySUID.executeQuery();
			while (rs.next()) {
				level = rs.getInt(3);
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return 0;
		}
		
		return level;
	}
	
	/**
	 * If you are coding for offline use please use the method with UID not username.
	 * @param username
	 * @param forceDb
	 * @return
	 */
	public int getAccessLevel(String username, boolean forceDb) {
		return this.getAccessLevel(this.getUserId(username), forceDb);
	}
	
	public int getAccessLevel(String username) {
		return this.getAccessLevel(username, false);
	}
	
	public int getGroupId(int userId) {
		int gid = -1;
		
		/* For online */
		for (String uname : this.users.keySet()) {
			PlayerData pd = this.users.get(uname);
			if (pd.getServerId() == userId) {
				return pd.getGroupId();
			}
		}
		
		/* For offline */
		try {
			this.getBySUID.setInt(1, userId);
			ResultSet rs = this.getBySUID.executeQuery();
			while (rs.next()) {
				gid = rs.getInt(5);
			}
			
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();			
			return -1;
		}
		
		return gid;
	}
	
	/**
	 * Alias for getUserId(int)
	 * This function will call getUserId(String) and if user is
	 * online this means userId will be retrieved from db.
	 * @param username
	 * @return
	 */
	public int getGroupId(String username) {
		return this.getGroupId(this.getUserId(username));
	}

	public boolean isInGroup(String username) {
		return (this.getGroupId(this.getUserId(username)) != -1);
	}

	public boolean isInGroup(int uid) {
		return (this.getGroupId(uid) != -1);
	}
	
	/**
	 * Will update group for user in db and PlayerData.
	 * @param usern
	 * @param toGroup
	 */
	public void setNewGroup(String usern, int toGroup) {
		int userId = this.getUserId(usern);
		this.users.get(usern).setGroupId(toGroup);
		this.updateUserDb(userId, "group", toGroup);
	}
	
	/**
	 * Will only update group in database.
	 * @param userId
	 * @param toGroup
	 */
	public void setNewGroup(int userId, int toGroup) {
		this.updateUserDb(userId, "group", toGroup);
	}
	
	public int getAccessLevel(Player p) {
		return getAccessLevel(p.getName());
	}

	public String getPrefix(String username) {
		int level = this.getAccessLevel(username);
		
		switch (level) {
		case 1: return ChatColor.WHITE + username;
		case 5: return ChatColor.BLUE + "[Mod] " + username;
		case 10: return ChatColor.GOLD + "[Admin] " + username;
		default: return ChatColor.GRAY + "[Gjest] " + username;
		}
	}
	
}
