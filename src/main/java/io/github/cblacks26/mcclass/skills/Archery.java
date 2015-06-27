// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass.skills;

import java.util.HashMap;

import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Archery extends Skill implements Listener{

	private static HashMap<EntityType, Double> entities = new HashMap<EntityType, Double>();
	private static Double damage = 0.05;
	
	public Archery(String name, String scoreboardName) {
		super(name, scoreboardName);
	}
	
	private static Double getXP(EntityType entity){
		if(entities.containsKey(entity))
			return entities.get(entity);
		return 2.0;
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageByEntityEvent event){
		if(event.getCause() == DamageCause.PROJECTILE){
			if(event.getDamager() instanceof Arrow){
				Arrow a = (Arrow)event.getDamager();
				if(a.getShooter() instanceof Player){
					Player player = (Player)a.getShooter();
					if(!(hasSkill(player.getUniqueId()))){
						return;
					}
					Double damagestart = event.getDamage();
					Double dmg = UserManager.getUser(player.getUniqueId()).getUserSkillClass().getLevel(this) * damage;
					event.setDamage(DamageModifier.BASE, damagestart+dmg);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event){
		PluginManager pm = Bukkit.getServer().getPluginManager();
		EntityType entity = event.getEntity().getType();
		EntityDamageEvent e = event.getEntity().getLastDamageCause();
		if(e instanceof EntityDamageByEntityEvent){
			EntityDamageByEntityEvent entityevent = (EntityDamageByEntityEvent) e;
			if(entityevent.getDamager() instanceof Arrow){
				Arrow a = (Arrow)entityevent.getDamager();
				if(a.getShooter() instanceof Player){
					Player player = (Player) a.getShooter();
					if(!(hasSkill(player.getUniqueId()))){
						return;
					}
					Double xp = getXP(entity);
					SkillXPGainEvent skillevent = new SkillXPGainEvent(player.getUniqueId(), this, xp);
					pm.callEvent(skillevent);
				}
			}
		}
	}
	
	@EventHandler
	public void ArrowShot(EntityShootBowEvent event){
		if ((!(event.getEntity() instanceof Player)) || (!(event.getProjectile() instanceof Arrow)))
			return;
		Player player = (Player)event.getEntity();
		UserSkillClass userSkillClass = UserManager.getUser(player.getUniqueId()).getUserSkillClass();
		if(!(userSkillClass.isBound()))
			return;
		if(userSkillClass.getBind() != this)
			return;
		Arrow arrow = (Arrow)event.getProjectile();
		arrow.setFireTicks(100);
		userSkillClass.useAbility(10);
	} 
	
	@EventHandler
	public void InteractEvent(PlayerInteractEvent event){
		if(!(hasSkill(event.getPlayer().getUniqueId())))
			return;
		ItemStack item = event.getPlayer().getItemInHand();
		if(item.getType() == Material.BOW){
			if(event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)){
				User user = UserManager.getUser(event.getPlayer().getUniqueId());
				user.getUserSkillClass().toggleBind(this);
				if(!(user.getUserSkillClass().isBound()))
					event.getPlayer().sendMessage("Blazing Arrows Off");
				else
					event.getPlayer().sendMessage("Blazing Arrows On");
			}
		}
	}
	
	public void loadSettings(FileConfiguration config){
		ConfigurationSection section = config.getConfigurationSection("Skills.Archery");
		if(section.getKeys(false)!=null){
			for(String s:section.getKeys(false)){
				if(EntityType.valueOf(s.toUpperCase()) != null){
					EntityType type = EntityType.valueOf(s.toUpperCase());
					Double val = config.getDouble("Skills.Archery."+s);
					entities.put(type, val);
				}
			}
		}
	}
}
