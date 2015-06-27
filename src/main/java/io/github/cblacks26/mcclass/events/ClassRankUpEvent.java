// McClasses
// Author: cblacks26
// Date: Jun 12, 2015

package io.github.cblacks26.mcclass.events;

import java.util.UUID;

import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ClassRankUpEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private User user;
    private int rank;
    
    public ClassRankUpEvent(UUID id, int rank) {
        this.user = UserManager.getUser(id);
        this.rank = rank;
    }

	public User getUser() {
        return user;
    }
    
    public int getRank(){
    	return rank;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
