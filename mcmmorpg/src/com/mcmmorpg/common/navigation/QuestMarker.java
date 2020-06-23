package com.mcmmorpg.common.navigation;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.ui.TextPanel;

/**
 * A marker that guides player characters toward quest-related locations.
 */
public abstract class QuestMarker {

	private static final String TEXT_PANEL_TEXT = ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "!";

	private final Quest quest;
	private final Location location;
	private TextPanel textPanel;

	/**
	 * Create a new quest marker at the specified location to display in the
	 * overworld and also on maps if added to a map segment.
	 */
	public QuestMarker(Quest quest, Location location) {
		this.quest = quest;
		this.location = location;
	}

	/**
	 * Create a new quest marker above the head of the specified character to
	 * display in the overworld and also on maps if added to a map segment. The
	 * location is determined by the character's height.
	 */
	public QuestMarker(Quest quest, AbstractCharacter character) {
		this(quest, character.getLocation().add(0, character.getHeight() + 0.25, 0));
	}

	public Quest getQuest() {
		return quest;
	}

	/**
	 * Returns the location of this quest marker.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Returns whether this quest marker is visible on a text panel at its location.
	 */
	public boolean isTextPanelVisible() {
		return textPanel != null;
	}

	/**
	 * Sets whether this quest marker will be visible on a text panel at its
	 * location.
	 */
	public void setTextPanelVisible(boolean visible) {
		if (visible) {
			textPanel = new TextPanel(location, TEXT_PANEL_TEXT);
			textPanel.setVisible(true);
		} else if (textPanel != null) {
			textPanel.setVisible(false);
		}
	}

	/**
	 * How this quest marker is displayed on maps.
	 */
	protected abstract QuestMarkerIcon getIcon(PlayerCharacter pc);

	/**
	 * How a quest maker is displayed on a map.
	 */
	public enum QuestMarkerIcon {
		HIDDEN(0), READY_TO_START(36864), READY_TO_TURN_IN(Integer.MAX_VALUE), OBJECTIVE(Integer.MAX_VALUE);

		private final double mapDisplayRangeSquared;

		private QuestMarkerIcon(double mapDisplayRangeSquared) {
			this.mapDisplayRangeSquared = mapDisplayRangeSquared;
		}

		String getText(Quest quest, PlayerCharacter pc) {
			switch (this) {
			case HIDDEN:
				return "";
			case OBJECTIVE:
				List<Quest> currentQuests = Quest.getAllQuestsMatchingStatus(pc, QuestStatus.IN_PROGRESS);
				int questNum = currentQuests.indexOf(quest) + 1;
				return ChatColor.YELLOW + "" + questNum;
			case READY_TO_START:
				return ChatColor.YELLOW + "!";
			case READY_TO_TURN_IN:
				return ChatColor.YELLOW + "?";
			default:
				return null;
			}
		}

		double getMapDisplayRangeSquared() {
			return mapDisplayRangeSquared;
		}
	}

}
