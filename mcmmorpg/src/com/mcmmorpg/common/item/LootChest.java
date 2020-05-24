package com.mcmmorpg.common.item;

import java.util.ArrayList;
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
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.utils.BukkitUtils;
import com.mcmmorpg.common.utils.CardinalDirection;

/**
 * Place chests that player characters can loot.
 */
public class LootChest {

	private static final double UPDATE_PERIOD_SECONDS = 1.0;
	private static final double SPAWN_RADIUS = 40;
	private static final double DESPAWN_RADIUS = 45;
	private static final int PARTICLE_COUNT = 3;

	private static final List<LootChest> lootChests = new ArrayList<>();
	private static final Map<Location, LootChest> spawnedChestMap = new HashMap<>();
	static final List<Inventory> inventories = new ArrayList<>();

	private final Location targetLocation;
	private final CardinalDirection direction;
	private final double respawnTime;
	private final Item[] contents;
	private final TextPanel text;
	private Location trueLocation;
	private boolean isSpawned;

	private LootChest(Location location, CardinalDirection direction, double respawnTime, Item[] contents) {
		this.targetLocation = BukkitUtils.nearestEmptyBlockLocation(location);
		this.direction = direction;
		this.respawnTime = respawnTime;
		this.contents = contents;
		this.text = new TextPanel(location, ChatColor.GOLD + "Loot Chest");
		isSpawned = false;
		lootChests.add(this);
	}

	/**
	 * Called when the plugin is enabled.
	 */
	public static void init() {
		RepeatingTask update = new RepeatingTask(UPDATE_PERIOD_SECONDS) {
			@Override
			public void run() {
				for (int i = 0; i < lootChests.size(); i++) {
					LootChest lootChest = lootChests.get(i);
					if (lootChest.isSpawned) {
						if (lootChest.shouldDespawn()) {
							lootChest.despawn();
						} else {
							lootChest.spawnParticles();
						}
					} else {
						if (lootChest.shouldSpawn()) {
							lootChest.spawn();
						}
					}
				}
			}
		};
		update.schedule();
	}

	public static LootChest spawnLootChest(Location location, CardinalDirection direction, double respawnTime,
			Item... contents) {
		return new LootChest(location, direction, respawnTime, contents);
	}

	/**
	 * Create a loot chest at the specified location with the specified items
	 * contents.
	 */
	public static LootChest spawnLootChest(Location location, CardinalDirection direction, Item... contents) {
		return new LootChest(location, direction, 0, contents);
	}

	/**
	 * Create a loot chest at the specified location with the specified items
	 * contents.
	 */
	public static LootChest spawnLootChest(Location location, Item... contents) {
		return new LootChest(location, CardinalDirection.NORTH, 0, contents);
	}

	private static BlockFace blockFaceForCardinalDirection(CardinalDirection direction) {
		switch (direction) {
		case EAST:
			return BlockFace.EAST;
		case NORTH:
			return BlockFace.NORTH;
		case SOUTH:
			return BlockFace.SOUTH;
		case WEST:
			return BlockFace.WEST;
		default:
			throw new IllegalArgumentException("Invalid direction");
		}
	}

	/**
	 * Returns the loot chest at the specified location.
	 */
	static LootChest forLocation(Location location) {
		return spawnedChestMap.get(location);
	}

	/**
	 * Returns the location of this loot chest.
	 */
	public Location getLocation() {
		return targetLocation;
	}

	/**
	 * Returns the item contents of this loot chest.
	 */
	public Item[] getContents() {
		return contents;
	}

	void open(PlayerCharacter pc) {
		Inventory inventory = Bukkit.createInventory(null, 27, "Loot Chest");
		ArrayList<ItemStack> itemStacks = new ArrayList<>(27);
		for (int i = 0; i < contents.length; i++) {
			ItemStack itemStack = contents[i].getItemStack();
			itemStacks.add(itemStack);
		}
		for (int i = contents.length; i < 27; i++) {
			itemStacks.add(null);
		}
		Collections.shuffle(itemStacks);
		inventory.setContents(itemStacks.toArray(new ItemStack[itemStacks.size()]));
		pc.getPlayer().openInventory(inventory);
		inventories.add(inventory);
		if (respawnTime > 0) {
			respawn();
		}
	}

	private boolean shouldSpawn() {
		return PlayerCharacter.playerCharacterIsNearby(targetLocation, SPAWN_RADIUS);
	}

	private boolean shouldDespawn() {
		return !PlayerCharacter.playerCharacterIsNearby(targetLocation, DESPAWN_RADIUS);
	}

	private void spawn() {
		isSpawned = true;
		trueLocation = BukkitUtils.nearestEmptyBlockLocation(targetLocation);
		text.setLocation(trueLocation.clone().add(0.5, 1, 0.5));
		text.setVisible(true);
		Block block = trueLocation.getBlock();
		block.setType(Material.CHEST);
		BlockData blockData = block.getBlockData();
		((Directional) blockData).setFacing(blockFaceForCardinalDirection(direction));
		block.setBlockData(blockData);
		spawnedChestMap.put(trueLocation, this);
	}

	private void despawn() {
		isSpawned = false;
		spawnedChestMap.remove(trueLocation);
		text.setVisible(false);
		trueLocation.getBlock().setType(Material.AIR);
	}

	private void respawn() {
		new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				spawnLootChest(targetLocation, direction, respawnTime, contents);
			}
		}.schedule();
	}

	private void spawnParticles() {
		World world = trueLocation.getWorld();
		for (int i = 0; i < PARTICLE_COUNT; i++) {
			double offsetX = Math.random();
			double offsetY = Math.random() + 0.5;
			double offsetZ = Math.random();
			Vector offset = new Vector(offsetX, offsetY, offsetZ);
			Location particleLocation = trueLocation.clone().add(offset);
			world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, PARTICLE_COUNT);
		}
	}

	/**
	 * Removes this loot chest from its location.
	 */
	public void remove() {
		lootChests.remove(this);
		if (isSpawned) {
			spawnedChestMap.remove(trueLocation);
			trueLocation.getBlock().setType(Material.AIR);
			text.setVisible(false);
		}
	}

	/**
	 * Called when the plugin is disabled.
	 */
	public static void removeAll() {
		for (int i = 0; i < lootChests.size(); i++) {
			LootChest lootChest = lootChests.get(i);
			lootChest.remove();
		}
	}

}
