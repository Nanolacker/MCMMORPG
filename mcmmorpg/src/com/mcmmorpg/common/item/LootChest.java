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
import com.mcmmorpg.common.utils.CardinalDirection;

/**
 * Place chests that player characters can loot.
 */
public class LootChest {

	private static final int MAX_SPAWN_RADIUS = 5;
	private static final double PARTICLE_TASK_PERIOD_SECONDS = 1.5;
	private static final int PARTICLE_COUNT = 3;

	private static final Map<Location, LootChest> chestMap = new HashMap<>();
	static List<Inventory> inventories = new ArrayList<>();

	private final Location location;
	private final CardinalDirection direction;
	private final double respawnTime;
	private final Item[] contents;
	private final TextPanel text;
	private boolean removed;

	private LootChest(Location location, CardinalDirection direction, double respawnTime, Item[] contents) {
		this.location = getNearestEmptyBlock(location);
		this.direction = direction;
		this.respawnTime = respawnTime;
		this.contents = contents;
		this.removed = false;
		this.text = new TextPanel(this.location.clone().add(0.5, 1, 0.5), ChatColor.GOLD + "Loot Chest");
		text.setVisible(true);
		Block block = this.location.getBlock();
		block.setType(Material.CHEST);
		BlockData blockData = block.getBlockData();
		((Directional) blockData).setFacing(blockFaceForCardinalDirection(direction));
		block.setBlockData(blockData);
		chestMap.put(this.location, this);
	}

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
	 * Called when the plugin is disabled.
	 */
	public static void removeAll() {
		List<LootChest> chests = new ArrayList<>(chestMap.values());
		for (LootChest chest : chests) {
			chest.remove();
		}
	}

	/**
	 * Returns the loot chest at the specified location.
	 */
	static LootChest forLocation(Location location) {
		return chestMap.get(location);
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

	/**
	 * Returns the location of this loot chest.
	 */
	public Location getLocation() {
		return location;
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

	private void respawn() {
		new DelayedTask(respawnTime) {
			@Override
			protected void run() {
				spawnLootChest(location, direction, respawnTime, contents);
			}
		}.schedule();
	}

	/**
	 * Removes this loot chest from its location.
	 */
	public void remove() {
		if (removed) {
			return;
		}
		chestMap.remove(location);
		location.getBlock().setType(Material.AIR);
		text.setVisible(false);
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
			world.spawnParticle(Particle.VILLAGER_HAPPY, particleLocation, PARTICLE_COUNT);
		}
	}

}
