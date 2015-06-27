// McClasses
// Author: cblacks26
// Date: Jun 23, 2015

package io.github.cblacks26.mcclass.commands;

import io.github.cblacks26.mcclass.Main;
import io.github.cblacks26.mcclass.skillclass.SkillClass;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandSkillClassList implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("VList")){
			sender.sendMessage(Main.getLineWithText("Skill Classes"));
			for(SkillClass sc:SkillClass.getSkillClasses()){
				String skills = "";
				for(int i = 0; i < sc.getSkills().size(); i++){
					if(i == sc.getSkills().size()-1)
						skills += sc.getSkills().get(i).getName();
					else
						skills += sc.getSkills().get(i).getName()+", ";
						
				}
				sender.sendMessage(ChatColor.AQUA + sc.getName()+" - "+skills);
			}
			sender.sendMessage(Main.getLine());
			return true;
		}
		return false;
	}

}
