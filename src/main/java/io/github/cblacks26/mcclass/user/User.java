// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.user;

import io.github.cblacks26.mcclass.database.DatabaseUtil;
import io.github.cblacks26.mcclass.skillclass.SkillClass;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.skills.Skill;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class User {

	private UserSkillClass userSkillClass;
	private Player player;
	private UUID id;
	private Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
	private Objective boardClass;
	
	public User(UUID id){
		this.player = Bukkit.getPlayer(id);
		this.id = id;
		loadData();
		boardClass = board.registerNewObjective(ChatColor.AQUA+userSkillClass.getSkillClass().getName(), "dummy");
	}
	
	public Player getPlayer(){
		return player;
	}
	
	public void setPlayer(Player player){
		this.player = player;
	}
	
	public UUID getID(){
		return id;
	}
	
	public UserSkillClass getUserSkillClass(){
		return userSkillClass;
	}
	
	public void setUserSkillClass(UserSkillClass userSkillClass){
		this.userSkillClass = userSkillClass;
	}
	
	private void loadData(){
		setUserSkillClass(DatabaseUtil.loadUser(id));
	}
	
	public void switchClass(SkillClass skillClass){
		DatabaseUtil.saveClass(id, getUserSkillClass());
		setUserSkillClass(DatabaseUtil.loadUserClass(id, skillClass));
		if(board.getObjective(ChatColor.AQUA+userSkillClass.getSkillClass().getName()) != null){
			boardClass = board.getObjective(ChatColor.AQUA+userSkillClass.getSkillClass().getName());
		}else{
			boardClass = board.registerNewObjective(ChatColor.AQUA+userSkillClass.getSkillClass().getName(), "dummy");
		}
		scoreboard();
	}
	
	public void saveClass(){
		DatabaseUtil.saveClass(id, userSkillClass);
	}
	
	public void scoreboard(){
		boardClass.setDisplaySlot(DisplaySlot.SIDEBAR);
		Score rank = boardClass.getScore(ChatColor.GREEN+"Rank:");
		Score power = boardClass.getScore(ChatColor.GOLD+"Power:");
		rank.setScore(userSkillClass.getRank());
		power.setScore(userSkillClass.getPower());
		Score[] score = new Score[userSkillClass.getSkillClass().getSkills().size()];
		for(int i = 0; i < userSkillClass.getSkillClass().getSkills().size(); i++){
			Skill skill = userSkillClass.getSkillClass().getSkills().get(i);
			score[i] = boardClass.getScore(ChatColor.GREEN+skill.getScoreboardName()+":");
			score[i].setScore(userSkillClass.getLevel(skill));
		}
		player.setScoreboard(board);
	}
}
