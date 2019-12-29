package com.mcmmorpg.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.World;
import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;

public abstract class Zone {

	private static final Map<String, Zone> zoneMap;

	static {
		zoneMap = new HashMap<>();
	}

	public static Zone forName(String name) {
		return zoneMap.get(name);
	}

	private static class ZoneBoundCollider extends Collider {
		private final Zone zone;

		private ZoneBoundCollider(Zone zone, BoundingBox boundingBox) {
			super(zone.getWorld(), boundingBox);
			this.zone = zone;
		}

		@Override
		protected void onCollisionEnter(Collider other) {
			if (other instanceof PlayerCharacterCollider) {
				PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
				zone.onEnter(pc);
			}
		}

		@Override
		protected void onCollisionExit(Collider other) {
			if (other instanceof PlayerCharacterCollider) {
				PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
				zone.onExit(pc);
			}
		}
	}

	private final String name;
	private final World world;
	private final BoundingBox[] zoneBounds;

	public Zone(String name, World world, BoundingBox[] zoneBounds) {
		this.name = name;
		this.world = world;
		this.zoneBounds = zoneBounds;
		for (BoundingBox bb : zoneBounds) {
			ZoneBoundCollider col = new ZoneBoundCollider(this, bb);
			col.setActive(true);
		}
	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		return world;
	}
	
	public abstract void onEnter(PlayerCharacter pc);

	public abstract void onExit(PlayerCharacter pc);

}
