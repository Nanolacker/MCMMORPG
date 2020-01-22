package com.mcmmorpg.common;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterEnterZoneEvent;
import com.mcmmorpg.common.event.PlayerCharacterExitZoneEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.ui.TitleMessage;

public abstract class Zone {

	private static final Map<String, Zone> zoneMap;

	static {
		zoneMap = new HashMap<>();
	}

	public static Zone forName(String name) {
		return zoneMap.get(name);
	}

	private final String name;
	private final World world;
	private final ChatColor displayColor;
	private final TitleMessage welcomeTitleMessage;

	public Zone(String name, World world, BoundingBox[] zoneBounds, ChatColor displayColor) {
		this.name = name;
		this.world = world;
		this.displayColor = displayColor;
		this.welcomeTitleMessage = new TitleMessage(name, "");
		for (BoundingBox bb : zoneBounds) {
			ZoneBoundCollider collider = new ZoneBoundCollider(this, bb);
			collider.setActive(true);
		}
	}

	public String getName() {
		return name;
	}

	public World getWorld() {
		return world;
	}
	
	private void welcome(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		welcomeTitleMessage.apply(player);
	}

	public abstract void onEnter(PlayerCharacter pc);

	public abstract void onExit(PlayerCharacter pc);

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
				zone.welcome(pc);
				zone.onEnter(pc);
				PlayerCharacterEnterZoneEvent event = new PlayerCharacterEnterZoneEvent(pc, zone);
				EventManager.callEvent(event);
			}
		}

		@Override
		protected void onCollisionExit(Collider other) {
			if (other instanceof PlayerCharacterCollider) {
				PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
				zone.onExit(pc);
				PlayerCharacterExitZoneEvent event = new PlayerCharacterExitZoneEvent(pc, zone);
				EventManager.callEvent(event);
			}
		}
	}

}
