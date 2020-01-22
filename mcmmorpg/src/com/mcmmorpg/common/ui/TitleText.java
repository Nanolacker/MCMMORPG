package com.mcmmorpg.common.ui;

import org.bukkit.entity.Player;

import com.mcmmorpg.common.utils.MathUtils;

public class TitleText {

	private final String title;
	private final String subtitle;
	private final int fadeInTicks;
	private final int stayTicks;
	private final int fadeOutTicks;

	public TitleText(String title, String subtitle, double fadeInSeconds, double staySeconds, double fadeOutSeconds) {
		this.title = title;
		this.subtitle = subtitle;
		this.fadeInTicks = MathUtils.secondsToTicks(fadeInSeconds);
		this.stayTicks = MathUtils.secondsToTicks(staySeconds);
		this.fadeOutTicks = MathUtils.secondsToTicks(fadeOutSeconds);
	}

	public TitleText(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
		// default values used by Bukkit
		this.fadeInTicks = 10;
		this.stayTicks = 70;
		this.fadeOutTicks = 20;
	}

	public void apply(Player player) {
		player.sendTitle(title, subtitle, fadeInTicks, stayTicks, fadeOutTicks);
	}

}
