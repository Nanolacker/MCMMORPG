package com.mcmmorpg.common.util;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BukkitUtility {

	/**
	 * Preferred method of spawning Bukkit entities, as this method prevents entity
	 * persistence errors from occurring.
	 **/
	@SuppressWarnings("deprecation")
	public static Entity spawnNonpersistentEntity(Location location, EntityType entityType) {
		Entity entity = location.getWorld().spawnEntity(location, entityType);
		entity.setPersistent(false);
		return entity;
	}

	/**
	 * Returns the location of the nearest block of type air to the specified
	 * location.
	 **/
	public static Location nearestEmptyBlockLocation(Location location) {
		World world = location.getWorld();
		int baseX = location.getBlockX();
		int baseY = location.getBlockY();
		int baseZ = location.getBlockZ();
		int radius = 0;
		while (true) {
			for (int x = -radius; x <= radius; x++) {
				for (int y = -radius; y <= radius; y++) {
					for (int z = -radius; z <= radius; z++) {
						Block block = world.getBlockAt(baseX + x, baseY + y, baseZ + z);
						if (block.getType() == Material.AIR) {
							return block.getLocation();
						}
					}
				}
			}
			radius++;
		}
	}

	/**
	 * Convenience method for creating item stacks.
	 */
	public static ItemStack createItemStack(String name, String lore, Material material) {
		List<String> loreAsList = StringUtility.lineSplit(lore);
		return createItemStack0(name, loreAsList, material);
	}

	/**
	 * Convenience method for creating item stacks.
	 */
	public static ItemStack createItemStack0(String name, List<String> lore, Material material) {
		ItemStack itemStack = new ItemStack(material);
		ItemMeta itemMeta = itemStack.getItemMeta();
		itemMeta.setDisplayName(name);
		if (lore != null) {
			itemMeta.setLore(lore);
		}
		itemMeta.setUnbreakable(true);
		itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_ENCHANTS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		itemStack.setItemMeta(itemMeta);
		return itemStack;
	}

	public static boolean inventoryIsEmpty(Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		for (ItemStack itemStack : contents) {
			if (itemStack != null) {
				return false;
			}
		}
		return true;
	}
	
}
