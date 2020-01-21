package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.StringUtils;

import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.Vec3D;
import net.minecraft.server.v1_14_R1.World;

public class TextArea {

	/**
	 * The maximum distance from a {@code Player} that a {@code TextPanel} can be
	 * before its associated entities despawn.
	 */
	private static final double MAX_SPAWN_DISTANCE_FROM_PLAYER = 50.0;
	/**
	 * The distance between two consecutive lines in a multi-lined
	 * {@code TextPanel}.
	 */
	private static final double LINE_SEPEARATION_DISTANCE = 0.25;
	/**
	 * The period of each {@code TextPanel}'s {@code spawnManageTask}.
	 */
	private static final double SPAWN_MANAGE_TASK_PERIOD = 1.0;
	/**
	 * The maximum allowed offset between a {@code TextPanel}'s location and the
	 * locations of its entities. Sometimes the entities become caught on blocks and
	 * must be freed once they are detected as being more than this distance from
	 * where they should be.
	 */
	private static final double ALLOWED_ENTITY_LOC_OFFSET = 0.1;

	/**
	 * Stores all {@code TextPanels} that are instantiated so that the entities
	 * associated with them can be removed when the server is stopped.
	 */
	private static List<TextArea> activeTextAreas = new ArrayList<>();

	/**
	 * Whether this {@code TextPanel} is visible and rendering text.
	 */
	private boolean visible;
	/**
	 * The maximum number of characters that will be rendered on this
	 * {@code TextPanel} per line before a new line is started.
	 */
	private int charsPerLine;
	/**
	 * The text rendered by this {@code TextPanel}.
	 */
	private String text;
	/**
	 * The text that has been formatted to be rendered across multiple lines.
	 */
	private List<String> formattedText;
	/**
	 * this {@code TextPanel}'s {@code Location}
	 */
	private Location location;
	/**
	 * the Minecraft entities used to display this {@code TextPanel}
	 */
	private List<EntityArmorStand> entities;
	/**
	 * A {@code RepeatingTask} which manages spawning and despawning the entities
	 * associated with this {@code TextPanel}.
	 */
	private RepeatingTask entitySpawnManageTask;
	/**
	 * Whether the entities associate with this {@code TextPanel} are spawned.
	 */
	private boolean spawned;

	/**
	 * Constructs a new {@code TextPanel}. Be sure to invoke {@code setVisible} to
	 * make it visible. If this is not done, the text will be invisible.
	 * 
	 * @param text     the text rendered by this {@code TextPanel}
	 * @param location this {@code TextPanel}'s {@code Location}
	 */
	public TextArea(Location location, String text) {
		visible = false;
		this.charsPerLine = StringUtils.STANDARD_LINE_LENGTH;
		setText(text);
		this.location = location;
		entities = new ArrayList<>();
		spawned = false;
		initSpawnManageTask();
	}

	/**
	 * Constructs a new {@code TextPanel} whose text value defaults to {@code ""}.
	 * Be sure to invoke {@code setVisible} to make it visible. If this is not done,
	 * the text will be invisible.
	 * 
	 * @param location this {@code TextPanel}'s {@code Location}
	 */
	public TextArea(Location location) {
		this(location, "");
	}

	/**
	 * Removes all entities associated with any {@code TextPanel}s to keep worlds
	 * clean.
	 * <p>
	 * <b>Only to be invoked once during runtime, when the server is stopping.</b>
	 */
	public static void removeAllEntities() {
		for (int i = 0; i < activeTextAreas.size(); i++) {
			TextArea textarea = activeTextAreas.get(i);
			textarea.setVisible(false);
		}
	}

	/**
	 * Initializes the {@code RepeatingTask} used to manage spawning entities
	 * associated with this {@code TextPanel}.
	 */
	private void initSpawnManageTask() {
		entitySpawnManageTask = new RepeatingTask(SPAWN_MANAGE_TASK_PERIOD) {
			@Override
			protected void run() {
				boolean playerNearby = PlayerCharacter.playerIsNearby(location, MAX_SPAWN_DISTANCE_FROM_PLAYER,
						MAX_SPAWN_DISTANCE_FROM_PLAYER, MAX_SPAWN_DISTANCE_FROM_PLAYER);
				if (playerNearby) {
					if (spawned) {
						Location idealEntityLoc = getLocation().clone();
						for (int i = 0; i < entities.size(); i++) {
							EntityArmorStand entity = entities.get(i);
							double offsetX = idealEntityLoc.getX() - entity.locX;
							double offsetY = idealEntityLoc.getY() - entity.locY;
							double offsetZ = idealEntityLoc.getZ() - entity.locZ;
							if (offsetX > ALLOWED_ENTITY_LOC_OFFSET || offsetY > ALLOWED_ENTITY_LOC_OFFSET
									|| offsetZ > ALLOWED_ENTITY_LOC_OFFSET) {
								removeSingleEntity(i);
								spawnSingleEntity(i);
							}
							idealEntityLoc.subtract(0, LINE_SEPEARATION_DISTANCE, 0);
						}
					} else if (!text.isEmpty()) {
						spawnEntities();
					}
				} else if (!playerNearby && spawned) {
					removeEntities();
				}
			}
		};
	}

	/**
	 * Returns whether this {@code TextPanel} is visible and rendering text.
	 */
	public boolean getVisible() {
		return visible;
	}

	/**
	 * Sets whether this {@code TextPanel} is visible and rendering text.
	 */
	public void setVisible(boolean visible) {
		boolean redundant = this.visible == visible;
		if (redundant) {
			return;
		}
		this.visible = visible;
		if (visible) {
			activeTextAreas.add(this);
			entitySpawnManageTask.schedule();
		} else {
			activeTextAreas.remove(this);
			removeEntities();
			entitySpawnManageTask.cancel();
		}
	}

	/**
	 * Returns the text rendered by this {@code TextPanel}.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the text rendered by this {@code TextPanel}.
	 */
	public void setText(String text) {
		this.text = text;
		formattedText = StringUtils.lineSplit(text, charsPerLine);
		if (visible) {
			removeEntities();
			spawnEntities();
		}
	}

	public int getCharactersPerLine() {
		return charsPerLine;
	}

	public void setCharactersPerLine(int charactersPerLine) {
		charsPerLine = charactersPerLine;
		formattedText = StringUtils.lineSplit(text, charsPerLine);
		if (visible) {
			removeEntities();
			spawnEntities();
		}
	}

	/**
	 * Returns this {@code TextPanel}'s {@code Location}.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Sets this {@code TextPanel}'s {@code Location}.
	 */
	public void setLocation(Location location) {
		Location temp = this.location;
		this.location = location;
		if (visible) {
			Vector movement = location.toVector().subtract(temp.toVector());
			Vec3D vec3d = new Vec3D(movement.getX(), movement.getY(), movement.getZ());
			for (int i = 0; i < entities.size(); i++) {
				EntityArmorStand entity = entities.get(i);
				entity.move(null, vec3d);
			}
		}
	}

	/**
	 * Spawns all of the entities entities necessary to display text.
	 */
	private void spawnEntities() {
		int numLines = formattedText.size();
		for (int lineCount = 0; lineCount < numLines; lineCount++) {
			spawnSingleEntity(lineCount);
		}
		spawned = true;
	}

	/**
	 * Spawns a single entity to represent a single line of this {@code TextPanel}.
	 * 
	 * @param lineCount the number of line that the entity will represent
	 */
	private void spawnSingleEntity(int lineCount) {
		World world = ((CraftWorld) location.getWorld()).getHandle();
		EntityArmorStand entity = new EntityArmorStand(world, location.getX(),
				location.getY() - lineCount * LINE_SEPEARATION_DISTANCE, location.getZ());
		entity.setNoGravity(true);
		entity.setInvisible(true);
		entity.setSmall(true);
		entity.setArms(false);
		entity.setMarker(true);
		String line = formattedText.get(lineCount);
		entity.getBukkitEntity().setCustomName(line);
		entity.setCustomNameVisible(true);
		world.addEntity(entity);
		entities.add(lineCount, entity);
	}

	/**
	 * Removes old entities so that this {@code TextPanel} no longer displays text.
	 */
	private void removeEntities() {
		int numEntities = entities.size();
		for (int i = 0; i < numEntities; i++) {
			// always take 0 because the entity list is being modified
			removeSingleEntity(0);
		}
		entities.clear();
		spawned = false;
	}

	/**
	 * Removes a single entity.
	 * 
	 * @param lineCount the number of line that the entity to be removed represents.
	 */
	private void removeSingleEntity(int lineCount) {
		World world = ((CraftWorld) location.getWorld()).getHandle();
		EntityArmorStand entity = entities.get(lineCount);
		entity.killEntity();
		entities.remove(entity);
	}

}
