package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.common.utils.StringUtils;

/**
 * Override onComplete() to add completion behavior. Be sure to set rate after
 * instantiation.
 */
public class ProgressBar {

	/**
	 * In seconds.
	 */
	private static final double UPDATE_PERIOD = 0.15;
	private static final int TEXT_PANEL_PIPE_COUNT = 16;

	private static final List<ProgressBar> progressBars = new ArrayList<>();

	private String title;
	private ProgressBarColor color;
	private double progress;
	private double rate;
	private TextPanel textPanel;
	private BossBar bossBar;

	static {
		RepeatingTask progressUpdater = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				for (int i = 0; i < progressBars.size(); i++) {
					ProgressBar progressBar = progressBars.get(i);
					progressBar.update();
				}
			}
		};
		progressUpdater.schedule();
	}

	public ProgressBar(String title, ProgressBarColor color) {
		this.title = title;
		this.color = color;
		this.progress = 0;
		this.rate = 0;
		this.textPanel = null;
		this.bossBar = null;
		progressBars.add(this);
	}

	private final void update() {
		setProgress(progress + rate * UPDATE_PERIOD);
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
		if (textPanel != null) {
			updateTextPanelText();
		}
		if (bossBar != null) {
			bossBar.setTitle(title);
		}
	}

	public final ProgressBarColor getColor() {
		return color;
	}

	public final void setColor(ProgressBarColor color) {
		this.color = color;
		if (textPanel != null) {
			updateTextPanelText();
		}
		if (bossBar != null) {
			bossBar.setColor(color.barColor);
		}
	}

	/**
	 * 0-1.
	 */
	public final double getProgress() {
		return progress;
	}

	/**
	 * 0-1.
	 */
	public final void setProgress(double progress) {
		this.progress = MathUtils.clamp(progress, 0, 1);
		if (this.progress == 1) {
			onComplete();
			dispose();
		} else {
			if (textPanel != null) {
				updateTextPanelText();
			}
			if (bossBar != null) {
				updateBossBarProgress();
			}
		}
	}

	/**
	 * In proportion per second.
	 */
	public final double getRate() {
		return rate;
	}

	/**
	 * In proportion per second.
	 */
	public final void setRate(double rate) {
		this.rate = rate;
	}

	public final void display(Location location) {
		if (textPanel == null) {
			textPanel = new TextPanel(location);
			updateTextPanelText();
			textPanel.setVisible(true);
		} else {
			textPanel.setLocation(location);
		}
	}

	public final void display(PlayerCharacter pc) {
		display(pc.getPlayer());
	}

	public final Location getDisplayLocation() {
		return textPanel == null ? null : textPanel.getLocation();
	}

	public final void display(Player player) {
		if (bossBar == null) {
			bossBar = Bukkit.createBossBar(title, color.barColor, BarStyle.SOLID);
			bossBar.setProgress(0.0);
		}
		bossBar.addPlayer(player);
	}

	public final void hide(PlayerCharacter pc) {
		hide(pc.getPlayer());
	}

	public final void hide(Player player) {
		if (bossBar != null) {
			bossBar.removePlayer(player);
		}
	}

	private final void updateTextPanelText() {
		StringBuilder text = new StringBuilder();
		text.append(title);
		text.append("\n");
		text.append(ChatColor.GRAY + "[");
		int numColoredPipes = (int) (progress * TEXT_PANEL_PIPE_COUNT);
		int numGrayPipes = TEXT_PANEL_PIPE_COUNT - numColoredPipes;
		text.append(color.chatColor + StringUtils.repeat("|", numColoredPipes));
		text.append(ChatColor.GRAY + StringUtils.repeat("|", numGrayPipes));
		text.append(ChatColor.GRAY + "]");
		textPanel.setText(text.toString());
	}

	private final void updateBossBarProgress() {
		bossBar.setProgress(progress);
	}

	/**
	 * Call this to get rid of it before it finishes. Automatically called when it
	 * does finish.
	 */
	public final void dispose() {
		if (textPanel != null) {
			textPanel.setVisible(false);
		}
		if (bossBar != null) {
			bossBar.removeAll();
		}
		progressBars.remove(this);
	}

	/**
	 * Invoked when the progress bar becomes entirely full.
	 */
	protected void onComplete() {
	}

	public static enum ProgressBarColor {

		BLUE(ChatColor.BLUE, BarColor.BLUE), GREEN(ChatColor.GREEN, BarColor.GREEN), PINK(ChatColor.LIGHT_PURPLE,
				BarColor.PINK), PURPLE(ChatColor.DARK_PURPLE, BarColor.PURPLE), RED(ChatColor.RED, BarColor.RED), WHITE(
						ChatColor.WHITE, BarColor.WHITE), YELLOW(ChatColor.YELLOW, BarColor.YELLOW);

		private final ChatColor chatColor;
		private final BarColor barColor;

		ProgressBarColor(ChatColor chatColor, BarColor barColor) {
			this.chatColor = chatColor;
			this.barColor = barColor;
		}

	}

}
