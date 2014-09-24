package net.mittnett.edvin.area.PolarPartyArea.handlers;


import java.util.HashMap;
import java.util.UUID;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

/**
 * Support multiple bans for crea server, arena server and global ban.
 * @author edvin
 *
 */
public class BanDataCollection {

	protected HashMap<Integer, BanData> bandata;
	protected UUID userID;
	
	private UserHandler userHandler;
	
	protected int GLOBAL_BAN_ID = 0;
	protected int CREA_BAN_ID = 1;
	protected int ARENA_BAN_ID = 2;
	
	public BanDataCollection(PolarPartyArea plugin) {
		 bandata = new HashMap<Integer, BanData>();
		 this.userHandler = plugin.getUserHandler();
	}
	
	/**
	 * Set UUID for who this collection belongs to.
	 * @param uuid
	 */
	public void setUserUUID(UUID uuid) {
		this.userID = uuid;
	}
	
	/**
	 * Provides UUID for who this collection belongs to.
	 * @return
	 */
	public UUID getUserUUID() {
		return userID;
	}
	
	/**
	 * Add new BanData to this collection.
	 * @param bd
	 */
	public void addBanData(BanData bd) {
		this.bandata.put(bd.getFromServer(), bd);
	}
	
	/**
	 * Indicates if this user has a global ban.
	 * @return
	 */
	public boolean hasGlobalBan() {
		BanData bd = this.getSpecificBanData(GLOBAL_BAN_ID);
		return (bd != null);
	}
	
	/**
	 * Indicates if the user has an ban on serverID.
	 * @param serverID
	 * @return boolean
	 */
	public boolean hasServerBan(int serverID) {
		BanData bd = this.getSpecificBanData(serverID);
		return (bd != null);
	}
	
	/**
	 * Short-hand function to indicate if user is banned on creative server.
	 * @return
	 */
	public boolean hasCreativeServerBan() {
		return this.hasServerBan(CREA_BAN_ID);
	}
	
	/**
	 * Short-hand function to indicate if user is banned on arena server.
	 * @return
	 */
	public boolean hasArenaServerBan() {
		return this.hasServerBan(ARENA_BAN_ID);
	}
	
	/**
	 * Indicates if user has an ban.
	 * @return
	 */
	public boolean hasBan() {
		return (this.bandata.size() > 0);
	}
	
	/**
	 * Alias for hasBan()
	 * 
	 * @see hasBan()
	 * @return
	 */
	public boolean hasBans() {
		return hasBan();
	}
	
	/**
	 * Provides ban reason if any.
	 * @param serverID
	 * @return null is returned if no reason exists.
	 */
	public String getReason(int serverID) {
		BanData bd = this.getSpecificBanData(serverID);
		if (bd != null) {
			return bd.getReason();
		}
		
		return null;
	}
	
	/**
	 * Provides global ban reason if any.
	 * @return null is returned if no reason exists.
	 */
	public String getGlobalBanReason() {
		return this.getReason(GLOBAL_BAN_ID);
	}
	
	/**
	 * Removes ban from specific server.
	 * @param serverID Server ID
	 * @return true on success, false on failure. Global bans is not allowed to be deleted here.
	 */
	public boolean removeBan(int serverID) {
		BanData bd = this.getSpecificBanData(serverID);
		if (bd != null) {
			this.userHandler.removeBan(bd.getBanned(), serverID);
			return true;	
		}
		
		return false;
	}
	
	/**
	 * Provides all bans in this collection.
	 * @return Raw HashMap
	 */
	public HashMap<Integer, BanData> getAllBans() {
		return this.bandata;
	}
	
	/**
	 * Used for this class.
	 * Provides an specific bandata for serverID.
	 * 
	 * @param serverID
	 * @return
	 */
	public BanData getSpecificBanData(int serverID) {
		for (Integer i : bandata.keySet()) {
			BanData bd = bandata.get(i);
			if (bd != null && bd.getFromServer() == serverID) {
				return bd;			
			}
		}
		
		return null;
	}
	
}