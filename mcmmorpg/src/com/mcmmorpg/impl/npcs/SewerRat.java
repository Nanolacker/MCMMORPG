package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Endermite;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.character.XP;
import com.mcmmorpg.common.sound.Noise;

public class SewerRat extends NonPlayerCharacter {

	private static final int LEVEL = 5;
	private static final int XP_REWARD = 10;
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_SILVERFISH_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_SILVERFISH_DEATH);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final MovementSyncer movementSyncer;
	private Endermite entity;

	public SewerRat(Location spawnLocation) {
		super(ChatColor.RED + "Rat", LEVEL, spawnLocation);
		this.spawnLocation = spawnLocation;
		this.hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 0.25, 0), 1, 0.5, 1);
		this.movementSyncer = new MovementSyncer(this, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.add(0, 0.25, 0));
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void spawn() {
		setLocation(spawnLocation);
		super.spawn();
	}

	@Override
	protected void despawn() {
		super.despawn();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		hitbox.setActive(false);
		movementSyncer.setEnabled(false);
		entity.remove();

		Location deathLocation = getLocation();
		XP.distributeXP(deathLocation, 25, XP_REWARD);
		DEATH_NOISE.play(deathLocation);
	}

}
