package com.mcmmorpg.common.character;

import org.bukkit.Location;

import com.mcmmorpg.TextArea;

public abstract class CommonCharacter {

	private String name;
	private int level;
	private Location location;
	private TextArea nameplate;

	protected CommonCharacter(String name, int level, Location location) {
		this.name = name;
		this.level = level;
		this.location = location;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
