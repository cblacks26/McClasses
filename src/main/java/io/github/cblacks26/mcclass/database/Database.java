// Database Snippet
// Author: cblacks26
// Date: Jun 5, 2015
// Email: wcblackstock@gmail.com

package io.github.cblacks26.mcclass.database;

import io.github.cblacks26.mcclass.Main;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {

	private Connection c;

	public abstract Connection getConnection();

	public void closeConnection(){
		try{
			if(c != null){
				c.close();
			}
		}catch(SQLException e){
			Main.writeError("Error closing Connection: " + e.getMessage());
		}
	}

	public abstract boolean initialize();

	public abstract boolean open();

	public abstract void executeStatements(String[] statements);
}
