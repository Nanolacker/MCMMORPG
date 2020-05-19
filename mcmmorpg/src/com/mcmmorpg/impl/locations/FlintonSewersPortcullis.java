package com.mcmmorpg.impl.locations;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.TextPanel;

public class FlintonSewersPortcullis {

	private static final int WIDTH = 11, HEIGHT = 5;
	private static final double SHIFT_PERIOD = 1;
	private static final double OPEN_DURATION = 5;
	private static final Material GATE_MATERIAL = Material.IRON_BARS;
	private static final Noise SHIFT_NOISE = new Noise(Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR);

	private final Location location;
	private final Block[][] blocks;
	private boolean closed;

	public FlintonSewersPortcullis(Location location, boolean northFacing, Item key) {
		this.location = location;
		blocks = new Block[HEIGHT][WIDTH];
		double sizeX;
		double sizeZ;
		if (northFacing) {
			sizeX = WIDTH;
			sizeZ = 1;
		} else {
			sizeX = 1;
			sizeZ = WIDTH;
		}
		Location blockLocation = location.clone();
		for (int i = 0; i < HEIGHT; i++) {
			if (northFacing) {
				blockLocation.setX(location.getX() - WIDTH / 2.0);
			} else {
				blockLocation.setZ(location.getZ() - WIDTH / 2.0);
			}
			for (int j = 0; j < WIDTH; j++) {
				Block block = blockLocation.getBlock();
				blocks[i][j] = block;
				if (northFacing) {
					blockLocation.setX(blockLocation.getX() + 1);
				} else {
					blockLocation.setZ(blockLocation.getZ() + 1);
				}
			}
			blockLocation.setY(blockLocation.getY() + 1);
		}

		PlayerCharacterInteractionCollider interactionCollider = new PlayerCharacterInteractionCollider(
				location.clone().add(0, HEIGHT / 2.0, 0), sizeX, HEIGHT, sizeZ) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				if (pc.getItemCount(key) >= 1) {
					if (closed) {
						pc.sendMessage(ChatColor.GRAY + "Opened using " + key.formatName());
						open();
					}
				} else {
					pc.sendMessage(ChatColor.GRAY + "Requires " + key.formatName() + ChatColor.GRAY + " to open");
				}
			}
		};
		interactionCollider.setActive(true);
		interactionCollider.setVisible(true);

		TextPanel text = new TextPanel(location.clone().add(0, HEIGHT / 2.0, 0),
				ChatColor.GRAY + "Requires " + key.formatName() + ChatColor.GRAY + " to open");
		text.setVisible(true);

		this.closed = true;
	}

	private void open() {
		closed = false;
		for (int i = 0; i < HEIGHT; i++) {
			Block[] row = blocks[i];
			DelayedTask shift = new DelayedTask(i * SHIFT_PERIOD) {
				@Override
				protected void run() {
					for (int j = 0; j < WIDTH; j++) {
						SHIFT_NOISE.play(location);
						Block block = row[j];
						if (block.getType() == GATE_MATERIAL) {
							block.setType(Material.AIR);
						}
					}
				}
			};
			shift.schedule();
		}
		DelayedTask close = new DelayedTask(OPEN_DURATION + HEIGHT * SHIFT_PERIOD) {
			@Override
			protected void run() {
				close();
			}
		};
		close.schedule();
	}

	private void close() {
		closed = true;
		for (int i = 0; i < HEIGHT; i++) {
			Block[] row = blocks[HEIGHT - 1 - i];
			DelayedTask shift = new DelayedTask(SHIFT_PERIOD + i * SHIFT_PERIOD) {
				@Override
				protected void run() {
					for (int j = 0; j < WIDTH; j++) {
						SHIFT_NOISE.play(location);
						Block block = row[j];
						if (block.getType() == Material.AIR) {
							block.setType(GATE_MATERIAL);
						}
					}
				}
			};
			shift.schedule();
		}
	}

}
