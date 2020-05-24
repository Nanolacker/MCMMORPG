package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.common.ui.TextPanel;
import com.mcmmorpg.common.utils.MathUtils;
import com.mcmmorpg.impl.Quests;
import com.mcmmorpg.impl.Worlds;

public class FlintonSewersXylphanos extends AbstractCultist {

	private static final int LEVEL = 15;
	private static final double MAX_HEALTH = 1000;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTk5NzQ4MDk3NCwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTVkNTg3NjhmYjJlNDMyN2I4NWVkMDAxODY0NmUxYTBlNGIzYzhiYmIwY2ViZDAyYWM2YzI5NjBjYjQzNzNkYiIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "vRomneBuiAl7MB+6+kpoS/a42CIISA6sdBNoJMgOyoDuu7jA/h+mZRlScAtIRbn897BIeowRRIfCZlOcgb51saLk0ELIKkOV83nh9KyimTNSjP99t7BxCC+Rf/TQUdzTuLwC1S4GdF1KZ7NDfMlOuyslCgRl5YEo03Czcr9zk5Ff92gCGzksuLMiwuill+pPFeMFxZ2BT6tQ9ysJ7Ev+PP+c4fwGJ/JRRKsYxjGx/VkVCk/LnnQEL40DvQC30ztHpN7QuvMfpnpa37OZMsy8MdKgdVTw0O+PWWSMU4AdYHIQTKjDsdpr8tnygi1t9TmIEXaLQzrQhtvlyf9O2+xdh49EXDU56DD9TNYb9/K12QRQK1O2z2NGed1Ve/UwrVSHv829+YVrr820g0KI47pJRlZDE2mbL4FKrGGdkp28YsCfyvk+czNPwFvKA8vqj5Cg8l7K7MNe4HjQ5JH+kZFE78D/NhlUntRIj7abNC8jMuoOlnmiwYkaZqixuSQa9Of9OwmRijnPfmX+ArUgvLDYCAtPG8wdVdduEqRdzuL2sZqr54KCkixPl1V/xlpAscZLh1g7cOjohn4qcoGx9xxyrsIixjsFhwOUreuXND331/qUhqjNPEflEYnSaUa801xygT3IMmNQzfP+gLEHGLfe5A9bOfP2vTSD3Wn/+DDYg24=";
	private static final double FIELD_OF_DEATH_CHANNEL_DURATION = 5;
	private static final double FIELD_OF_DEATH_CHARGE_DURATION = 4;
	private static final double FIELD_OF_DEATH_WIDTH = 15;
	private static final double FIELD_OF_DEATH_HEIGHT = 1;
	private static final double FIELD_OF_DEATH_DAMAGE = 150;
	private static final Particle FIELD_OF_DEATH_PARTICLE = Particle.SPELL_WITCH;
	private static final Noise FIELD_OF_DEATH_CHANNEL_NOISE = new Noise(Sound.BLOCK_PORTAL_TRIGGER);
	private static final Noise FIELD_OF_DEATH_EXPLODE_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);
	private static final Noise TELEPORT_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);
	private static final double TELEPORT_SPEED = 10;

	private final Collider surroundings;
	private final BossBar bossBar;
	private final InteractionSequence completeBattleInteraction;
	private boolean passive;

	public FlintonSewersXylphanos(Location spawnLocation) {
		super(ChatColor.RED + "Xylphanos", LEVEL, spawnLocation, MAX_HEALTH, TEXTURE_DATA, TEXTURE_SIGNATURE,
				"Field of Death", FIELD_OF_DEATH_CHANNEL_DURATION);
		surroundings = new Collider(getLocation(), 50, 10, 50) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					bossBar.addPlayer(pc.getPlayer());
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					bossBar.removePlayer(pc.getPlayer());
				}
			}
		};
		completeBattleInteraction = new InteractionSequence(2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("dialogue", 50);
					break;
				case 1:
					Location location = getLocation();
					List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 40);
					for (PlayerCharacter nearbyPc : nearbyPcs) {
						Quests.CULLING_THE_CULT.getObjective(2).complete(nearbyPc);
					}
					TELEPORT_NOISE.play(location);
					// so players don't see death
					setLocation(location.subtract(0, 32, 0));
					setAlive(false);
					passive = false;
					new DelayedTask(respawnTime) {
						@Override
						protected void run() {
							setAlive(true);
						}
					}.schedule();
					break;
				}
			}
		};
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);

	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		surroundings.setCenter(location);
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtils.clamp(currentHealth / getMaxHealth(), 0.0, 1.0);
		bossBar.setProgress(progress);
	}

	@Override
	public void damage(double amount, Source source) {
		if (getCurrentHealth() - amount <= 0) {
			passive = true;
			spellProgressBar.dispose();
			setCurrentHealth(getMaxHealth());
			hitbox.setActive(false);
			aiSyncer.setEnabled(false);
			ai.remove();
			List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 40);
			for (PlayerCharacter pc : nearbyPcs) {
				completeBattleInteraction.advance(pc);
				return;
			}
			return;
		}
		super.damage(amount, source);
		if (Math.random() > 0.6) {
			TELEPORT_NOISE.play(getLocation());
			Vector velocity = new Vector(Math.random() - 0.5, 0, Math.random() - 0.5);
			velocity.multiply(TELEPORT_SPEED);
			ai.setVelocity(velocity);
		}
	}

	@Override
	protected void onLive() {
		super.onLive();
		bossBar.setProgress(1);
		passive = false;
	}

	@Override
	protected void spawn() {
		super.spawn();
		surroundings.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		surroundings.setActive(false);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		surroundings.setActive(false);
	}

	@Override
	protected void chargeSpell() {
		if (passive) {
			return;
		}
		super.chargeSpell();
		List<Player> players = bossBar.getPlayers();
		for (Player player : players) {
			spellProgressBar.display(player);
		}
	}

	@Override
	protected void useSpell() {
		Entity target = ai.getTarget();
		Location targetLocation = target.getLocation();
		TextPanel text = new TextPanel(targetLocation.clone().add(0, 2, 0), "Field of Death");
		text.setVisible(true);
		FIELD_OF_DEATH_CHANNEL_NOISE.play(targetLocation);
		Collider hitbox = new Collider(targetLocation.add(0, FIELD_OF_DEATH_HEIGHT / 2, 0), FIELD_OF_DEATH_WIDTH,
				FIELD_OF_DEATH_HEIGHT, FIELD_OF_DEATH_WIDTH) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) other).getCharacter();
					if (!isFriendly(character)) {
						character.damage(FIELD_OF_DEATH_DAMAGE, FlintonSewersXylphanos.this);
					}
				}
			}
		};
		double period = 0.5;
		new RepeatingTask(period) {
			int count = 0;

			@Override
			protected void run() {
				if (count * period >= FIELD_OF_DEATH_CHARGE_DURATION) {
					FIELD_OF_DEATH_EXPLODE_NOISE.play(targetLocation);
					hitbox.setActive(true);
					hitbox.setActive(false);
					text.setVisible(false);
					cancel();
					return;
				}
				createFieldOfDeathParticleEffect(targetLocation, count);
				count++;
			}
		}.schedule();
	}

	private void createFieldOfDeathParticleEffect(Location location, int particleDensity) {
		int particleCount = (int) (particleDensity * FIELD_OF_DEATH_WIDTH * FIELD_OF_DEATH_WIDTH
				* FIELD_OF_DEATH_HEIGHT);
		Location corner = location.clone().subtract(FIELD_OF_DEATH_WIDTH / 2, FIELD_OF_DEATH_HEIGHT / 2,
				FIELD_OF_DEATH_WIDTH / 2);
		for (int i = 0; i < particleCount; i++) {
			double offsetX = Math.random() * FIELD_OF_DEATH_WIDTH;
			double offsetY = Math.random() * FIELD_OF_DEATH_HEIGHT;
			double offsetZ = Math.random() * FIELD_OF_DEATH_WIDTH;
			Vector offset = new Vector(offsetX, offsetY, offsetZ);
			Location particleLocation = corner.clone().add(offset);
			Worlds.ELADRADOR.spawnParticle(FIELD_OF_DEATH_PARTICLE, particleLocation, 1);
		}
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter) || passive;
	}

}
