package com.mcmmorpg.common.ui;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.common.utils.StringUtils;

/**
 * A bar that uses a text panel to display progress in the form of a bar that
 * fills up over time. The method onComplete() can be overridden in subclasses
 * to provide additional functionality.
 */
public class ProgressBar {

	/**
	 * In seconds.
	 */
	private static final double UPDATE_PERIOD = 0.25;

	private String title;
	private int width;
	private ChatColor color;
	private double progress;
	private double rate;
	private final TextPanel textPanel;
	private final RepeatingTask updateProgress;

	public ProgressBar(Location location, String title, int width, ChatColor color) {
		this.title = title;
		this.width = width;
		this.color = color;
		progress = 0;
		rate = 0;
		textPanel = new TextPanel(location);
		textPanel.setVisible(true);
		updateProgress = new RepeatingTask(UPDATE_PERIOD) {
			@Override
			protected void run() {
				update();
			}
		};
		updateProgress.schedule();
	}

	private final void update() {
		progress += rate * UPDATE_PERIOD;
		StringBuilder text = new StringBuilder();
		text.append(title);
		text.append("\n");
		text.append(ChatColor.WHITE + "[");
		int numColoredPipes = (int) (progress * width);
		int numGrayPipes = width - numColoredPipes;
		text.append(color + StringUtils.repeat("|", numColoredPipes));
		text.append(ChatColor.GRAY + StringUtils.repeat("|", numGrayPipes));
		text.append(ChatColor.WHITE + "]");
		textPanel.setText(text.toString());
		if (progress >= 1) {
			onComplete();
			dispose();
		}
	}

	public final Location getLocation() {
		return textPanel.getLocation();
	}

	public final void setLocation(Location location) {
		textPanel.setLocation(location);
	}

	public final String getTitle() {
		return title;
	}

	public final void setTitle(String title) {
		this.title = title;
	}

	public final int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	public final ChatColor getColor() {
		return color;
	}

	public final void setColor(ChatColor color) {
		this.color = color;
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
		if (progress == 1) {
			dispose();
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

	/**
	 * Call this to get rid of it before it finishes. Automatically called when if
	 * it does finish.
	 */
	public final void dispose() {
		textPanel.setVisible(false);
		if (updateProgress.isScheduled()) {
			updateProgress.cancel();
		}
	}

	/**
	 * Invoked when the progress bar becomes entirely full.
	 */
	protected void onComplete() {
	}

}
