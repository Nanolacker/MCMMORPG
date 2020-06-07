package com.mcmmorpg.common.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import com.mcmmorpg.common.utils.StringUtils;

/**
 * Uses scoreboards to display text on a player's sidebar.
 */
public class SidebarText {

	private static final Set<Player> players = new HashSet<>();

	private String title;
	private String text;

	/**
	 * Creates sidebar text with the specified title and text to be displayed below.
	 */
	public SidebarText(String title, String text) {
		this.title = title;
		this.text = text;
	}

	/**
	 * Applies sidebar text to the specified player.
	 */
	public void apply(Player player) {
		Scoreboard scoreboard;
		if (!players.contains(player)) {
			scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
			player.setScoreboard(scoreboard);
			players.add(player);
		} else {
			scoreboard = player.getScoreboard();
			Objective objective = scoreboard.getObjective("objective");
			objective.unregister();
		}
		Objective objective = scoreboard.registerNewObjective("objective", "dummy", title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		List<String> lines = StringUtils.lineSplit(text);
		Set<String> usedLines = new HashSet<>();
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			while (usedLines.contains(line)) {
				// scoreboards don't display duplicate lines
				line = ChatColor.RESET + line;
			}
			usedLines.add(line);
			Score score = objective.getScore(line);
			score.setScore(lines.size() - i - 1);
		}
		player.setScoreboard(scoreboard);
	}

	/**
	 * No sidebar text will be displayed to this player.
	 */
	public static void clear(Player player) {
		Scoreboard scoreboard = player.getScoreboard();
		if (scoreboard != null) {
			scoreboard.clearSlot(DisplaySlot.SIDEBAR);
		}
	}

}
