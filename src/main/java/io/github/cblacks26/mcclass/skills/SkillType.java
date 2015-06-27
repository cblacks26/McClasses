// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.skills;

public enum SkillType {
	
	ACROBATICS("Acrobatics", "acrobat"),
	ARCHERY("Archery", "archery"),
	WOODCUTTING("WoodCutting", "woodcutting"),
	HOE("Hoe", "hoe"),
	MINING("Mining", "mining"),
	DIGGING("Digging", "digging"),
	WEAPONRY("Weaponry", "weaponry"),
	UNARMED("Unarmed", "unarmed");
	
	private String name;
	private String sqlname;
	
	SkillType(String name, String sqlname){
		this.name = name;
		this.sqlname = sqlname;
	}
	
	public String getName(){
		return name;
	}
	
	public String getSQLName(){
		return sqlname;
	}	
}
