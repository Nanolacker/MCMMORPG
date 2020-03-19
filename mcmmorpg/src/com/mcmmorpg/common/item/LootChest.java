package com.mcmmorpg.common.item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.mcmmorpg.common.ui.ProgressBar;

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

	private final Location location;
	private final int size;
	private final Particle aura;
	private final PlayerCharacter owner;
	private final Item[] contents;
	private boolean removed;
	private final ProgressBar lifetimeBar;

	private LootChest(Location location, String title, int size, Material material, Particle aura,
			double lifetimeSeconds, PlayerCharacter owner, Item[] contents) {
		this.location = getNearestEmptyBlock(location);
		this.size = size;
		this.aura = aura;
		// starting owner state
		this.owner = owner;
		this.contents = contents;
		this.removed = false;

		Block block = this.location.getBlock();
		block.setType(material);

		Location lifetimeBarLocation = this.location.clone().add(0.5, 1.1, 0.5);
		lifetimeBar = new ProgressBar(lifetimeBarLocation, title, 16, ChatColor.WHITE);
		lifetimeBar.setRate(1 / lifetimeSeconds);

		chestMap.put(this.location, this);
		new DelayedTask(lifetimeSeconds) {
			@Override
			protected void run() {
				LootChest.this.remove();
			}
		}.schedule();
	}

	/**
	 * The default loot chest.
	 */
	public static LootChest spawnLootChest(Location location, Item... contents) {
		return spawnLootChest(location, ChatColor.GOLD + "Loot Chest", 27, Material.CHEST, Particle.VILLAGER_HAPPY, 30,
				null, contents);
	}

	public static LootChest spawnLootChest(Location location, String title, int size, Material material, Particle aura,
			double lifetimeSeconds, PlayerCharacter owner, Item... contents) {
		return new LootChest(location, title, size, material, aura, lifetimeSeconds, owner, contents);
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

	private static Location getNearestEmptyBlock(Location location) {
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

	public Location getLocation() {
		return location;
	}

	public PlayerCharacter getOwner() {
		return owner;
	}

	public Item[] getContents() {
		return contents;
	}

	void open(PlayerCharacter pc) {
		Inventory inventory = Bukkit.createInventory(null, size, "Loot");
		ArrayList<ItemStack> itemStacks = new ArrayList<>(size);
		for (int i = 0; i < contents.length; i++) {
			ItemStack itemStack = contents[i].getItemStack();
			itemStacks.add(itemStack);
		}
		for (int i = contents.length; i < size; i++) {
			itemStacks.add(null);
		}
		Collections.shuffle(itemStacks);
		inventory.setContents(itemStacks.toArray(new ItemStack[itemStacks.size()]));
		pc.getPlayer().openInventory(inventory);
		inventories.add(inventory);
	}

	public void remove() {
		if (removed) {
			return;
		}
		chestMap.remove(location);
		location.getBlock().setType(Material.AIR);
		lifetimeBar.dispose();
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
