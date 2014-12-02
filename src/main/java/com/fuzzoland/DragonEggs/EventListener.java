package com.fuzzoland.DragonEggs;

import net.milkbowl.vault.economy.EconomyResponse;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class EventListener implements Listener{

	private Main plugin;
	
	public EventListener(Main plugin){
		this.plugin = plugin;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event){ 
		Player player = event.getPlayer();
		if(event.getAction() == Action.RIGHT_CLICK_BLOCK){
			Block block = event.getClickedBlock();
			if(block.getType() == Material.DRAGON_EGG){
				if(player.getItemInHand().getType() == Material.EYE_OF_ENDER && player.hasPermission("DragonEggs.Summon")){
					Boolean wrongworld = false;
					switch(player.getWorld().getEnvironment()){
					case NORMAL:
						if(plugin.getConfig().getBoolean("DisableDragonSpawn.NormalEnv")){
							wrongworld = true;
						}
						break;
					case NETHER:
						if(plugin.getConfig().getBoolean("DisableDragonSpawn.NetherEnv")){
							wrongworld = true;
						}
						break;
					case THE_END:
						if(plugin.getConfig().getBoolean("DisableDragonSpawn.TheEndEnv")){
							wrongworld = true;
						}
						break;
					default:
						break;
					}
					if(wrongworld){
						player.sendMessage(plugin.getConfig().getString("Messages.WrongWorld").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
						return;
					}
					String worldn = player.getWorld().getName();
					for(String n : plugin.getConfig().getStringList("Dragon.Limit")){
						String[] parts = n.split(";");
						if(parts[0].equals(worldn)){
							Integer ec = 0;
							for(Entity e : player.getWorld().getEntities()){
								if(e instanceof EnderDragon){
									ec++;
								}
							}
							if(ec >= Integer.parseInt(parts[1])){
								player.sendMessage(plugin.getConfig().getString("Messages.SummonFail").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
								return;
							}
						}
					}
					ItemStack icost = null;
					if(plugin.getConfig().getBoolean("ItemCost.Enabled")){
						icost = new ItemStack(Material.getMaterial(plugin.getConfig().getString("ItemCost.Item")));
						if(!player.getInventory().containsAtLeast(icost, plugin.getConfig().getInt("ItemCost.Amount"))){
							player.sendMessage(plugin.getConfig().getString("Messages.SummonFail").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
							return;
						}
					}
					if(plugin.getConfig().getBoolean("EconomyCost.Enabled")){
						EconomyResponse ecost = plugin.econ.withdrawPlayer(player.getName(), plugin.getConfig().getDouble("EconomyCost.Cost"));
						if(!ecost.transactionSuccess()){
							player.sendMessage(plugin.getConfig().getString("Messages.SummonFail").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
							return;
						}
					}
					if(icost != null){
						for(int i = 0; i < plugin.getConfig().getInt("ItemCost.Amount"); i++){
							player.getInventory().removeItem(icost);
						}
						player.updateInventory();
					}
					block.setType(Material.AIR);
					player.getWorld().createExplosion(block.getLocation(), 0.0F);
					EnderDragon dragon = block.getWorld().spawn(block.getLocation().add(0.5, 0.5, 0.5), EnderDragon.class);
					dragon.setHealth(plugin.getConfig().getInt("Dragon.Health"));
					player.sendMessage(plugin.getConfig().getString("Messages.SummonSuccess").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
				}else{
					player.sendMessage(plugin.getConfig().getString("Messages.SummonFail").replaceAll("(&([a-f0-9l-or]))", "\u00A7$2"));
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityCreatePortal(EntityCreatePortalEvent event){
		if(plugin.getConfig().getBoolean("Dragon.DeathHandling.EnableDeathHandling")){
			if(!plugin.getConfig().getBoolean("Dragon.DeathHandling.EnableDeathHandling.CreatePortal")){
				if(PortalType.ENDER != null){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		if(plugin.getConfig().getBoolean("Dragon.DeathHandling.EnableDeathHandling")){
			if(plugin.getConfig().getBoolean("Dragon.DeathHandling.EnableDeathHandling.DropEgg")){
				if(event.getEntity() instanceof EnderDragon){
					EnderDragon dragon = (EnderDragon) event.getEntity();
					Location loc = dragon.getLocation();
					World world = dragon.getWorld();
					ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
					world.dropItemNaturally(loc, dragonEgg);
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		if(event.getEntity() instanceof EnderDragon){
			Boolean disable = false;
			switch(event.getEntity().getWorld().getEnvironment()){
			case NORMAL:
				if(plugin.getConfig().getBoolean("DisableDragonBlockDamage.NormalEnv")){
					disable = true;
				}
				break;
			case NETHER:
				if(plugin.getConfig().getBoolean("DisableDragonBlockDamage.NetherEnv")){
					disable = true;
				}
				break;
			case THE_END:
				if(plugin.getConfig().getBoolean("DisableDragonBlockDamage.TheEndEnv")){
					disable = true;
				}
				break;
			default:
				break;
			}
			if(disable){
				event.setCancelled(true);
			}
		}
	}
}
