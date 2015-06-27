// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.skillclass;

import io.github.cblacks26.mcclass.skills.Skill;
import io.github.cblacks26.mcclass.skills.SkillType;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class SkillClass {

	private static List<SkillClass> classes = new ArrayList<SkillClass>();
	private List<Skill> skills = new ArrayList<Skill>();
	private String name;
	private String sqlName;
	
	public SkillClass(String name){
		setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.sqlName = name.replaceAll(" ", "_").toLowerCase();
	}
	
	public String getSQLName(){
		return sqlName;
	}
	
	public void addSkill(Skill skill){
		skills.add(skill);
	}
	
	public List<Skill> getSkills(){
		return skills;
	}
	
	public boolean hasSkillType(SkillType type){
		for(Skill skill: skills){
			if(skill.getSkillType() == type){
				return true;
			}
		}
		return false;
	}
	
	public static void addSkillClass(SkillClass vclass){
		classes.add(vclass);
	}
	
	public static List<SkillClass> getSkillClasses(){
		return classes;
	}
	
	public static SkillClass getSkillClass(String name){
		for(SkillClass sc: getSkillClasses()){
			if(sc.getSQLName().equals(name)){
				return sc;
			}
		}
		return null;
	}
	
	public static void loadSkillClasses(FileConfiguration config){
		ConfigurationSection section = config.getConfigurationSection("Classes");
		for(String s:section.getKeys(false)){
			SkillClass sc = new SkillClass(s.toString());
			for(String string:section.getStringList(s+".Skills")){
				for(Skill skill:Skill.getSkills().values()){
					if(skill.getName().equals(string))
						sc.addSkill(skill); 
				}
			}
			addSkillClass(sc);
		}
	}
}