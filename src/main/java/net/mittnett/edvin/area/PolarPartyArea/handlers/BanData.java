package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.util.UUID;

public class BanData {

	protected int banID;
	protected UUID banned;
	protected UUID banner;
	protected int timestamp;
	protected String reason;
	protected int fromServer;
	
	public BanData(int banID, UUID banned, UUID banner, int timestamp,
			String reason, int fromServer) {
		this.banID = banID;
		this.banned = banned;
		this.banner = banner;
		this.timestamp = timestamp;
		this.reason = reason;
		this.fromServer = fromServer;
	}

	public int getBanID() {
		return banID;
	}

	public void setBanID(int banID) {
		this.banID = banID;
	}

	public UUID getBanned() {
		return banned;
	}

	public void setBanned(UUID banned) {
		this.banned = banned;
	}

	public UUID getBanner() {
		return banner;
	}

	public void setBanner(UUID banner) {
		this.banner = banner;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	/**
	 * 0=All,1=Crea,2=Arena
	 * @return
	 */
	public int getFromServer() {
		return fromServer;
	}
	
	/**
	 * Provides friendly name for server.
	 * @return
	 */
	public String getFromServerFriendly() {
		String ret = "";
		switch (getFromServer()) {
		case 1: ret = "Creative"; break;
		case 2: ret = "Arena"; break;
		case 0: ret = "Global"; break;
		default: break;
		}
		
		return ret;
	}

	public void setFromServer(int fromServer) {
		this.fromServer = fromServer;
	}
	

}