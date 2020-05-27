package com.mcmmorpg.common.ui;

import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.utils.MathUtils;

/**
 * Allows text to be displayed to a player in the center of their view in big
 * letters (title slot).
 */
public class TitleText {

	private final String title;
	private final String subtitle;
	private final int fadeInTicks;
	private final int stayTicks;
	private final int fadeOutTicks;

	/**
	 * Create a new title text.
	 */
	public TitleText(String title, String subtitle, double fadeInSeconds, double staySeconds, double fadeOutSeconds) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeInTicks = MathUtils.secondsToTicks(fadeInSeconds);
		this.stayTicks = MathUtils.secondsToTicks(staySeconds);
		this.fadeOutTicks = MathUtils.secondsToTicks(fadeOutSeconds);
	}

	/**
	 * Create a new title text, using default fade durations.
	 */
	public TitleText(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		// default values used by Bukkit
		this.fadeInTicks = 10;
		this.stayTicks = 70;
		this.fadeOutTicks = 20;
	}

	/**
	 * Display text to the player.
	 */
	public void apply(Player player) {
		player.sendTitle(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks);
	}

	/**
	 * Display text to the player character.
	 */
	public void apply(PlayerCharacter pc) {
		apply(pc.getPlayer());
	}

}
