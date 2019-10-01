package com.mcmmorpg.test;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Vindicator;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.physics.Collider;

public class Monster extends NonPlayerCharacter {

	private Vindicator entity;
	private MovementSyncer movementSyncer;
	private CharacterCollider collider;

	public Monster(Location location) {
		super("Monster", 1, location, 20.0);
		collider = new CharacterCollider(this, location.clone().add(0.0, 1.0, 0.0), 1, 2, 1) {
			@Override
			protected void onCollisionEnter(Collider other) {
			}

			@Override
			protected void onCollisionExit(Collider other) {
			}
		};
		collider.setActive(true);
	}

	@Override
	public void spawn() {
		super.spawn();
		World world = Bukkit.getWorld("world");
		Location location = getLocation();
		entity = (Vindicator) world.spawnEntity(location, EntityType.VINDICATOR);
		entity.setInvulnerable(true);
		movementSyncer = new MovementSyncer(this, entity, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		movementSyncer.setEnabled(true);
		Debug.log("Monster spawned");
	}

	@Override
	public void despawn() {
		super.despawn();
		entity.remove();
		movementSyncer.setEnabled(false);
		Debug.log("Monster despawned");
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		collider.setCenter(location.clone().add(0.0, 1.0, 0.0));
	}

	@Override
	protected Location getNameplateLocation() {
		return getLocation().add(0, 2, 0);
	}

}
