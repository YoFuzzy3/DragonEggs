package com.fuzzoland.DragonEggs;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class CommandDragonEggs implements CommandExecutor{

	private Main plugin;
	
	public CommandDragonEggs(Main plugin){
		this.plugin = plugin;
	}
	
	private Logger logger = Bukkit.getLogger();
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("DragonEggs")){
			if(sender.hasPermission("DragonEggs.Admin")){
				if(args.length == 0){
					sender.sendMessage(ChatColor.GOLD + "/DragonEggs reload - Reload the configuration file");
					sender.sendMessage(ChatColor.DARK_GREEN + "/DragonEggs reset - Reset the configuration file");
					sender.sendMessage(ChatColor.GOLD + "/DragonEggs kill <radius> - Kill all dragons nearby");
				}else if(args.length >= 1){
					if(args[0].equalsIgnoreCase("reload")){
						if(args.length == 1){
							plugin.reloadConfig();
							if(plugin.getConfig().getBoolean("DragonEggRecipe.Enabled") == true){
								plugin.setupRecipe();
								logger.log(Level.INFO, "[DragonEggs] Crafting recipe loaded!");
							}
							if(plugin.getConfig().getBoolean("EconomyCost.Enabled")){
								plugin.setupEconomy();
							}else{
								logger.log(Level.INFO, "[DragonEggs] You have chosen to disable economy support.");
							}
							sender.sendMessage(ChatColor.GREEN + "The configuration file was successfully reloaded!");	
						}else{
							sender.sendMessage(ChatColor.RED + "/DragonEggs reload");	
						}
					}else if(args[0].equalsIgnoreCase("reset")){
						if(args.length == 1){
							for(String key : plugin.getConfig().getKeys(false)){
								plugin.getConfig().set(key, null);
							}
							plugin.getConfig().options().copyDefaults(true);
							plugin.saveConfig();
							plugin.reloadConfig();
							sender.sendMessage(ChatColor.GREEN + "The configuration file was successfully reset!");
						}else{
							sender.sendMessage(ChatColor.RED + "/DragonEggs reset");
						}
					}else if(args[0].equalsIgnoreCase("kill")){
						if(args.length == 2){
							if(sender instanceof Player){
								try{
									Integer.parseInt(args[1]);
								}catch(NumberFormatException e){
									sender.sendMessage(ChatColor.RED + "The radius must be an integer");
									return true;
								}
								Integer radius = Integer.parseInt(args[1]);
								for(Entity entity : ((Player) sender).getNearbyEntities(radius, radius, radius)){
									if(entity instanceof EnderDragon){
										entity.remove();
									}
								}
								sender.sendMessage(ChatColor.GREEN + "Killed all dragons within " + String.valueOf(radius) + " blocks.");
							}else{
								sender.sendMessage(ChatColor.RED + "Only players can use this command.");
							}
						}else{
							sender.sendMessage(ChatColor.RED + "/DragonEggs kill <radius>");
						}
					}else{
						sender.sendMessage(ChatColor.RED + "Type /DragonEggs for help.");
					}
				}else{
					sender.sendMessage(ChatColor.RED + "Type /DragonEggs for help.");
				}
			}else{
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command.");
			}
		}
		return true;
	}
}
