// McClasses
// Author: cblacks26
// Date: Jun 14, 2015

package io.github.cblacks26.mcclass.listeners;

import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener{

	@EventHandler
	public void PlayerJoin(PlayerJoinEvent event){
		UUID id = event.getPlayer().getUniqueId();
		if(UserManager.getUser(id) == null){
			UserManager.addUser(id);
		}else{
			UserManager.getUser(id).setPlayer(event.getPlayer());
			UserManager.getUser(id).scoreboard();
		}
	}
	
	@EventHandler
	public void PlayerLeave(PlayerQuitEvent event){
		UUID id = event.getPlayer().getUniqueId();
		User user = UserManager.getUser(id);
		if(user.getUserSkillClass().getPower() == user.getUserSkillClass().getMaxPower()){
			user.saveClass();
			UserManager.removeUser(id);
		}
	}	
}
