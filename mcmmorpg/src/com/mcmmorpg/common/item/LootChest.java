package com.mcmmorpg.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;

public class LootChest {

	private static final int MAX_SPAWN_RADIUS = 5;
	private static final double PARTICLE_TASK_PERIOD_SECONDS = 1.5;
	private static final int PARTICLE_COUNT = 3;

	private static final Map<Location, LootChest> chestMap = new HashMap<>();
	static List<Inventory> inventories = new ArrayList<>();

	/**
	 * Called when the plugin is enabled.
	 */
	public static void init() {
		RepeatingTask particleTask = new RepeatingTask(PARTICLE_TASK_PERIOD_SECONDS) {
			@Override
			protected void run() {
				Collection<LootChest> chests = chestMap.values();
				for (LootChest chest : chests) {
					chest.spawnParticles();
				}
			}
		};
		particleTask.schedule();
	}

	/**
	 * Called when the plugin is disabled.
	 */
	public static void removeAll() {
		List<LootChest> chests = new ArrayList<>(chestMap.values());
		for (LootChest chest : chests) {
			chest.remove();
		}
	}

	private final Location location;
	private final Particle aura;
	private final ItemStack[] contents;
	private boolean removed;

	/**
	 * The default loot chest.
	 */
	public LootChest(Location location, ItemStack[] contents) {
		this(location, Material.CHEST, Particle.VILLAGER_HAPPY, 30, contents);
	}

	public LootChest(Location location, Material material, Particle aura, double lifetimeSeconds,
			ItemStack[] contents) {
		this.location = getNearestEmptyBlock(location);
		this.aura = aura;
		this.contents = contents;
		if (location == null) {
			return;
		}
		this.removed = false;
		Block block = this.location.getBlock();
		block.setType(material);
		chestMap.put(this.location, this);
		new DelayedTask(lifetimeSeconds) {
			@Override
			protected void run() {
				LootChest.this.remove();
			}
		}.schedule();
	}

	private Location getNearestEmptyBlock(Location location) {
		World world = location.getWorld();
		int baseX = location.getBlockX();
		int baseY = location.getBlockY();
		int baseZ = location.getBlockZ();
		for (int radius = 0; radius <= MAX_SPAWN_RADIUS; radius++) {
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
		}
		return null;
	}

	void open(PlayerCharacter pc) {
		Inventory inventory = Bukkit.createInventory(null, 9, "Loot");
		inventory.setContents(contents);
		pc.getPlayer().openInventory(inventory);
		inventories.add(inventory);
	}

	public void remove() {
		if (removed) {
			return;
		}
		chestMap.remove(location);
		location.getBlock().setType(Material.AIR);
		removed = true;
	}

	private void spawnParticles() {
		World world = location.getWorld();
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			double offsetX = Math.random();
			double offsetY = Math.random() + 0.5;
			double offsetZ = Math.random();
			Vector offset = new Vector(offsetX, offsetY, offsetZ);
			Location particleLocation = location.clone().add(offset);
			world.spawnParticle(aura, particleLocation, PARTICLE_COUNT);
		}
	}

	static LootChest forLocation(Location location) {
		return chestMap.get(location);
	}

}
