package com.mcmmorpg.common.ui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import com.mcmmorpg.common.utils.StringUtils;

/**
 * Uses scoreboards to display text on a player's sidebar.
 */
public class SidebarText {

	private String title;
	private String text;
	private Scoreboard scoreboard;

	public SidebarText(String title, String text) {
		this.title = title;
		this.text = text;
		updateScoreboard();
	}

	private void updateScoreboard() {
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		this.scoreboard = scoreboardManager.getNewScoreboard();

		Objective objective = scoreboard.registerNewObjective("objective", "dummy", title);
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);

		List<String> lines = StringUtils.lineSplit(text);
		for (int i = 0; i < lines.size(); i++) {
			String line = lines.get(i);
			Score score = objective.getScore(line);
			score.setScore(lines.size() - i - 1);
		}
	}

	public void apply(Player player) {
		player.setScoreboard(scoreboard);
	}

	/**
	 * No sidebar text will be displayed to this player.
	 */
	public static void clear(Player player) {
		ScoreboardManager scoreboardManager = Bukkit.getScoreboardManager();
		player.setScoreboard(scoreboardManager.getNewScoreboard());
	}

}
