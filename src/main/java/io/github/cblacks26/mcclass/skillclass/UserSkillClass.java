// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.skillclass;

import io.github.cblacks26.mcclass.Main;
import io.github.cblacks26.mcclass.events.ClassRankUpEvent;
import io.github.cblacks26.mcclass.events.SkillLevelUpEvent;
import io.github.cblacks26.mcclass.skills.Skill;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;

public class UserSkillClass {

	private HashMap<Skill, Integer> level = new HashMap<Skill, Integer>();
	private HashMap<Skill, Double> exp = new HashMap<Skill, Double>();
	private Skill bind = null;
	private int power;
	private int maxpower;
	private int rank;
	private double rankXP;
	private SkillClass skillClass;
	private UUID id;

	public UserSkillClass(UUID id, SkillClass skillClass){
		setUserID(id);
		this.skillClass = skillClass;
		rank = 1;
		rankXP = 0.0;
		for(Skill skill: skillClass.getSkills()){
			setLevel(skill, 1);
			setExp(skill, 0.0, false);
		}
		setMaxPower(90);
		setPower(90);
	}
	// Returns the User
	public UUID getUserID(){
		return id;
	}
	// Sets the User variable
	public void setUserID(UUID id){
		this.id = id;
	}
	// Returns the loadedVClass
	public SkillClass getSkillClass(){
		return skillClass;
	}
	// Sets the experience of the specified Skill
	public void setExp(Skill skill, double xp, boolean checkLVL){
		if(checkLVL==false){
			exp.put(skill, xp);
		}else{
			if(skillClass.getSkills().contains(skill)){
				int lvl = getLevel(skill);
				double xpToLevel = 10 * (lvl+1) * ((lvl+1)/2);
				boolean lvlUp = false;
				while(xp >= xpToLevel){
					lvlUp = true;
					lvl++;
					xp -= xpToLevel;
					xpToLevel = 10 * (lvl+1) * ((lvl+1)/2);
				}
				if(lvlUp){
					SkillLevelUpEvent event = new SkillLevelUpEvent(id, skill, lvl);
					Bukkit.getServer().getPluginManager().callEvent(event);
				}
				exp.put(skill, xp);
			}else{
				Main.writeError("setExp() - Skill " + skill.getName() + " not in class " + skillClass.getName());
			}
		}
	}
	// Adds experience to the specified Skill
	public void addExp(Skill skill, double xp){
		if(skillClass.getSkills().contains(skill)){
			double c = exp.get(skill);
			c += xp;
			int lvl = getLevel(skill);
			double xpToLevel = 10 * (lvl+1) * ((lvl+1)/2);
			if(c >= xpToLevel){
				SkillLevelUpEvent event = new SkillLevelUpEvent(id, skill, lvl + 1);
				Bukkit.getServer().getPluginManager().callEvent(event);
				c -= xpToLevel;
			}
			exp.put(skill, c);
		}else{
			Main.writeError("addExp() - Skill " + skill.getName() + " not in class " + skillClass.getName());
		}
	}
	// Subtracts experience from the specified Skill
	public void subExp(Skill skill, double xp){
		if(skillClass.getSkills().contains(skill)){
			double c = exp.get(skill);
			exp.put(skill, c-xp);
			if(exp.get(skill) < 0)
				exp.put(skill, 0.0);
		}else{
			Main.writeError("subExp() - Skill " + skill.getName() + " not in class " + skillClass.getName());
		}
	}
	// Gets the experience for the specified Skill
	public double getExp(Skill skill){
		if(skillClass.getSkills().contains(skill))
			return exp.get(skill);
		else{
			Main.writeError("getExp() - Skill " + skill.getName() + " not in class " + skillClass.getName());
			return 0;
		}

	}
	// Sets the level for the specified Skill
	public void setLevel(Skill skill, int lvl){
		if(skillClass.getSkills().contains(skill)){
			level.put(skill, lvl);
		}else{
			Main.writeError("setLevel() - Skill " + skill.getName() + " not in class " + skillClass.getName());
		}
	}
	// Adds a level to the specified Skill
	public void addLevel(Skill skill){
		if(skillClass.getSkills().contains(skill)){
			int lvl = level.get(skill);
			level.put(skill, lvl+1);
		}else{
			Main.writeError("addLevel() - Skill " + skill.getName() + " not in class " + skillClass.getName());
		}
	}
	// Subtracts a level from the specified Skill
	public void subLevel(Skill skill){
		if(skillClass.getSkills().contains(skill)){
			int lvl = level.get(skill);
			level.put(skill, lvl--);
		}else{
			Main.writeError("subLevel() - Skill " + skill.getName() + " not in class " + skillClass.getName());
		}
	}
	// Gets the level for the specified Skill
	public int getLevel(Skill skill){
		if(skillClass.getSkills().contains(skill))
			return level.get(skill);
		else{
			Main.writeError("getLevel() - Skill " + skill.getName() + " not in class " + skillClass.getName());
			return 0;
		}

	}
	// Gets the rank of the SkillClass
	public int getRank() {
		return rank;
	}
	// Set the rank for the SkillClass
	public void setRank(int rank) {
		this.rank = rank;
	}
	// Adds a rank for the SkillClass
	public void addRank(){
		rank++;
		addMaxPower();
		refreshPower();
	}
	// Subtracts a rank for the SkillClass\
	public void subRank(){
		rank--;
	}
	// Gets the experience of the SkillClass
	public double getRankXP() {
		return rankXP;
	}
	// Sets the experience for the SkillClass
	public void setRankXP(double rankXP, boolean checkRank) {
		if(checkRank==false){
			this.rankXP = rankXP;
		}else{
			double xpToLevel = 10 * (rank+1) * ((rank+1)/2);
			boolean rankUp = false;
			int r = rank;
			while(rankXP >= xpToLevel){
				rankUp = true;
				r++;
				rankXP -= xpToLevel;
				xpToLevel = 10 * (r+1) * ((r+1)/2);
			}
			if(rankUp){
				ClassRankUpEvent event = new ClassRankUpEvent(id, r);
				Bukkit.getServer().getPluginManager().callEvent(event);
			}
			this.rankXP = rankXP;
		}
	}
	// Adds the specified experience for the SkillClass
	public void addRankXP(double xp){
		double xpToLevel = 10 * (rank+1) * ((rank+1)/2);
		if(rankXP + xp >= xpToLevel){
			ClassRankUpEvent event = new ClassRankUpEvent(id, rank + 1);
			Bukkit.getServer().getPluginManager().callEvent(event);
			rankXP += xp;
			rankXP -= xpToLevel;
		}else
			rankXP += xp;
	}
	// Subtracts the specified experience from the SkillClass but doesn't demote
	public void subRankXP(double xp){
		rankXP -= xp;
		if(rankXP < 0)
			rankXP = 0;
	}
	// Gets the player's max power
	public int getMaxPower(){
		return maxpower;
	}
	// Sets the player's max power
	public void setMaxPower(int maxpower){
		this.maxpower = maxpower;
	}
	// Adds power to the player's max power
	public void addMaxPower(){
		setMaxPower(getMaxPower()+1);
	}
	// Gets the player's current power
	public int getPower(){
		return power;
	}
	// Sets the player's current power
	public void setPower(int power){
		if(power > maxpower){
			this.power = maxpower;
		}else{
			this.power = power;
		}
	}
	// Adds power to the player's current power
	public void addPower(){
		if(power == maxpower)
			return;
		setPower(power+1);
		UserManager.getUser(id).scoreboard();
	}
	// Refreshes the player's current power to their max power
	public void refreshPower(){
		setPower(maxpower);
		UserManager.getUser(id).scoreboard();
	}
	// Checks if the player can use an ability
	public boolean canUseAbility(int power){
		if(getPower() >= power)
			return true;
		return false;
	}
	// Reduces the players power after ability was used
	public void useAbility(int power){
		setPower(getPower()-power);
		UserManager.getUser(id).scoreboard();
	}
	// Checks if an ability is bound
	public boolean isBound(){
		if(bind != null)
			return true;
		return false;
	}
	// Gets the bound ability
	public Skill getBind(){
		return bind;
	}
	// Toggles the bound ability
	public void toggleBind(Skill skill){
		if(isBound()){
			if(getBind().equals(skill)){
				this.bind = null;
			}else{
				this.bind = skill;
			}
		}else{
			this.bind = skill;
		}
	}
}
