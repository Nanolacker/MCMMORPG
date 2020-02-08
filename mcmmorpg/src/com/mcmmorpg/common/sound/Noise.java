package com.mcmmorpg.common.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Noise {

	private final Sound type;
	private final float volume, pitch;

	public Noise(Sound type, float volume, float pitch) {
		this.type = type;
		this.volume = volume;
		this.pitch = pitch;
	}

	public Noise(Sound type) {
		this(type, 1.0f, 1.0f);
	}

	public Noise(Sound type, float volume, MusicNote note) {
		this(type, volume, note.getPitch());
	}

	public void play(Location source) {
		World world = source.getWorld();
		world.playSound(source, type, SoundCategory.MASTER, volume, pitch);
	}

	public void play(Player player) {
		Location source = player.getLocation();
		play(player, source);
	}

	public void play(Player player, Location source) {
		player.playSound(source, type, SoundCategory.MASTER, volume, pitch);
	}

}
