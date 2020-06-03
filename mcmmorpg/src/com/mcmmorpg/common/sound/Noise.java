package com.mcmmorpg.common.sound;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * A single sound that can be played ambiently or to a specific player.
 */
public class Noise {

	private final Sound type;
	private final float volume, pitch;

	/**
	 * Create a new noise with the specified volume and pitch.
	 */
	public Noise(Sound type, float volume, float pitch) {
		this.type = type;
		this.volume = volume;
		this.pitch = pitch;
	}

	/**
	 * Create a new noise.
	 */
	public Noise(Sound type) {
		this(type, 1.0f, 1.0f);
	}

	/**
	 * Create a new noise with the specified volume and note.
	 */
	public Noise(Sound type, float volume, MusicNote note) {
		this(type, volume, note.getPitch());
	}

	/**
	 * Play this noise ambiently at the specified source.
	 */
	public void play(Location source) {
		World world = source.getWorld();
		world.playSound(source, type, SoundCategory.MASTER, volume, pitch);
	}

	/**
	 * Play this noise to strictly the specified player.
	 */
	public void play(Player player) {
		Location source = player.getLocation();
		play(player, source);
	}

	/**
	 * Play this noise to strictly the specified player character.
	 */
	public void play(PlayerCharacter pc) {
		play(pc.getPlayer());
	}

	/**
	 * Play this noise to strictly the specified player from the specified source.
	 */
	public void play(Player player, Location source) {
		player.playSound(source, type, SoundCategory.MASTER, volume, pitch);
	}

	/**
	 * Play this noise to strictly the specified player character from the specified
	 * source.
	 */
	public void play(PlayerCharacter pc, Location location) {
		play(pc.getPlayer(), location);
	}

}
