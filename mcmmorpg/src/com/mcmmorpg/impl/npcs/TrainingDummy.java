package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.character.Xp;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class TrainingDummy extends NonPlayerCharacter {

	private static final Noise DAMAGE_NOISE = new Noise(Sound.BLOCK_IRON_TRAPDOOR_CLOSE);
	private static final Noise DESTROY_NOISE = new Noise(Sound.BLOCK_FENCE_GATE_CLOSE);

	private final CharacterCollider hitbox;
	private ArmorStand entity;

	public TrainingDummy(Location location) {
		super(ChatColor.RED + "Training Dummy", 1, location);
		setMaxHealth(20);
		hitbox = new CharacterCollider(this, location.clone().add(0, 0.75, 0), 1, 2.5, 1);
	}

	@Override
	protected void spawn() {
		super.spawn();
		Location location = getLocation();
		entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
		hitbox.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		entity.remove();
		hitbox.setActive(false);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		DAMAGE_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		DESTROY_NOISE.play(getLocation());
		entity.remove();
		hitbox.setActive(false);
		Xp.distributeXp(getLocation(), 10, 3);
		DelayedTask respawnTask = new DelayedTask(10) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawnTask.schedule();
	}

}
