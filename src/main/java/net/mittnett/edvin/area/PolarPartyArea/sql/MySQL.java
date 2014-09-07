package net.mittnett.edvin.area.PolarPartyArea.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import net.mittnett.edvin.area.PolarPartyArea.ConfigurationHandler;
import net.mittnett.edvin.area.PolarPartyArea.PolarPartyArea;

public class MySQL {
	
	public Connection mysqlConn;
	private ConfigurationHandler config;
	
	public MySQL(PolarPartyArea plugin)
	{
		this.config = plugin.getConfigHandler();
	}

	public ResultSet select(String query) {
		try {
			PreparedStatement pre = mysqlConn.prepareStatement(query);
			ResultSet rs = pre.executeQuery();
			pre.close();
			rs.close();
			
			return rs;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public boolean update(String query) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			conn = this.connect();
			ps = conn.prepareStatement(query);
			ps.setEscapeProcessing(false);
			ps.executeUpdate();
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception catched while inserting to database.", ex);
			return false;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}				
				if (rs != null) {
					rs.close();
				}				
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				PolarPartyArea.log.log(Level.SEVERE, "Exception catched while closing connections.", ex);
			}
		}
		
		return true;
	}

	public boolean delete(String query) {
		return this.runUpdateQuery(query);		
	}
	
	public boolean runUpdateQuery(String query) {
		try {
			Statement stmt = mysqlConn.createStatement();
			stmt.executeUpdate(query);
			stmt.close();
			
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int insert(String query) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int id = 0;
		try {
			conn = this.connect();
			ps = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			ps.setEscapeProcessing(false);
			ps.executeUpdate();
			rs = ps.getGeneratedKeys();
			while (rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		} catch (SQLException ex) {
			PolarPartyArea.log.log(Level.SEVERE, "Exception catched while inserting to database.", ex);
			return 0;
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}				
				if (rs != null) {
					rs.close();
				}				
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				PolarPartyArea.log.log(Level.SEVERE, "Exception catched while closing connections.", ex);
			}
		}
	}
	
	public Connection connect() {
		return connect(config.getHost(), config.getUsername(), config.getPassword(), config.getPluginDatabase());
	}
	
	public Connection connect(String host, String username, String password,
			String database) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			return DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + database, username, password);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}	
	}
	
	public boolean connectFriendly() {
		this.mysqlConn = this.connect();
		return this.mysqlConn != null;
	}
		
}
