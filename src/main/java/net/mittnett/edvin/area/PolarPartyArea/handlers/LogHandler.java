package net.mittnett.edvin.area.PolarPartyArea.handlers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.logging.Level;

import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;
import net.mittnett.edvin.area.PolarPartyArea.sql.MySQL;

public class LogHandler extends BaseHandler {

	private MySQL mysql;
	private Connection conn;
	
	private PreparedStatement insertLog;
	private PreparedStatement insertPm;
	private PreparedStatement insertGroup;

	public LogHandler(PolarPartyArea plugin) {
		super(plugin);
		
		this.mysql = plugin.getMysqlconnection();
	}

	@Override
	public void prepare() {
		conn = mysql.connect();
		try {
			this.insertLog = conn.prepareStatement("INSERT INTO `log`(`player`,`victim`,`amount`,`itemId`,`data`,`type`,`timestamp`)VALUES(?,?,?,?,?,?,UNIX_TIMESTAMP())");
			this.insertPm = conn.prepareStatement("INSERT INTO `pmlog`(`player_id`,`player_name`,`victim_id`,`victim_name`,`text`,`timestamp`)VALUES(?,?,?,?,?,UNIX_TIMESTAMP())");
			this.insertGroup = conn.prepareStatement("INSERT INTO `grouplog`(`player_id`,`player_name`,`group_id`,`text`,`timestamp`)VALUES(?,?,?,?,UNIX_TIMESTAMP())");
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		}
	}

	@Override
	public void cleanup() {
		try {
			this.insertLog.close();
			this.insertPm.close();
			this.insertGroup.close();
			
			conn.close();
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		}
	}
	
	public void log(int player, String playerN, int victim, String victimN, int amount, int itemId, String data, LogType type) {
		try {
			switch (type) {
			case PM:
				this.insertPm.setInt(1, player);
				this.insertPm.setString(2, playerN);
				this.insertPm.setInt(3, victim);
				this.insertPm.setString(4, victimN);
				this.insertPm.setString(5, data);
				this.insertPm.executeUpdate();
				break;
			case GROUPCHAT:
				this.insertGroup.setInt(1, player);
				this.insertGroup.setString(2, playerN);
				this.insertGroup.setInt(3, victim);
				this.insertGroup.setString(5, data);
				this.insertGroup.executeUpdate();
				break;
			case MOD:
				this.insertLog.setInt(1, player);
				this.insertLog.setInt(2, victim);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "MOD");
				this.insertLog.executeUpdate();
				break;
			case JOIN:
				this.insertLog.setInt(1, player);
				this.insertLog.setNull(2, Types.INTEGER);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "JOIN");
				this.insertLog.executeUpdate();
				break;
			case QUIT:
				this.insertLog.setInt(1, player);
				this.insertLog.setNull(2, Types.INTEGER);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "QUIT");
				this.insertLog.executeUpdate();
				break;
			case KILL:
				this.insertLog.setInt(1, player);
				this.insertLog.setInt(2, victim);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "KILL");
				this.insertLog.executeUpdate();
				break;
			case KILLNATURAL:
				this.insertLog.setInt(1, player);
				this.insertLog.setNull(2, Types.INTEGER);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "KILLNATURAL");
				this.insertLog.executeUpdate();
				break;
			case WIN:
				this.insertLog.setInt(1, player);
				this.insertLog.setNull(2, Types.INTEGER);
				this.insertLog.setNull(3, Types.INTEGER);
				this.insertLog.setNull(4, Types.SMALLINT);
				this.insertLog.setString(5, data);
				this.insertLog.setString(6, "WIN");
				this.insertLog.executeUpdate();
				break;
			default:
				break;
			}
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "SQL Exception", ex);
		}
	}

}
