// SQLite Snippet
// Author: cblacks26
// Date: Jun 5, 2015
// Email: wcblackstock@gmail.com

package io.github.cblacks26.mcclass.database;

import io.github.cblacks26.mcclass.Main;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SQLite extends Database{

	private Connection c;
	private Statement s;
	private File f;
	
	public SQLite(File f) {
		super();
		this.f = f;
	}

	public boolean initialize(){
		try{
			Class.forName("org.sqlite.JDBC");
			return true;
		} catch (ClassNotFoundException e) {
			Main.writeError("Error initializing SQLite: " + e.getMessage());
			return false;
		}
	}
	
	public boolean open(){
		if (initialize()) {
			try {
				c = DriverManager.getConnection("jdbc:sqlite:" + f.getPath());
				return true;
			} catch (SQLException e) {
				Main.writeError("Error connecting to SQLite: " + e.getMessage());
				return false;
			}
		} else {
			return false;
		}
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
	public void executeStatements(String[] statements) {
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
			Main.writeError("Executing Statments: "+ e.getMessage());
		}
	}
	
}
