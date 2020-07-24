package com.shibatsu.RandomizingEye;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Main extends JavaPlugin implements Listener {
	public Set<Material> allMaterials = new HashSet<Material>();
	public Set<Material> materialsBlackList = new HashSet<Material>();
	public Set<String> activePlayers = new HashSet<String>();
	public Map<String,Block> lastBlocks = new HashMap<String,Block>();
	Random rand = new Random();
	
	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getCommand("RandomizingEye").setTabCompleter(new Tab());
		materialsBlackList.add(Material.END_PORTAL);
		materialsBlackList.add(Material.END_PORTAL_FRAME);
		materialsBlackList.add(Material.NETHER_PORTAL);
		materialsBlackList.add(Material.END_CRYSTAL);
		materialsBlackList.add(Material.AIR);
		materialsBlackList.add(Material.BEDROCK);
		materialsBlackList.add(Material.END_GATEWAY);
		materialsBlackList.add(Material.OBSIDIAN);
		
		for(Material material : Material.values()) {
			if(!materialsBlackList.contains(material) && material.isBlock() && !material.isLegacy()) {
				allMaterials.add(material);
			}
		}
	}
	
	@Override
	public void onDisable() {

	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("RandomizingEye")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("[RandomizingEye] " + "Sorry! This command is players only!");
				return false;
			}
			if (args.length == 0) {
				return false;
			}
			if (args[0].equalsIgnoreCase("start")) {
				if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(!activePlayers.contains(player.getName())) {
							activePlayers.add(player.getName());
							player.sendMessage("[RandomizingEye] " + "You have recieved power of Randomizing Eye!");
						}
					}
					return true;
				}
				if(activePlayers.contains(sender.getName())) {
					sender.sendMessage("[RandomizingEye] " + "You already have power of Randomizing Eye!");
					return false;
				}
				else {
					activePlayers.add(sender.getName());
					sender.sendMessage("[RandomizingEye] " + "You have recieved power of Randomizing Eye!");
					return true;
				}
			}
			if (args[0].equalsIgnoreCase("stop")) {
				if (args.length == 2 && args[1].equalsIgnoreCase("all")) {
					for(Player player : Bukkit.getOnlinePlayers()) {
						if(activePlayers.contains(player.getName())) {
							activePlayers.remove(sender.getName());
							if(lastBlocks.containsKey(sender.getName())) {
								lastBlocks.remove(sender.getName());
							}
							sender.sendMessage("[RandomizingEye] " + "You have lost control on Randomizing Eye!");
							return true;
						}
					}
					return true;
				}
				if(activePlayers.contains(sender.getName())) {
					activePlayers.remove(sender.getName());
					if(lastBlocks.containsKey(sender.getName())) {
						lastBlocks.remove(sender.getName());
					}
					sender.sendMessage("[RandomizingEye] " + "You have lost control on Randomizing Eye!");
					return true;
				}
				else {
					sender.sendMessage("[RandomizingEye] " + "You cannot stop what, not started yet..");
					return false;
				}
			}
		}
		return false;
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if(!activePlayers.contains(event.getPlayer().getName())) {
			return;
		}
		if(event.getPlayer().rayTraceBlocks(100,FluidCollisionMode.SOURCE_ONLY) == null) {
			return;
		}
		if(!lastBlocks.containsKey(event.getPlayer().getName())) {
			lastBlocks.put(event.getPlayer().getName(), event.getPlayer().rayTraceBlocks(100,FluidCollisionMode.SOURCE_ONLY).getHitBlock());
		}
		Block hitBlock = event.getPlayer().rayTraceBlocks(100,FluidCollisionMode.SOURCE_ONLY).getHitBlock();
		if(!lastBlocks.get(event.getPlayer().getName()).equals(hitBlock)) {
			Block tempBlock = lastBlocks.get(event.getPlayer().getName());
			lastBlocks.put(event.getPlayer().getName(), hitBlock);
			if(!tempBlock.isEmpty() && !materialsBlackList.contains(tempBlock.getType())) {
				tempBlock.setType((Material)allMaterials.toArray()[rand.nextInt(allMaterials.size())]);
			}
		}
	}
}