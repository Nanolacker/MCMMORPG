package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.StringUtils;

import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.World;

public final class TextPanel {

	private static final double LINE_SEPEARATION_DISTANCE = 0.25;
	private static final double SPAWNER_PERIOD = 1.0;
	private static final double SPAWN_RADIUS = 50.0;

	private static final List<TextPanel> textPanels = new ArrayList<>();

	private boolean visible;
	private boolean spawned;
	private Location location;
	private int lineLength;
	private String text;
	private List<String> lines;
	private List<EntityArmorStand> entities;

	static {
		RepeatingTask spawner = new RepeatingTask(SPAWNER_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < textPanels.size(); i++) {
					TextPanel textPanel = textPanels.get(i);
					textPanel.spawnUpdate();
				}
			}
		};
		spawner.schedule();
	}

	public TextPanel(Location location, String text) {
		this.visible = false;
		spawned = false;
		this.location = location;
		lineLength = StringUtils.STANDARD_LINE_LENGTH;
		setText(text);
		textPanels.add(this);
		entities = new ArrayList<>();
	}

	public TextPanel(Location location) {
		this(location, "");
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		if (this.visible == visible) {
			return;
		}
		this.visible = visible;
	}

	private void spawnUpdate() {
		boolean playerIsNearby = playerIsNearby();
		if (playerIsNearby && visible) {
			if (!spawned) {
				spawn();
			}
		} else {
			if (spawned) {
				despawn();
			}
		}
	}

	private boolean playerIsNearby() {
		ArrayList<Entity> nearbyEntites = (ArrayList<Entity>) location.getWorld().getNearbyEntities(location,
				SPAWN_RADIUS, SPAWN_RADIUS, SPAWN_RADIUS);
		for (int i = 0; i < nearbyEntites.size(); i++) {
			Entity nearbyEntity = nearbyEntites.get(i);
			if (nearbyEntity.getType() == EntityType.PLAYER) {
				return true;
			}
		}
		return false;
	}

	private void spawn() {
		spawned = true;
		addAllEntities();
	}

	private void despawn() {
		spawned = false;
		removeAllEntities();
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
		Location targetEntityLocation = location.clone();
		if (visible && spawned) {
			int lineCount = lines.size();
			for (int i = 0; i < lineCount; i++) {
				EntityArmorStand entity = entities.get(i);
				entity.setPosition(targetEntityLocation.getX(), targetEntityLocation.getY(),
						targetEntityLocation.getZ());
				targetEntityLocation.setY(targetEntityLocation.getY() - LINE_SEPEARATION_DISTANCE);
			}
		}
	}

	public int getLineLength() {
		return lineLength;
	}

	public void setLineLength(int lineLength) {
		this.lineLength = lineLength;
		setText0();
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		setText0();
	}

	private void setText0() {
		lines = StringUtils.lineSplit(text, lineLength);
		int lineCount = lines.size();
		if (visible) {
			resize(lineCount);
			updateEntityDisplayNames();
		}
	}

	private void addAllEntities() {
		World world = ((CraftWorld) location.getWorld()).getHandle();
		int lineCount = lines.size();
		for (int i = 0; i < lineCount; i++) {
			addEntity(i, world);
		}
		updateEntityDisplayNames();
	}

	private void removeAllEntities() {
		while (!entities.isEmpty()) {
			removeEntity(0);
		}
	}

	private void updateEntityDisplayNames() {
		int lineCount = lines.size();
		for (int i = 0; i < lineCount; i++) {
			updateEntityDisplayName(i);
		}
	}

	private void updateEntityDisplayName(int index) {
		EntityArmorStand entity = entities.get(index);
		String line = lines.get(index);
		entity.getBukkitEntity().setCustomName(line);
	}

	private void resize(int lineCount) {
		int entityCount = entities.size();
		if (entityCount < lineCount) {
			// add entities
			World world = ((CraftWorld) location.getWorld()).getHandle();
			for (int i = entityCount; i < lineCount; i++) {
				addEntity(i, world);
			}
		} else if (entityCount > lineCount) {
			// remove entities
			for (int i = entityCount; i > lineCount; i--) {
				removeEntity(i);
			}
		}
	}

	private void addEntity(int index, World world) {
		EntityArmorStand entity = new EntityArmorStand(world, location.getX(),
				location.getY() - index * LINE_SEPEARATION_DISTANCE, location.getZ());
		entity.setNoGravity(true);
		entity.collides = false;
		entity.setInvisible(true);
		entity.setSmall(true);
		entity.setArms(false);
		entity.setMarker(true);
		entity.setCustomNameVisible(true);
		world.addEntity(entity);
		entities.add(index, entity);
	}

	private void removeEntity(int index) {
		EntityArmorStand entity = entities.remove(index);
		entity.killEntity();
	}

}
