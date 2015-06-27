// McClasses
// Author: cblacks26
// Date: Jun 14, 2015

package io.github.cblacks26.mcclass.listeners;

import io.github.cblacks26.mcclass.Main;
import io.github.cblacks26.mcclass.events.ClassRankUpEvent;
import io.github.cblacks26.mcclass.events.SkillLevelUpEvent;
import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.user.User;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.meta.FireworkMeta;

public class SkillsListener implements Listener{
	
	@EventHandler
	public void onRankUpEvent(ClassRankUpEvent event){
		User user = event.getUser();
		Player player = Bukkit.getPlayer(user.getID());
		user.getUserSkillClass().addRank();
		user.scoreboard();
		player.sendMessage(ChatColor.AQUA + "You Ranked up!");
		Location loc = player.getLocation();
		Firework firework = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta meta = firework.getFireworkMeta();
		FireworkEffect fe = FireworkEffect.builder().with(Type.BALL_LARGE).withColor(Color.BLUE).build();
		meta.addEffect(fe);
		meta.setPower(3);
		firework.setFireworkMeta(meta);
	}
	
	@EventHandler
	public void onSkillLevelUpEvent(SkillLevelUpEvent event){
		User user = event.getUser();
		Player player = Bukkit.getPlayer(user.getID());
		user.getUserSkillClass().addLevel(event.getSkill());
		user.scoreboard();
		player.sendMessage(ChatColor.AQUA + event.getSkill().getName() + " Level Up!");
	}
	
	@EventHandler
	public void onSkillXPGainEvent(SkillXPGainEvent event){
		User user = event.getUser();
		Player player = Bukkit.getPlayer(user.getID());
		player.sendMessage(event.getSkill().getName()+": +"+event.getXPGained()+"xp");
		user.getUserSkillClass().addExp(event.getSkill(), event.getXPGained());
		user.getUserSkillClass().addRankXP(event.getXPGained());
		Main.depositPlayer(player, event.getXPGained());
	}
	
}
