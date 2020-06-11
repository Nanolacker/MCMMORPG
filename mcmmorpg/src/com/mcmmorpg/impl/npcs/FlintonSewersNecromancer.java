package com.mcmmorpg.impl.npcs;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.util.BukkitUtility;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Quests;

public class FlintonSewersNecromancer extends AbstractCultist {

	private static final int LEVEL = 10;
	private static final double MAX_HEALTH = 150;
	private static final int XP_REWARD = 60;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODcyNTQ0MzcxMzUsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q5NmM4NjA3NWFhOTJiYjAwYmI2NzZlNzQ5MWM5NWUxYzY5YjU0ZjlmNzY3MzU1MjlhMGY2NmUwNGQzZDI1ZmUifX19";
	private static final String TEXTURE_SIGNATURE = "uks+6CegMiDE3DNcnJ5ZMf0iA6AtGTGUkncv9DbukLgzAYp9gmgWsm0TKaRbtOcH9TSWNYid2jr2XyezYwIxqDwZGdYLno2cqtdjwE+EzPhhvGZX5YkEHwyQtcRiPp1Yz1Mp5XBFfBPfAa6p+YTw9ry5+V4cEGfoxuxFZ3LZny8MngLnVuNro80H17Hb1QNzCSoJ224z3M9J5thNs5gliz9KO1cotbd4g9ejiBF8u+OgpU57U+0steLy8MyTGtJw1vfiRnmZ69a73BjwYkM+BIhGpR63N9Zt3GcJIn56Uwpn1ACFjHIyzVjjKrM6XTH/pXy/GQ277cmaULKBQuL0ryNb1EeLV6gkNfEqH+DFUy4wPSBnR9hKaLMP8hXJaV/JkQJk/AzzGItqyxNk8j3YzQjqwOvzJkUDUvf+rNqJMp9dZ6ZmQlH6jnvTxRlLFnTpgd0T00qjrRqQaHgE2bWsVkftsGJBg46uXKOBl/togm+1cIsHO3FfXB9gCbgBIFMYdm3rkHqc0h0otLyefd5qBZmxZQmbNv3FWCWUx/STmCVmfpZCsMu8JgWmvTRoFT2HqQEVx/uUn5zQI/EK1EXbLk+BzUBueYVDDsH5cz0fpKuUDxHI7lB4YVFfASN5kzvwNl8pNQEq02HYI5RFDsVrB0OoFFhm0EqKTI04VeYDx9M=";
	private static final Noise ZOMBIE_SPAWN_NOISE = new Noise(Sound.BLOCK_FIRE_EXTINGUISH);
	private static final Noise FIREBALL_CONJURE_NOISE = new Noise(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
	private static final Noise FIREBALL_EXPLODE_1_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE);
	private static final Noise FIREBALL_EXPLODE_2_NOISE = new Noise(Sound.BLOCK_FIRE_AMBIENT);
	private static final String SUMMON_UNDEAD = "Summon Undead";
	private static final String FIREBALL = "Fireball";
	private static final double SUMMON_UNDEAD_CHANNEL_DURATION = 8;
	private static final double FIREBALL_CHANNEL_DURATION = 6;

	private FlintonSewersUndead spawnedUndead;

	public FlintonSewersNecromancer(Location spawnLocation) {
		super(ChatColor.RED + "Necromancer", LEVEL, spawnLocation, MAX_HEALTH, XP_REWARD, TEXTURE_DATA,
				TEXTURE_SIGNATURE, SUMMON_UNDEAD, SUMMON_UNDEAD_CHANNEL_DURATION);
		spawnedUndead = null;
	}

	@Override
	protected void useSpell() {
		if (spellName.equals(SUMMON_UNDEAD)) {
			useSummonUndead();
			spellName = FIREBALL;
			spellChannelDuration = FIREBALL_CHANNEL_DURATION;
		} else if (spellName.equals(FIREBALL)) {
			useFireball();
			if (spawnedUndead == null || !spawnedUndead.isAlive()) {
				spellName = SUMMON_UNDEAD;
				spellChannelDuration = SUMMON_UNDEAD_CHANNEL_DURATION;
			}
		}
	}

	private void useSummonUndead() {
		Location location = getLocation();
		Location undeadSpawnLocation = location.add(location.getDirection()).add(0, 1, 0);
		spawnedUndead = new FlintonSewersUndead(undeadSpawnLocation, false);
		spawnedUndead.setAlive(true);
		ZOMBIE_SPAWN_NOISE.play(undeadSpawnLocation);
	}

	private void useFireball() {
		double damageAmount = 10;
		double range = 15;
		Location start = getLocation();
		Vector lookDirection = start.getDirection();
		start.add(lookDirection).add(0, 0.5, 0);
		FIREBALL_CONJURE_NOISE.play(start);
		Vector velocity = lookDirection.multiply(8);
		// ensure we don't shoot through walls
		Location end = ai.getTargetBlock(null, (int) range).getLocation().add(0.5, 0.5, 0.5);
		double maxDistance = start.distance(end);
		double hitSize = 0.75;
		World world = start.getWorld();
		Fireball fireball = (Fireball) BukkitUtility.spawnNonpersistentEntity(start, EntityType.FIREBALL);
		Projectile projectile = new Projectile(start, velocity, maxDistance, hitSize) {
			// -40 to account for error
			boolean explode = start.distanceSquared(end) < range * range - 40;
			// used to ensure that the fireball only explodes for the first target hit
			boolean hasHitTarget = false;

			@Override
			protected void onHit(Collider hit) {
				if (hasHitTarget) {
					return;
				}
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!isFriendly(character)) {
						explode = true;
						hasHitTarget = true;
						this.remove();
					}
				}
			}

			@Override
			public void remove() {
				super.remove();
				fireball.remove();
				Location location = getLocation();
				if (explode) {
					FIREBALL_EXPLODE_1_NOISE.play(location);
					world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
					for (int i = 0; i < 5; i++) {
						world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 1);
					}
					new DelayedTask(1) {
						@Override
						protected void run() {
							FIREBALL_EXPLODE_2_NOISE.play(getLocation());
						}
					}.schedule();
					Collider hitbox = new Collider(getLocation(), 2, 2, 2) {
						@Override
						protected void onCollisionEnter(Collider other) {
							if (other instanceof CharacterCollider) {
								AbstractCharacter character = ((CharacterCollider) other).getCharacter();
								if (!isFriendly(character)) {
									character.damage(damageAmount, FlintonSewersNecromancer.this);
								}
							}
						}
					};
					hitbox.setActive(true);
					hitbox.setActive(false);
				}
			}
		};
		projectile.fire();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Location location = getLocation();
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.CULLING_THE_CULT.getObjective(0).addProgress(pc, 1);
		}
		Items.CONJURERS_CLOAK.drop(location, 0.05);
		Items.CONJURERS_HOOD.drop(location, 0.05);
		Items.CONJURERS_LEGGINGS.drop(location, 0.05);
		Items.CONJURERS_SHOES.drop(location, 0.05);
		Items.POTION_OF_LESSER_HEALING.drop(location, 0.05);
		Items.SKELETAL_WAND.drop(location, 0.05);
	}

}
