// MySQL Snippet
// Author: cblacks26
// Date: Jun 5, 2015
// Email: wcblackstock@gmail.com

package io.github.cblacks26.mcclass.database;

import io.github.cblacks26.mcclass.Main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQL extends Database{

	private String host;
	private String port;
	private String dbname;
	private String username;
	private String password;
	private Connection c;
	private Statement s;

	public MySQL(String host, String port, String dbname, String username, String password){
		super();
		this.host = host;
		this.port = port;
		this.dbname = dbname;
		this.username = username;
		this.password = password;
	}

	public boolean initialize(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			return true;
		} catch (ClassNotFoundException e) {
			Main.writeError("Error initializing MySQL: " + e.getMessage());
			return false;
		}
	}

	public boolean open(){
		if (initialize()) {
			String url = "jdbc:mysql://" + host + ":" + port + "/" + dbname;
			try {
				c = DriverManager.getConnection(url, username, password);
				return true;
			} catch (SQLException e) {
				Main.writeError("Could not establish a MySQL connection: " + e.getMessage());
				return false;
			}
		}
		return false;
	}

	public Connection getConnection(){
		if(open()){
			return c;
		}else{
			Main.writeError("Connection is null");
			return null;
		}
	}
	
	@Override
	public void executeStatements(String[] statements){
		try{
			open();
			c = getConnection();
			c.setAutoCommit(false);
			s = c.createStatement();
			for(String table:statements){
				s.addBatch(table);
			}
			s.executeBatch();
			c.commit();
			s.close();
			c.close();
		}catch(SQLException e){
			Main.writeError("Problem Creating Tables: "+ e.getMessage());
		}
	}
}
