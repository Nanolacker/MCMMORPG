package com.mcmmorpg.common.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class BukkitUtils {

	@SuppressWarnings("deprecation")
	public static Entity spawnNonpersistentEntity(Location location, EntityType entityType) {
		Entity entity = location.getWorld().spawnEntity(location, entityType);
		entity.setPersistent(false);
		return entity;
	}

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
}
