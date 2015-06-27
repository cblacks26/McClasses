// McClasses
// Author: cblacks26
// Date: Jun 23, 2015

package io.github.cblacks26.mcclass.skills;

import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Weaponry extends Skill implements Listener{

	private static HashMap<EntityType, Double> entities = new HashMap<EntityType, Double>();
	private static List<Material> weapons = new ArrayList<Material>();
	private static Double damage = 0.05;
	
	public Weaponry(String name, String scoreboardName) {
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
			for(Material m:weapons){
				if(item.getType().equals(m)){
					Double dmg = UserManager.getUser(id).getUserSkillClass().getLevel(this) * damage;
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
			if(entityevent.getDamager() instanceof Player){
				Player player = (Player) entityevent.getDamager();
				if(!(hasSkill(player.getUniqueId()))){
					return;
				}
				ItemStack item = player.getItemInHand();
				for(Material m:weapons){
					if(item.getType().equals(m)){
						Double xp = getXP(entity);
						SkillXPGainEvent skillevent = new SkillXPGainEvent(player.getUniqueId(), this, xp);
						pm.callEvent(skillevent);
					}
				}
			}
		}
	}

	@Override
	public void loadSettings(FileConfiguration config) {
		ConfigurationSection sectionE = config.getConfigurationSection("Skills.Weaponry.Entities");
		ConfigurationSection section = config.getConfigurationSection("Skills.Weaponry");
		if(sectionE.getKeys(false)!=null){
			for(String s:sectionE.getKeys(false)){
				if(EntityType.valueOf(s.toUpperCase()) != null){
					EntityType type = EntityType.valueOf(s.toUpperCase());
					Double val = sectionE.getDouble(s);
					entities.put(type, val);
				}
			}
		}
		if(section.getStringList("Weapons") != null){
			for(String s:section.getStringList("Weapons")){
				if(Material.valueOf(s.toUpperCase()) != null){
					Material type = Material.valueOf(s.toUpperCase());
					weapons.add(type);
				}
			}
		}
	}

}
