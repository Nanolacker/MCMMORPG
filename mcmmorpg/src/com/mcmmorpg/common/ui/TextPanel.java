package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.StringUtility;

/**
 * Displays text at a location.
 */
public final class TextPanel {
    private static final double LINE_SEPEARATION_DISTANCE = 0.25;
    private static final double SPAWNER_PERIOD = 1.0;
    private static final double SPAWN_RADIUS = 50.0;

    private static final List<TextPanel> visibleTextPanels = new ArrayList<>();

    private boolean visible;
    private boolean spawned;
    private Location location;
    private int lineLength;
    private String text;
    private List<String> lines;
    private List<ArmorStand> entities;

    static {
        RepeatingTask spawner = new RepeatingTask(SPAWNER_PERIOD) {
            @Override
            protected void run() {
                for (int i = 0; i < visibleTextPanels.size(); i++) {
                    TextPanel textPanel = visibleTextPanels.get(i);
                    textPanel.spawnUpdate();
                }
            }
        };
        spawner.schedule();
    }

    /**
     * Create a new text panel at the specified location with the specified text.
     * setVisible() still needs to be invoked before this text panel will display.
     */
    public TextPanel(Location location, String text) {
        this.visible = false;
        spawned = false;
        this.location = location;
        lineLength = StringUtility.STANDARD_LINE_LENGTH;
        setText(text);
        entities = new ArrayList<>();
    }

    /**
     * Create a new text panel at the specified location with empty text.
     * setVisible() still needs to be invoked before this text panel will display.
     */
    public TextPanel(Location location) {
        this(location, "");
    }

    /**
     * Returns whether players can see this text panel.
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Sets whether players can see this text panel.
     */
    public void setVisible(boolean visible) {
        if (this.visible == visible) {
            return;
        }
        this.visible = visible;
        if (visible) {
            spawn();
            visibleTextPanels.add(this);
        } else {
            despawn();
            visibleTextPanels.remove(this);
        }
    }

    private void spawnUpdate() {
        boolean playerIsNearby = playerIsNearby();
        if (playerIsNearby) {
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

    /**
     * Returns where this text panel is being displayed from.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Sets where this text panel is being displayed from.
     */
    public void setLocation(Location location) {
        this.location = location;
        Location targetEntityLocation = location.clone();
        if (visible && spawned) {
            int lineCount = lines.size();
            for (int i = 0; i < lineCount; i++) {
                ArmorStand entity = entities.get(i);
                entity.teleport(targetEntityLocation);
                targetEntityLocation.setY(targetEntityLocation.getY() - LINE_SEPEARATION_DISTANCE);
            }
        }
    }

    /**
     * Return how many characters are allowed to be in a line for this text panel.
     */
    public int getLineLength() {
        return lineLength;
    }

    /**
     * Sets how many characters are allowed to be in a line for this text panel and
     * updates its text.
     */
    public void setLineLength(int lineLength) {
        this.lineLength = lineLength;
        setText0();
    }

    /**
     * Return the text displayed by this text panel.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text displayed by this text panel.
     */
    public void setText(String text) {
        this.text = text;
        setText0();
    }

    private void setText0() {
        lines = StringUtility.lineSplit(text, lineLength);
        int lineCount = lines.size();
        if (visible) {
            resize(lineCount);
            updateEntityDisplayNames();
        }
    }

    private void addAllEntities() {
        int lineCount = lines.size();
        for (int i = 0; i < lineCount; i++) {
            addEntity(i);
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
        ArmorStand entity = entities.get(index);
        String line = lines.get(index);
        if (!StringUtility.isEmpty(line)) {
            entity.setCustomName(line);
            entity.setCustomNameVisible(true);
        }
    }

    private void resize(int lineCount) {
        int entityCount = entities.size();
        if (entityCount < lineCount) {
            // add entities
            for (int i = entityCount; i < lineCount; i++) {
                addEntity(i);
            }
        } else if (entityCount > lineCount) {
            // remove entities
            for (int i = entityCount - 1; i >= lineCount; i--) {
                removeEntity(i);
            }
        }
    }

    private void addEntity(int index) {
        Location entityLocation = new Location(location.getWorld(), location.getX(),
                location.getY() - index * LINE_SEPEARATION_DISTANCE, location.getZ());
        ArmorStand entity = (ArmorStand) location.getWorld().spawnEntity(entityLocation, EntityType.ARMOR_STAND);
        entity.setPersistent(false);
        entity.setGravity(false);
        entity.setCollidable(false);
        entity.setVisible(false);
        entity.setSmall(true);
        entity.setArms(false);
        entity.setMarker(true);
        entities.add(index, entity);
    }

    private void removeEntity(int index) {
        ArmorStand entity = entities.remove(index);
        entity.remove();
    }
}
