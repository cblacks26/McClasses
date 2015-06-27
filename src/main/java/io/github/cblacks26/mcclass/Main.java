// McClasses
// Author: cblacks26
// Date: Jun 5, 2015

package io.github.cblacks26.mcclass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.github.cblacks26.mcclass.commands.CommandSkillClassList;
import io.github.cblacks26.mcclass.commands.CommandSwitch;
import io.github.cblacks26.mcclass.database.DatabaseUtil;
import io.github.cblacks26.mcclass.database.MySQL;
import io.github.cblacks26.mcclass.database.SQLite;
import io.github.cblacks26.mcclass.listeners.PlayerListener;
import io.github.cblacks26.mcclass.listeners.SkillsListener;
import io.github.cblacks26.mcclass.skillclass.SkillClass;
import io.github.cblacks26.mcclass.skillclass.UserSkillClass;
import io.github.cblacks26.mcclass.skills.Acrobatics;
import io.github.cblacks26.mcclass.skills.Archery;
import io.github.cblacks26.mcclass.skills.Digging;
import io.github.cblacks26.mcclass.skills.Mining;
import io.github.cblacks26.mcclass.skills.Skill;
import io.github.cblacks26.mcclass.skills.Unarmed;
import io.github.cblacks26.mcclass.skills.Weaponry;
import io.github.cblacks26.mcclass.skills.WoodCutting;
import io.github.cblacks26.mcclass.user.User;
import io.github.cblacks26.mcclass.user.UserManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin{

	private static Economy econ = null;
	private static Double moneymultiplier = 0.25;

	public void onEnable(){
		setupEconomy();
		registerListeners();  
		registerCommands();
		loadConfig();
		UserManager.addUsers();
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable(){
			public void run(){
				for(User user:UserManager.getUsers().values()){
					UserSkillClass userSkillClass = user.getUserSkillClass();
					if(userSkillClass.getPower() < userSkillClass.getMaxPower()){
						userSkillClass.addPower();
					}else{
						if(!(user.getPlayer().isOnline())){
							user.saveClass();
							UserManager.removeUser(user.getID());
						}
					}
				}
			}
		}, 0, 40);
	}

	public void onDisable(){
		FileConfiguration config = this.getConfig();
		for(SkillClass sc:SkillClass.getSkillClasses()){
			List<String> list = new ArrayList<String>();
			for(Skill s:sc.getSkills()){
				list.add(s.getName());
			}
			config.set("Last."+sc.getName()+".Skills", list);
		}
		this.saveConfig();
		UserManager.saveUsers();
		UserManager.clearUsers();
	}

	private void loadConfig(){
		this.saveDefaultConfig();
		FileConfiguration config = this.getConfig();
		// Loads SkillClasses and SKills
		for(Skill s:Skill.getSkills().values()){
			s.loadSettings(config);
		}
		SkillClass.loadSkillClasses(config);
		moneymultiplier = config.getDouble("Money Multiplier");
		// Sets up the Database
		String type = config.getString("Database.Type");
		if(type.equalsIgnoreCase("MySQL")){
			String host = config.getString("Database.Host");
			String port = config.getString("Database.Port");
			String dbname = config.getString("Database.DBName");
			String username = config.getString("Database.Username");
			String password = config.getString("Database.Password");
			DatabaseUtil.setDatabase(new MySQL(host,port,dbname,username,password));
		}else{
			File db = new File(getDataFolder() + File.separator + "Database.db");
			if(!(db.exists())){
				try {
					writeMessage("Creating Database File");
					db.createNewFile();
				} catch (IOException e) {
					writeError("Creating Database File: "+ e.getMessage());
				}
			}
			DatabaseUtil.setDatabase(new SQLite(db));
		}
		checkForSkillClassUpdates(config);
	}

	private static void checkForSkillClassUpdates(FileConfiguration config){
		DatabaseUtil.createTables();
		ConfigurationSection section = config.getConfigurationSection("Last");
		if(section != null){
			ArrayList<String> strings = new ArrayList<String>();
			Set<String> skillClasses = section.getKeys(false);
			// S represents each SkillClass
			for(String s:skillClasses){
				// Loads the SkillClass's last skills
				String sqlName = s.toLowerCase();
				sqlName.replaceAll(" ", "_");
				SkillClass sc = SkillClass.getSkillClass(sqlName);
				List<Skill> skills = new ArrayList<Skill>();
				for(String string:config.getStringList("Last."+s+".Skills")){
					for(Skill skill:Skill.getSkills().values()){
						if(skill.getName().equalsIgnoreCase(string))
							skills.add(skill);
					}
				}
				List<Skill> newSkills = new ArrayList<Skill>();
				for(Skill skill:sc.getSkills()){
					if(!(skills.contains(skill))){
						newSkills.add(skill);
					}
				}
				if(!(newSkills.isEmpty())){
					for(int i = 0; i < newSkills.size(); i++){
						Skill skill = newSkills.get(i);
						strings.add("ALTER TABLE Main_"+sc.getSQLName()+" ADD "+skill.getSkillType().getSQLName()+"_lvl Integer;");
						strings.add("ALTER TABLE Main_"+sc.getSQLName()+" ADD "+skill.getSkillType().getSQLName()+"_xp Double;");
					}
				}
			}
			if(strings.isEmpty()){
				return; 
			}
			String[] statements = new String[strings.size()];
			for(int i = 0; i < statements.length; i++){
				statements[i] = strings.get(i);
				Main.writeMessage(statements[i]);
			}
			DatabaseUtil.executeStatements(statements);
		}
	}

	public static void writeMessage(String message){
		Bukkit.getLogger().info("[Main][Info] - "+message);
	}

	public static void writeError(String message){
		Bukkit.getLogger().info("[Main][Error] - "+message);
	}

	private void registerListeners(){
		// Adds Archery Skill
		Archery a = new Archery("Archery", "Archery");
		this.getServer().getPluginManager().registerEvents(a, this);
		Skill.addSkill(a);
		// Adds Unarmed Skill
		Unarmed u = new Unarmed("Unarmed", "Unarmed");
		this.getServer().getPluginManager().registerEvents(u, this);
		Skill.addSkill(u);
		// Adds Acrobatics Skill
		Acrobatics acro = new Acrobatics("Acrobatics", "Acrobat");
		this.getServer().getPluginManager().registerEvents(acro, this);
		Skill.addSkill(acro);
		// Adds Mining Skill
		Mining m = new Mining("Mining", "Mining");
		this.getServer().getPluginManager().registerEvents(m, this);
		Skill.addSkill(m);
		// Adds WoodCutting Skill
		WoodCutting c = new WoodCutting("WoodCutting", "WoodCut");
		this.getServer().getPluginManager().registerEvents(c, this);
		Skill.addSkill(c);
		// Adds Digging Skill
		Digging d = new Digging("Digging", "Digging");
		this.getServer().getPluginManager().registerEvents(d, this);
		Skill.addSkill(d);
		// Adds Weaponry Skill
		Weaponry w = new Weaponry("Weaponry", "Weaponry");
		this.getServer().getPluginManager().registerEvents(w, this);
		Skill.addSkill(w);
		// Adds other listeners
		this.getServer().getPluginManager().registerEvents(new SkillsListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
	}
	
	private void registerCommands(){
		this.getCommand("VSwitch").setExecutor(new CommandSwitch());
		this.getCommand("VList").setExecutor(new CommandSkillClassList());
	}

	private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }
	
	public static void depositPlayer(Player player, double val){
		OfflinePlayer p = (OfflinePlayer) player;
		if(!econ.hasAccount(p)){
			econ.createPlayerAccount(p);
		}else{
			econ.depositPlayer(p, (val*moneymultiplier));
		}
	}

	public static String getLineWithText(String text){
		int length = 54 - text.length();
		String line = ChatColor.GREEN+"";
		for(int i = 0; i < (length/2); i++){
			line += "#";
		}
		line += " "+ChatColor.AQUA+text+ChatColor.GREEN+" ";
		for(int i = 0; i < (length/2); i++){
			line += "#";
		}
		if(length % 2 == 1)
			line += "#";
		return line;
	}
	
	public static String getLine(){
		int length = 53;
		String line = ChatColor.GREEN+"";
		for(int i = 0; i < length; i++){
			line += "#";
		}
		return line;
	}
}
