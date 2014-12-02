package com.fuzzoland.DragonEggs;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin{
	
	private Logger logger = Bukkit.getLogger();
	public Economy econ = null;
	
	public void onEnable(){		
		getConfig().options().copyDefaults(true);
		saveConfig();
		logger.log(Level.INFO, "[DragonEggs] Configuration file loaded!");
		getServer().getPluginManager().registerEvents(new EventListener(this), this);
		logger.log(Level.INFO, "[DragonEggs] Events registered!");
		getCommand("DragonEggs").setExecutor(new CommandDragonEggs(this));
        logger.log(Level.INFO, "[DragonEggs] Commands registered!");       
		if(getConfig().getBoolean("DragonEggRecipe.Enabled") == true){
			setupRecipe();
			logger.log(Level.INFO, "[DragonEggs] Crafting recipe loaded!");
		}
		try{
			MetricsLite metrics = new MetricsLite(this);
			metrics.start();
			logger.log(Level.INFO, "[DragonEggs] Metrics initiated!");
			}catch(IOException e){
				e.printStackTrace();
		}
		if(getConfig().getBoolean("EconomyCost.Enabled")){
			setupEconomy();
		}else{
			logger.log(Level.INFO, "[DragonEggs] You have chosen to disable economy support.");
		}
	}

    public boolean setupEconomy(){
    	logger.log(Level.INFO, "[DragonEggs] You have chosen to enable economy support.");
    	logger.log(Level.INFO, "[DragonEggs] Trying to enable economy support...");
    	if(getServer().getPluginManager().getPlugin("Vault") == null){
    		logger.log(Level.SEVERE, "[DragonEggs] Failed to enable economy support - Vault not found!");
    		getPluginLoader().disablePlugin(this);
    		return false;
    	}
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if(rsp == null){
        	logger.log(Level.SEVERE, "[DragonEggs] Failed to enable economy support - Economy plugin not found!");
        	getPluginLoader().disablePlugin(this);
            return false;
        }
        econ = rsp.getProvider();
        logger.log(Level.INFO, "[DragonEggs] Succesfully enabled economy support!");
    	return econ != null;
    }
    
    public void setupRecipe(){
		ShapedRecipe dragonEgg = new ShapedRecipe(new ItemStack(Material.DRAGON_EGG, 1)).shape("123", "456", "789");
		dragonEgg.setIngredient('1', Material.matchMaterial(getConfig().getString("DragonEggRecipe.TopLeft")));
		dragonEgg.setIngredient('2', Material.matchMaterial(getConfig().getString("DragonEggRecipe.TopMiddle")));
		dragonEgg.setIngredient('3', Material.matchMaterial(getConfig().getString("DragonEggRecipe.TopRight")));
		dragonEgg.setIngredient('4', Material.matchMaterial(getConfig().getString("DragonEggRecipe.MiddleLeft")));
		dragonEgg.setIngredient('5', Material.matchMaterial(getConfig().getString("DragonEggRecipe.MiddleMiddle")));
		dragonEgg.setIngredient('6', Material.matchMaterial(getConfig().getString("DragonEggRecipe.MiddleRight")));
		dragonEgg.setIngredient('7', Material.matchMaterial(getConfig().getString("DragonEggRecipe.BottomLeft")));
		dragonEgg.setIngredient('8', Material.matchMaterial(getConfig().getString("DragonEggRecipe.BottomMiddle")));
		dragonEgg.setIngredient('9', Material.matchMaterial(getConfig().getString("DragonEggRecipe.BottomRight")));
		getServer().addRecipe(dragonEgg);
    }
}
