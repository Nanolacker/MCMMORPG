package com.mcmmorpg.common.ui;

  import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Uses text panels to signal something to the player, such as a merchant or
 * quest giver.
 */
public class Notice {

	private TextPanel textPanel;

	private Notice(NoticeType type, Location location) {
		textPanel = new TextPanel(location, type.text);
		textPanel.setVisible(true);
	}

	public static Notice createNotice(NoticeType type, Location location) {
		return new Notice(type, location);
	}

	public Location getLocation() {
		return textPanel.getLocation();
	}

	public void setLocation(Location location) {
		textPanel.setLocation(location);
	}

	public void setVisible(boolean visible) {
		textPanel.setVisible(visible);
	}

	public static enum NoticeType {
		QUEST(ChatColor.YELLOW + "!"), TRADE(ChatColor.GOLD + "$");

		private final String text;

		private NoticeType(String text) {
			this.text = text;
		}
	}

}
