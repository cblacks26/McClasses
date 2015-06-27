// McClasses
// Author: cblacks26
// Date: Jun 15, 2015

package io.github.cblacks26.mcclass.skills;

import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Unarmed extends Skill implements Listener {

	private static HashMap<EntityType, Double> entities = new HashMap<EntityType, Double>();
	private static Double damage = 0.05;

	public Unarmed(String name, String scoreboardName) {
		super(name, scoreboardName);
	}

	private static Double getXP(EntityType entity){
		if(entities.containsKey(entity))
			return entities.get(entity);
		return 2.0;
	}
	
	@EventHandler
	public void EntityDamagebyPlayer(EntityDamageByEntityEvent event){
		if(event.getDamager() instanceof Player){
			Player player = (Player)event.getDamager();
			UUID id = player.getUniqueId();
			double damagestart = event.getDamage();
			if(!(hasSkill(id))){
				return;
			}
			ItemStack item = player.getItemInHand();
			UserSkillClass userSkillClass = UserManager.getUser(id).getUserSkillClass();
			if(item.getType().equals(Material.AIR)){
				Double dmg = userSkillClass.getLevel(this) * damage;
				if(userSkillClass.isBound()){
					if(userSkillClass.getBind().equals(this)){
						dmg += 2;
						userSkillClass.useAbility(20);
					}
				}
				event.setDamage(DamageModifier.BASE, damagestart+dmg);
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
			if(entityevent.getDamager() instanceof Player){
				Player player = (Player) entityevent.getDamager();
				if(!(hasSkill(player.getUniqueId()))){
					return;
				}
				ItemStack item = player.getItemInHand();
				if(item.getType().equals(Material.AIR)){
					Double xp = getXP(entity);
					SkillXPGainEvent skillevent = new SkillXPGainEvent(player.getUniqueId(), this, xp);
					pm.callEvent(skillevent);
				}
			}
		}
	}

	@EventHandler
	public void InteractEvent(PlayerInteractEvent event){
		if(!(hasSkill(event.getPlayer().getUniqueId())))
			return;
		ItemStack item = event.getPlayer().getItemInHand();
		if(item.getType() == Material.AIR){
			if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
				User user = UserManager.getUser(event.getPlayer().getUniqueId());
				user.getUserSkillClass().toggleBind(this);
				if(!(user.getUserSkillClass().isBound()))
					event.getPlayer().sendMessage("Power Punch Off");
				else
					event.getPlayer().sendMessage("Power Punch On");
			}
		}
	}
	
	@Override
	public void loadSettings(FileConfiguration config) {
		ConfigurationSection section = config.getConfigurationSection("Skills.Unarmed");
		if(section.getKeys(false)!=null){
			for(String s:section.getKeys(false)){
				if(EntityType.valueOf(s.toUpperCase()) != null){
					EntityType type = EntityType.valueOf(s.toUpperCase());
					Double val = config.getDouble("Skills.Unarmed."+s);
					entities.put(type, val);
				}
			}
		}
	}

}
