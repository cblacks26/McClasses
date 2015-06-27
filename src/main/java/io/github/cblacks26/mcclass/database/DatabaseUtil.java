// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.database;

import io.github.cblacks26.mcclass.Main;
import io.github.cblacks26.mcclass.skillclass.SkillClass;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.skills.Skill;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseUtil {

	private static Database sql;

	public static void setDatabase(Database sql){
		DatabaseUtil.sql = sql;
	}

	public static void createTables() {
		List<SkillClass> list = SkillClass.getSkillClasses();
		List<String> array = new ArrayList<String>();
		array.add("CREATE TABLE IF NOT EXISTS VSkills (id Text, skillclass Text);");
		for (SkillClass sc : list) {
			String table = "CREATE TABLE IF NOT EXISTS VSkills_" + sc.getSQLName() + " (id Text,";
			for (Skill skill: sc.getSkills()) {
				table += skill.getSkillType().getSQLName() + "_lvl Integer, " + skill.getSkillType().getSQLName() + "_xp Double, ";
			}
			table += "rank Integer, rankxp Double);";
			array.add(table);
		}
		String[] a = new String[array.size()];
		for(int i = 0; i < array.size(); i++){
			a[i] = array.get(i);
		}
		sql.executeStatements(a);
	}

	public static void saveClass(UUID id, UserSkillClass sc){
		String[] update = new String[2];
		update[0] = "UPDATE VSkills_"+sc.getSkillClass().getSQLName()+" SET ";
		List<Skill> types = sc.getSkillClass().getSkills();
		for (int i = 0; i < types.size(); i++) {
			Skill s = types.get(i);
			update[0] += s.getSkillType().getSQLName()+"_lvl = "+sc.getLevel(s)+", "+s.getSkillType().getSQLName()+"_xp = "+sc.getExp(s)+", ";
		}
		update[0] += "rank = "+sc.getRank()+", rankxp = "+sc.getRankXP();
		update[0] += " WHERE id = '"+id+"';";
		update[1] = "UPDATE VSkills SET skillclass = '"+sc.getSkillClass().getSQLName()+"' WHERE id = '"+id+"';";
		sql.executeStatements(update);
	}

	private static boolean checkUser(UUID id){
		try{
			sql.open();
			Connection c = sql.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT skillclass FROM VSkills WHERE id='"+id+"';");
			if(rs.next()){
				rs.close();
				s.close();
				c.close();
				return true;
			}else{
				rs.close();
				s.close();
				c.close();
				return false;
			}
		}catch(SQLException e){
			Main.writeError("Finding User Data "+e.getMessage());
		}
		return false;
	}

	public static UserSkillClass loadUser(UUID id){
		if(checkUser(id)==false){
			UserSkillClass userSkillClass = createUser(id);
			return userSkillClass;
		}else{
			try{
				sql.open();
				Connection c = sql.getConnection();
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery("SELECT skillclass FROM VSkills WHERE id='"+id+"';");
				rs.next();
				String scString = rs.getString("skillclass");
				SkillClass skillClass = null;
				for(SkillClass sc:SkillClass.getSkillClasses()){
					if(sc.getSQLName().equals(scString)){
						skillClass = SkillClass.getSkillClass(scString);
					}
				}
				if(skillClass == null){
					skillClass = SkillClass.getSkillClasses().get(0);
				}
				rs.close();
				s.close();
				c.close();
				return loadUserClass(id, skillClass);
			}catch(SQLException e){
				Main.writeError("Loading user data "+e.getMessage());
			}
			return null;
		}
	}

	public static UserSkillClass createUser(UUID id){
		String[] array = new String[SkillClass.getSkillClasses().size()+1];
		array[0] = "INSERT INTO VSkills (id, skillclass) VALUES ('"+id+"','"+SkillClass.getSkillClasses().get(0).getSQLName()+"');";
		for(int i = 0; i < SkillClass.getSkillClasses().size(); i++){
			SkillClass skillClass = SkillClass.getSkillClasses().get(i);
			String table = "INSERT INTO VSkills_"+skillClass.getSQLName()+" (id, ";
			for(Skill skill: skillClass.getSkills()){
				table += skill.getSkillType().getSQLName()+"_lvl, "+skill.getSkillType().getSQLName()+"_xp, ";
			}
			table += "rank, rankxp) VALUES ('"+id+"', ";
			for(int j = 0; j < skillClass.getSkills().size(); j++){
				table += "1,0,";
			}
			table += "1,0);";
			array[i+1] = table;
		}
		sql.executeStatements(array);
		SkillClass sc = SkillClass.getSkillClasses().get(0);
		UserSkillClass userSkillClass = new UserSkillClass(id, sc);
		userSkillClass.setRank(1);
		userSkillClass.setRankXP(0,false);
		for(Skill skill: sc.getSkills()){
			userSkillClass.setLevel(skill, 1);
			userSkillClass.setExp(skill, 0, false);
		}
		return userSkillClass;
	}

	public static UserSkillClass loadUserClass(UUID id, SkillClass skillClass){
		UserSkillClass userSkillClass = new UserSkillClass(id, skillClass);
		try{
			sql.open();
			Connection c = sql.getConnection();
			Statement s = c.createStatement();
			ResultSet rs = s.executeQuery("SELECT * FROM VSkills_"+skillClass.getSQLName()+" WHERE id='"+id+"';");
			if(rs.next()){
				for(Skill skill: skillClass.getSkills()){
					userSkillClass.setLevel(skill, rs.getInt(skill.getSkillType().getSQLName()+"_lvl"));
					userSkillClass.setExp(skill, rs.getDouble(skill.getSkillType().getSQLName()+"_xp"), false);
				}
				userSkillClass.setRank(rs.getInt("rank"));
				userSkillClass.setRankXP(rs.getDouble("rankxp"),false);
				userSkillClass.setMaxPower(90 + userSkillClass.getRank());
				userSkillClass.setPower(userSkillClass.getMaxPower());
				rs.close();
				s.close();
				c.close();
				return userSkillClass;
			}else{
				rs.close();
				String table = "INSERT INTO VSkills_"+skillClass.getSQLName()+" (id, ";
				for(Skill skill: skillClass.getSkills()){
					table += skill.getSkillType().getSQLName()+"_lvl, "+skill.getSkillType().getSQLName()+"_xp, ";
				}
				table += "rank, rankxp) VALUES ('"+id+"', ";
				for(int j = 0; j < skillClass.getSkills().size(); j++){
					table += "1,0,";
				}
				table += "1,0);";
				s.execute(table);
				s.close();
				c.close();
				return userSkillClass;
			}
		}catch(SQLException e){
			Main.writeError("Loading user data "+e.getMessage());
		}
		return null;
	}
	
	public static void executeStatements(String[] statements){
		sql.executeStatements(statements);
	}
}
