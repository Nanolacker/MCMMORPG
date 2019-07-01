package com.mcmmorpg.common.character;

import javax.annotation.OverridingMethodsMustInvokeSuper;

import org.bukkit.Location;

import com.mcmmorpg.TextArea;

public abstract class CommonCharacter {

	private String name;
	private int level;
	private Location location;
	private boolean alive;
	private TextArea nameplate;

	protected CommonCharacter(String name, int level, Location location) {
		this.name = name;
		this.level = level;
		this.location = location;
		alive = false;
		nameplate = new TextArea(0);
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

	public final Location getLocation() {
		return location;
	}

	@OverridingMethodsMustInvokeSuper
	public void setLocation(Location location) {
		this.location = location;
	}

	public boolean isAlive() {
		return alive;
	}

	public void die() {
		alive = false;
	}

}
