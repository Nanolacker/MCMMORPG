package com.mcmmorpg.common.quest;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.ui.TextPanel;

public class QuestMarker {

	private static final String TEXT = ChatColor.YELLOW + "!";
	private static final double STANDARD_RADIUS = 50.0;

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
					pc.sendMessage(String.format(
							ChatColor.YELLOW + "Quest marker " + ChatColor.GRAY + "located at (%.0f, %.0f, %.0f)",
							location.getX(), location.getY(), location.getZ()));
				}
			}
		};
		area.setActive(true);
	}

	public static QuestMarker createMarker(Location location) {
		return createMarker(location, STANDARD_RADIUS);
	}

	public static QuestMarker createMarker(Location location, double radius) {
		return new QuestMarker(location, radius);
	}

	public Location getLocation() {
		return textPanel.getLocation();
	}

	public void setLocation(Location location) {
		textPanel.setLocation(location);
		area.setCenter(location);
	}

	public void setVisible(boolean visible) {
		textPanel.setVisible(visible);
	}

}
