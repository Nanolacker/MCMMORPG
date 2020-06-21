package com.mcmmorpg.common.navigation;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.ui.TextPanel;

/**
 * A marker that guides player characters toward quest-related locations.
 */
public abstract class QuestMarker {

	private static final String TEXT_PANEL_TEXT = ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "!";

	private TextPanel textPanel;

	/**
	 * Create a new quest marker at the specified location to display in the
	 * overworld and also on maps if added to a map segment.
	 */
	public QuestMarker(Location location) {
		textPanel = new TextPanel(location, TEXT_PANEL_TEXT);
		textPanel.setVisible(true);
	}

	/**
	 * Create a new quest marker above the head of the specified character to
	 * display in the overworld and also on maps if added to a map segment. The
	 * location is determined by the character's height.
	 */
	public QuestMarker(AbstractCharacter character) {
		this(character.getLocation().add(0, character.getHeight() + 0.25, 0));
	}

	/**
	 * Returns the location of this quest marker.
	 */
	public Location getLocation() {
		return textPanel.getLocation();
	}

	/**
	 * How this quest marker is displayed on maps.
	 */
	protected abstract QuestMarkerDisplayType getDisplayType(PlayerCharacter pc);

	/**
	 * How a quest maker is displayed on a map.
	 */
	public enum QuestMarkerDisplayType {
		HIDDEN(""), READY_TO_START("§72;!"), READY_TO_TURN_IN("§74;?");

		private final String mapText;

		QuestMarkerDisplayType(String mapText) {
			this.mapText = mapText;
		}

		public String getMapText() {
			return mapText;
		}
	}

}
