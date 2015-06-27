// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.skills;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;

public abstract class Skill{

	private static HashMap<SkillType, Skill> skills = new HashMap<SkillType, Skill>();
	private String name;
	private String scoreboardName;
	private SkillType skillType;

	public Skill(String name, String scoreboardName){
		setName(name);
		setScoreboardName(scoreboardName);
		for(SkillType type:SkillType.values())
			if(type.getName().equals(name))
				skillType = type;
	}

	protected boolean hasSkill(UUID id){
		return UserManager.getUser(id).getUserSkillClass().getSkillClass().hasSkillType(skillType);
	}

	public abstract void loadSettings(FileConfiguration config);
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getScoreboardName() {
		return scoreboardName;
	}

	public void setScoreboardName(String scoreboardName) {
		this.scoreboardName = scoreboardName;
	}

	public SkillType getSkillType(){
		return skillType;
	}

	public static Skill getSkill(SkillType skillType) {
		return skills.get(skillType);
	}
	
	public static HashMap<SkillType, Skill> getSkills(){
		return skills;
	}

	public static void addSkill(Skill skill) {
		skills.put(skill.getSkillType(), skill);
	}
}
