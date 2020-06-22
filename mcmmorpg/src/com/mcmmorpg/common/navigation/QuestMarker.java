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
	private final TextPanel textPanel;

	/**
	 * Create a new quest marker at the specified location to display in the
	 * overworld and also on maps if added to a map segment.
	 */
	public QuestMarker(Quest quest, Location location) {
		this.quest = quest;
		textPanel = new TextPanel(location, TEXT_PANEL_TEXT);
		textPanel.setVisible(true);
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
		HIDDEN, READY_TO_START, READY_TO_TURN_IN, OBJECTIVE;

		String getMapText(Quest quest, PlayerCharacter pc) {
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
	}

}
