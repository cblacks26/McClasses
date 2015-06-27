// McClasses
// Author: cblacks26
// Date: Jun 23, 2015

package io.github.cblacks26.mcclass.commands;

import io.github.cblacks26.mcclass.skillclass.SkillClass;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandSwitch implements CommandExecutor{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("VSwitch")){
			if(!(sender instanceof Player)){
				sender.sendMessage("Cannot execute this command unless you are a player");
				return false;
			}
			Player player = (Player) sender;
			if(args.length == 1){
				for(SkillClass sc:SkillClass.getSkillClasses()){
					if(args[0].equalsIgnoreCase(sc.getName())){
						User user = UserManager.getUser(player.getUniqueId());
						if(user.getUserSkillClass().getPower() != user.getUserSkillClass().getMaxPower()){
							player.sendMessage(ChatColor.RED+"You must wait untill your power is recharged to change your class!");
							return true;
						}
						user.switchClass(sc);
						player.sendMessage("Skill Class switched to "+sc.getName());
						return true;
					}
				}
			}
		}
		return false;
	}

}
