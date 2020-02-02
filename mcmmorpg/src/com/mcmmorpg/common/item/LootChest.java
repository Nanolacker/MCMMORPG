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
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.Debug;

public class LootChest {

	private static final double PARTICLE_TASK_PERIOD_SECONDS = 1;
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

	public LootChest(Location location, Material material, Particle aura, ItemStack[] contents) {
		this.location = location;
		this.aura = aura;
		this.contents = contents;
		Block block = location.getBlock();
		block.setType(material);
		chestMap.put(location, this);
	}

	void open(PlayerCharacter pc) {
		Inventory inventory = Bukkit.createInventory(null, 9, "Loot");
		inventory.setContents(contents);
		pc.getPlayer().openInventory(inventory);
		inventories.add(inventory);
	}

	public void remove() {
		chestMap.remove(location);
		location.getBlock().setType(Material.AIR);
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
