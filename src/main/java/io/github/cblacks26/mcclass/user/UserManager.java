// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.user;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UserManager {

	private static final HashMap<UUID, User> users = new HashMap<UUID, User>();
	
	public static final User getUser(UUID id){
		return users.get(id);
	}
	
	public static final HashMap<UUID, User> getUsers(){
		return users;
	}
	
	public static final void addUser(UUID id){
		User user = new User(id);
		user.scoreboard();
		users.put(id, user);
	}
	
	public static final void removeUser(UUID id){
		users.remove(id);
	}
	
	public static final void clearUsers(){
		users.clear();
	}
	
	public static final void saveUsers(){
		for(User user:users.values()){
			user.saveClass();
		}
	}
	
	public static final void addUsers(){
		for(Player player:Bukkit.getOnlinePlayers()){
			addUser(player.getUniqueId());
		}
	}	
}
