package com.mcmmorpg.impl.npcs;

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
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class CultistMage extends AbstractCultist {

	private static final Noise FIREBALL_CONJURE_NOISE = new Noise(Sound.ENTITY_ZOMBIE_VILLAGER_CURE);
	private static final Noise FIREBALL_EXPLODE_1_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE);
	private static final Noise FIREBALL_EXPLODE_2_NOISE = new Noise(Sound.BLOCK_FIRE_AMBIENT);

	public CultistMage(Location spawnLocation) {
		super(ChatColor.RED + "Cultist Mage", spawnLocation, "Fireball", 3);
	}

	@Override
	protected void useSpell() {
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
		Fireball fireball = (Fireball) world.spawnEntity(start, EntityType.FIREBALL);
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
									character.damage(damageAmount, CultistMage.this);
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

}
