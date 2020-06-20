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
import com.mcmmorpg.common.util.MathUtility;
import com.mcmmorpg.impl.constants.Quests;
import com.mcmmorpg.impl.constants.Worlds;

public class FlintonSewersXylphanos extends AbstractCultist {

	private static final int LEVEL = 15;
	private static final double MAX_HEALTH = 6000;
	private static final int XP_REWARD = 250;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MjAyMzE3ODY4MywKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmMwZTI5YmY0ZmQ5OWIxNGE3ZTQwMmVkZjNmOTczMTJkMjEwMmY0NzZkM2VjMjIwNzMzMDg2MDE2OTk4MDI4ZCIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "ZAhfJZ4HefIBAKPC540XuzKR8YvlFodjihQIphOI/sA5RsCEM9v6xvArxVIZoFCgzpBrzF3NfA8WBq5aqo7+brJvaNudrWlsMYMO9p1XvWLCw/+GZeOwa62uWCqxF2l6NRcl6uAbXIhvKNW7JLhBTLT0dRiL8sRN4qs0ySWo4jA+VitsQnvaMskXOY5eoG+TKzMqy0RqEy4zwvEubKIkcgDXjTwlAmvzb0fGhS/7KRN/jyNsoj58sBL1pA+UOrMS5EKb6cHdbX4A7x+fV4rEUKZQyx6T76ukUxzDJtFruTVNaKaMy5lHApUnOarKFf4Sh6X30SI3JBhXFA6aMUtd4uDHzio7zMTZ17MNLdu+HL4eg43WUgErKcQXIZDo2ikAQhlz3ltuV9NTU6HQNtYacfX0RRPJHpyxEcuzUHE5bLt4iz2ztWLvLJzo3xyS0uQB8Fr4OigJaulQHxx8ZMQd9DD7BSgQ3ZI4NO0LSdzFl945a4S95JGqELhMkU4yPtb20w+QSdInDM+I4UPAI9RT7PpRvfVp/kHiLg8IUb0tygbolBhqAngUu3eS3ulDW4UwdE+pXAo8nVczHIsUgt4rw2hSTP+yA4WFkXj/MlkKYgSppZe8UQ+h+GxDjvzAt9ve9zRqI62303ynthTdHadFWTNNvlBEcr9dUQznwxbtMi0=";
	private static final String FIELD_OF_DEATH = "Field of Death";
	private static final double FIELD_OF_DEATH_CHANNEL_DURATION = 4;
	private static final double FIELD_OF_DEATH_WIDTH = 15;
	private static final double FIELD_OF_DEATH_HEIGHT = 1;
	private static final double FIELD_OF_DEATH_DAMAGE = 50;
	private static final Particle FIELD_OF_DEATH_PARTICLE = Particle.SPELL_WITCH;
	private static final Noise FIELD_OF_DEATH_CHANNEL_NOISE = new Noise(Sound.BLOCK_PORTAL_TRIGGER);
	private static final Noise FIELD_OF_DEATH_EXPLODE_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);
	private static final Noise TELEPORT_NOISE = new Noise(Sound.ENTITY_WITHER_HURT);
	private static final double TELEPORT_SPEED = 7;
	private static final String[] BATTLE_DIALOGUE_OPTIONS = { "You think vermin like you can stop me?" };
	private static final double DIALOGUE_PERIOD = 10;

	private final Collider surroundings;
	private final BossBar bossBar;
	private final InteractionSequence completeBattleInteraction;
	private boolean passive;
	private final TextPanel fieldOfDeathTextPanel;
	private final RepeatingTask fieldOfDeathParticleTask;
	private Location targetLocation;

	public FlintonSewersXylphanos(Location spawnLocation) {
		super(ChatColor.RED + "Xylphanos", LEVEL, spawnLocation, MAX_HEALTH, XP_REWARD, TEXTURE_DATA, TEXTURE_SIGNATURE,
				FIELD_OF_DEATH, FIELD_OF_DEATH_CHANNEL_DURATION);
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
		completeBattleInteraction = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					speak("Enough of this child's play.", 25);
					break;
				case 1:
					speak("You seek to stop my rise to power? My path to creating a just and forgiving world?", 25);
					break;
				case 2:
					speak("The current god of death is unworthy. She will be usurped, and replaced by someone who understands mortal grief and pain.",
							25);
					break;
				case 3:
					speak("How I reach this is none of your concern, you ignorant worm. My plans are for the greater good.",
							25);
					break;
				case 4:
					speak("I am Xylphanos. Remember my name, for it shall be celebrated once I am the new merciful god of death.",
							25);
					break;
				case 5:
					speak("Do not interfere more, lest you be known as the one who prolonged humanity's suffering.", 25);
					break;
				case 6:
					Location location = getLocation();
					List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 40);
					for (PlayerCharacter nearbyPc : nearbyPcs) {
						Quests.CULLING_THE_CULT.getObjective(1).complete(nearbyPc);
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
		RepeatingTask dialogueTask = new RepeatingTask(DIALOGUE_PERIOD) {
			int dialogueIndex = 0;

			@Override
			protected void run() {
				if (!passive) {
					String dialogue = BATTLE_DIALOGUE_OPTIONS[dialogueIndex];
					speak(dialogue, 20);
					dialogueIndex++;
					if (dialogueIndex == BATTLE_DIALOGUE_OPTIONS.length) {
						dialogueIndex = 0;
					}
				}
			}
		};
		dialogueTask.schedule();
		fieldOfDeathTextPanel = new TextPanel(spawnLocation, "Field of Death");
		fieldOfDeathParticleTask = new RepeatingTask(0.5) {
			int count = 0;

			@Override
			public void schedule() {
				super.schedule();
				count = 0;
			}

			@Override
			protected void run() {
				createFieldOfDeathParticleEffect(targetLocation, count);
				count++;
			}
		};
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		surroundings.setCenter(location);
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtility.clamp(currentHealth / getMaxHealth(), 0.0, 1.0);
		bossBar.setProgress(progress);
	}

	@Override
	public void damage(double amount, Source source) {
		if (getCurrentHealth() - amount <= 0) {
			passive = true;
			cancelSpell();
			ai.addPotionEffect(SLOW_EFFECT);
			for (int i = 1; i <= 8; i++) {
				double health = MAX_HEALTH / 8 * i;
				new DelayedTask(i * 0.25) {
					@Override
					protected void run() {
						setCurrentHealth(health);
					}
				}.schedule();
			}
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
		if (Math.random() < 0.6) {
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
	protected void chargeSpell() {
		if (passive) {
			return;
		}
		super.chargeSpell();
		List<Player> players = bossBar.getPlayers();
		for (Player player : players) {
			spellProgressBar.display(player);
		}

		Entity target = ai.getTarget();
		targetLocation = target.getLocation();
		fieldOfDeathTextPanel.setLocation(targetLocation.clone().add(0, 2, 0));
		fieldOfDeathTextPanel.setVisible(true);
		FIELD_OF_DEATH_CHANNEL_NOISE.play(targetLocation);
		fieldOfDeathParticleTask.schedule();
	}

	protected void useSpell() {
		fieldOfDeathParticleTask.cancel();
		fieldOfDeathTextPanel.setVisible(false);
		FIELD_OF_DEATH_EXPLODE_NOISE.play(targetLocation);
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
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	@Override
	protected void cancelSpell() {
		super.cancelSpell();
		if (fieldOfDeathParticleTask.isScheduled()) {
			fieldOfDeathParticleTask.cancel();
		}
		fieldOfDeathTextPanel.setVisible(false);
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
