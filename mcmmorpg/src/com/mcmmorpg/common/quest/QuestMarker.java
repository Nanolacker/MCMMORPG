package com.mcmmorpg.common.quest;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.util.CardinalDirection;

/**
 * A marker that guides player characters toward quest-related locations.
 */
public class QuestMarker {

	private static final String TEXT = ChatColor.YELLOW + "!";
	private static final double STANDARD_RADIUS = 50.0;
	private static final String QUEST_MARKER_TUTORIAL_TAG = "QUEST_MARKER_TUTORIAL";

	private TextPanel textPanel;
	private Collider area;

	private QuestMarker(Location location, double radius) {
		textPanel = new TextPanel(location, TEXT);
		textPanel.setVisible(true);
		double diameter = radius * 2;
		area = new Collider(location, diameter, diameter, diameter) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					CardinalDirection direction = CardinalDirection
							.forVector(location.clone().subtract(pc.getLocation()).toVector());
					pc.sendMessage(String.format(
							ChatColor.YELLOW + "Quest marker " + ChatColor.GRAY + "located " + ChatColor.GREEN
									+ direction + ChatColor.GRAY + " at " + ChatColor.GREEN + "(%.0f, %.0f, %.0f)",
							location.getX(), location.getY(), location.getZ()));
					if (!pc.hasTag(QUEST_MARKER_TUTORIAL_TAG)) {
						new DelayedTask(1) {
							@Override
							protected void run() {
								pc.sendMessage(ChatColor.GRAY + "[" + ChatColor.GREEN + "Tutorial" + ChatColor.GRAY
										+ "]: " + ChatColor.WHITE
										+ "Quest markers point you toward important locations for quests, usually quest givers.");
							}
						}.schedule();
						pc.addTag(QUEST_MARKER_TUTORIAL_TAG);
					}
				}
			}
		};
		area.setActive(true);
	}

	/**
	 * Creates a quest marker that has the standard radius in which player
	 * characters will be notified.
	 */
	public static QuestMarker createMarker(Location location) {
		return createMarker(location, STANDARD_RADIUS);
	}

	/**
	 * Creates a quest marker with the specified radius in which player characters
	 * will be notified.
	 */
	public static QuestMarker createMarker(Location location, double radius) {
		return new QuestMarker(location, radius);
	}

	/**
	 * Returns the location of this quest marker.
	 */
	public Location getLocation() {
		return textPanel.getLocation();
	}

	/**
	 * Moves this quest marker to the specified location.
	 */
	public void setLocation(Location location) {
		textPanel.setLocation(location);
		area.setCenter(location);
	}

	/**
	 * Sets whether or not this quest marker is visible. By default, it is visible.
	 */
	public void setVisible(boolean visible) {
		textPanel.setVisible(visible);
	}

}
