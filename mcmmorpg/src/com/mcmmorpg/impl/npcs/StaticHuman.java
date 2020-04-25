package com.mcmmorpg.impl.npcs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Cow;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.time.RepeatingTask;

/**
 * A non-moving NPC. Not intended to be damaged or killed.
 */
public abstract class StaticHuman extends AbstractFriendlyHuman {

	private static final PotionEffect SLOWNESS = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128);

	private static final List<StaticHuman> staticHumans = new ArrayList<>();

	static {
		new RepeatingTask(0.5) {
			@Override
			protected void run() {
				for (int i = 0; i < staticHumans.size(); i++) {
					StaticHuman human = staticHumans.get(i);
					if (human.isSpawned()) {
						Cow ai = human.ai;
						Location spawnLocation = human.spawnLocation;
						Location aiLocation = ai.getLocation();
						double distanceSquared = spawnLocation.distanceSquared(aiLocation);
						if (distanceSquared > 0.01) {
							ai.teleport(spawnLocation);
						}
					}
				}
			}
		}.schedule();
	}

	protected StaticHuman(String name, int level, Location location, String textureData, String textureSignature) {
		super(name, level, location, textureData, textureSignature);
		staticHumans.add(this);
	}

	@Override
	protected void spawn() {
		super.spawn();
		ai.addPotionEffect(SLOWNESS);
	}

}
