// McClasses
// Author: cblacks26
// Date: Jun 17, 2015

package io.github.cblacks26.mcclass.skills;

import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.plugin.PluginManager;

public class Acrobatics extends Skill implements Listener{

	private Double damageModifier;
	
	public Acrobatics(String name, String scoreboardName) {
		super(name, scoreboardName);
	}

	@Override
	public void loadSettings(FileConfiguration config) {
		damageModifier = config.getDouble("Skills.Acrobatics.Damage Modifier");
	}
	
	public double getFallDamage(Player player, Double damage){
		int lvl = UserManager.getUser(player.getUniqueId()).getUserSkillClass().getLevel(this);
		double dm = damage - (lvl * damageModifier);
		return dm;
	}

	@EventHandler
	public void PlayerDamage(EntityDamageEvent event){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if(event.getEntity() instanceof Player){
			Player player = (Player)event.getEntity();
			if(!(hasSkill(player.getUniqueId()))){
				return;
			}
		    if (event.getCause() == DamageCause.FALL) {
		    	double dmg = event.getDamage();
		    	SkillXPGainEvent skillevent = new SkillXPGainEvent(player.getUniqueId(), this, dmg);
		    	pm.callEvent(skillevent);
		    	event.setDamage(getFallDamage(player, event.getDamage()));
		    }
		}
	}
	
}
