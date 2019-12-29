package com.mcmmorpg.impl.npcs;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Zombie;

import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.physics.Collider;

public class BulskanUndead extends NonPlayerCharacter {

	private final Location spawnLocation;
	private Zombie zombie;

	public BulskanUndead(int level, Location location) {
		super("Undead", level, location, maxHealth(level));
		this.spawnLocation = location;
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		zombie = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
		zombie.setRemoveWhenFarAway(false);
	}

	@Override
	public void despawn() {
		super.despawn();
		zombie.remove();
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

	private static class UndeadHitbox extends Collider {

		public UndeadHitbox(BulskanUndead undead) {
			super(undead.getLocation().add(0, 1, 0), 1, 2, 1);
		}

		@Override
		protected void onCollisionEnter(Collider other) {

		}

		@Override
		protected void onCollisionExit(Collider other) {

		}

	}

}
