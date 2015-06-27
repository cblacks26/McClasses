// McClasses
// Author: cblacks26
// Date: Jun 23, 2015

package io.github.cblacks26.mcclass.skills;

import io.github.cblacks26.mcclass.events.SkillXPGainEvent;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;

public class Digging extends Skill implements Listener{

	private static HashMap<Material, Double> blocks = new HashMap<Material, Double>();
	
	public Digging(String name, String scoreboardName) {
		super(name, scoreboardName);
	}

	private static Double getXP(Material type){
		if(blocks.containsKey(type)){
			return blocks.get(type);
		}
		return 0.0;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		UUID id = event.getPlayer().getUniqueId();
		if(!(hasSkill(id))){
			return;
		}
		UserSkillClass userSkillClass = UserManager.getUser(id).getUserSkillClass();
		Material block = event.getBlock().getType();
		ItemStack item = event.getPlayer().getItemInHand();
		PluginManager pm = Bukkit.getServer().getPluginManager();
		if(getXP(block) > 0.0){
			switch(item.getType()){
				case DIAMOND_SPADE: case GOLD_SPADE: case IRON_SPADE: case STONE_SPADE: case WOOD_SPADE:
					SkillXPGainEvent skillevent = new SkillXPGainEvent(id, this, getXP(block));
					pm.callEvent(skillevent);
					Random random = new Random();
					int rand = random.nextInt(100);
					if(userSkillClass.getLevel(this) > 40){
						short dur = event.getPlayer().getItemInHand().getDurability();
						if(rand%5 == 0){
							event.getPlayer().getItemInHand().setDurability((short) (dur+1));
						}
					}else{
						int mod = 25 - (userSkillClass.getLevel(this) / 2);
						short dur = event.getPlayer().getItemInHand().getDurability(); 
						if(rand%mod == 0){
							if(dur == 0){
								return;
							}
							event.getPlayer().getItemInHand().setDurability((short) (dur-1));
						}
					}
					break;
				default:
					break;
			}
		}
	}
	
	@EventHandler
	public void onBlockDamageEvent(BlockDamageEvent event){
		UUID id = event.getPlayer().getUniqueId();
		UserSkillClass userSkillClass = UserManager.getUser(id).getUserSkillClass();
		if(!(hasSkill(id)))
			return;
		ItemStack item = event.getPlayer().getItemInHand();
		switch(item.getType()){
			case DIAMOND_SPADE: case GOLD_SPADE: case IRON_SPADE: case STONE_SPADE: case WOOD_SPADE:
				Material block = event.getBlock().getType();
				if(getXP(block) > 0.0){
					if(userSkillClass.isBound()){
						if(userSkillClass.getBind().equals(this)){
							if(userSkillClass.canUseAbility(10)){
								event.getBlock().breakNaturally();
								userSkillClass.useAbility(10);
								Bukkit.getPluginManager().callEvent(new SkillXPGainEvent(id, this, getXP(block)));
							}
						}
					}
				}
				break;
			default:
				break;
		}
	}

	@EventHandler
	public void InteractEvent(PlayerInteractEvent event){
		ItemStack item = event.getPlayer().getItemInHand();
		if(!(hasSkill(event.getPlayer().getUniqueId())))
			return;
		switch(item.getType()){
			case DIAMOND_SPADE: case GOLD_SPADE: case IRON_SPADE: case STONE_SPADE: case WOOD_SPADE:
				if(event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
					User user = UserManager.getUser(event.getPlayer().getUniqueId());
					user.getUserSkillClass().toggleBind(this);
					if(!(user.getUserSkillClass().isBound()))
						event.getPlayer().sendMessage("InstaDig Off");
					else
						event.getPlayer().sendMessage("InstaDig On");
				}
				break;
			default:
				break;
		}
	}
	
	@Override
	public void loadSettings(FileConfiguration config) {
		ConfigurationSection section = config.getConfigurationSection("Skills.Digging");
		if(section.getKeys(false)!=null){
			for(String s:section.getKeys(false)){
				if(Material.valueOf(s.toUpperCase()) != null){
					Material type = Material.valueOf(s.toUpperCase());
					Double val = config.getDouble("Skills.Digging."+s);
					blocks.put(type, val);
				}
			}
		}
	}

}
