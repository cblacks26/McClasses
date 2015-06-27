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

public class SkillXPGainEvent extends Event{

	private static final HandlerList handlers = new HandlerList();
	private Skill skill;
	private User user;
	private Double xp;

	public SkillXPGainEvent(UUID id, Skill skill, Double xp) {
		this.user = UserManager.getUser(id);
		this.skill = skill;
		this.xp = xp;
	}

	public User getUser() {
		return user;
	}

	public Skill getSkill(){
		return skill;
	}

	public Double getXPGained(){
		return xp;
	}

	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}
}
