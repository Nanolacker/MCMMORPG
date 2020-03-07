package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Uses a text panel to display an exclamation point at a location to indicate a
 * quest interaction.
 */
public class QuestNotice {

	private TextPanel textPanel;

	private QuestNotice(Location location) {
		textPanel = new TextPanel(location, ChatColor.YELLOW + "!");
		textPanel.setVisible(true);
	}

	public static QuestNotice createQuestNotice(Location location) {
		return new QuestNotice(location);
	}

	public void setLocation(Location location) {
		textPanel.setLocation(location);
	}

	public void setVisible(boolean visible) {
		textPanel.setVisible(visible);
	}

}
