package com.mcmmorpg.common.character;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.ui.TextArea;

public abstract class CommonCharacter {

	private String name;
	private int level;
	private Location location;
	private boolean alive;
	private double currentHealth;
	private double maxHealth;
	private TextArea nameplate;

	/**
	 * Constructs a character initialized with max health.
	 */
	protected CommonCharacter(String name, int level, Location location, double maxHealth) {
		this.name = name;
		this.level = level;
		this.location = location;
		alive = true;
		currentHealth = maxHealth;
		this.maxHealth = maxHealth;
		Location nameplateLocation = getNameplateLocation();
		nameplate = new TextArea(nameplateLocation);
		nameplate.setCharactersPerLine(25);
		updateNameplateText();
	}

	public final String getName() {
		return name;
	}

	@OverridingMethodsMustInvokeSuper
	public void setName(String name) {
		this.name = name;
	}

	public final int getLevel() {
		return level;
	}

	@OverridingMethodsMustInvokeSuper
	public void setLevel(int level) {
		this.level = level;
	}

	/**
	 * Returns a clone for safety.
	 */
	public final Location getLocation() {
		return location.clone();
	}

	@OverridingMethodsMustInvokeSuper
	public void setLocation(Location location) {
		// clone for safety
		this.location = location.clone();
		Location nameplateLocation = getNameplateLocation();
		nameplate.setLocation(nameplateLocation);
	}

	public final boolean isAlive() {
		return alive;
	}

	@OverridingMethodsMustInvokeSuper
	public void setAlive(boolean alive) {
		boolean temp = this.alive;
		this.alive = alive;
		if (temp && !alive) {
			die();
		}
	}

	public final double getCurrentHealth() {
		return currentHealth;
	}

	@OverridingMethodsMustInvokeSuper
	public void setCurrentHealth(double currentHealth) {
		this.currentHealth = currentHealth;
		if (currentHealth <= 0.0) {
			this.currentHealth = 0.0;
			setAlive(false);
		}
		updateNameplateText();
	}

	public final double getMaxHealth() {
		return currentHealth;
	}

	@OverridingMethodsMustInvokeSuper
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
		updateNameplateText();
	}

	@OverridingMethodsMustInvokeSuper
	protected void die() {
		setNameplateVisible(false);
	}

	private final String nameplateText() {
		int numBars = 20;
		StringBuilder text = new StringBuilder();
		text.append(ChatColor.WHITE + "[" + ChatColor.GOLD + "Lv. " + level + ChatColor.WHITE + "] " + ChatColor.RESET
				+ name + '\n');
		text.append(ChatColor.WHITE + "[");
		double currentToMaxHealthRatio = currentHealth / maxHealth;
		int numRedBars = (int) (numBars * currentToMaxHealthRatio);
		text.append(ChatColor.RED.toString());
		for (int i = 0; i < numRedBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.GRAY.toString());
		for (int i = numRedBars; i < numBars; i++) {
			text.append('|');
		}
		text.append(ChatColor.WHITE + "]");
		return text.toString();
	}

	private final void updateNameplateText() {
		String nameplateText = nameplateText();
		nameplate.setText(nameplateText);
	}

	public final void setNameplateVisible(boolean visible) {
		nameplate.setVisible(visible);
	}

	protected abstract Location getNameplateLocation();

}
