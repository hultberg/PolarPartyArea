package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.io.Serializable;

public class PlayerData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7777450815963597354L;
	private int server_id = -1;
	private String mc_uid = "asd";
	private String username = "Ukjent";
	private int access = 0;
	private boolean banned = false;
	private int groupId = -1;
	
	public PlayerData() {
	}

	public int getServerId() {
		return server_id;
	}

	public void setServerId(int server_id) {
		this.server_id = server_id;
	}

	public String getMcUid() {
		return mc_uid;
	}

	public void setMcUid(String mc_uid) {
		this.mc_uid = mc_uid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getAccess() {
		return access;
	}

	public void setAccess(int access) {
		this.access = access;
	}

	public boolean isBanned() {
		return banned;
	}

	public void setBanned(boolean banned) {
		this.banned = banned;
	}

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}	
	
}
