// McClasses
// Author: cblacks26
// Date: Jun 12, 2015

package io.github.cblacks26.mcclass.events;

import java.util.UUID;

import io.github.cblacks26.mcclass.skills.Skill;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillLevelUpEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private User user;
    private Skill skill;
    private int level;
    
    public SkillLevelUpEvent(UUID id, Skill skill, int level) {
        this.user = UserManager.getUser(id);
        this.skill = skill;
        this.level = level;
    }

	public User getUser() {
        return user;
    }
    
	public Skill getSkill(){
		return skill;
	}
    
    public int getLevel(){
    	return level;
    }
 
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
