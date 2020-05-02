package com.mcmmorpg.impl.npcs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.utils.MathUtils;

public class Broodmother extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final double MAX_HEALTH = 300;
	private static final double DAMAGE_AMOUNT = 12;
	private static final PotionEffect SPEED = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2);

	private final BossBar bossBar;
	private final Collider surroundings;
	private PlayerCharacter target;

	public Broodmother(Location spawnLocation) {
		super(ChatColor.RED + "Broodmother", LEVEL, spawnLocation);
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);
		surroundings = new Collider(spawnLocation, 30, 6, 30) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					Player player = ((PlayerCharacterCollider) other).getCharacter().getPlayer();
					bossBar.addPlayer(player);
					if (target == null) {
						target = PlayerCharacter.forPlayer(player);
						entity.setTarget(target.getPlayer());
					}
				}
			}

			@Override
			protected void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					Player player = ((PlayerCharacterCollider) other).getCharacter().getPlayer();
					bossBar.removePlayer(player);
					PlayerCharacter pc = PlayerCharacter.forPlayer(player);
					if (pc == target) {
						Collider[] colliders = surroundings.getCollidingColliders();
						for (int i = 0; i < colliders.length; i++) {
							Collider collider = colliders[i];
							if (collider instanceof PlayerCharacterCollider) {
								target = ((PlayerCharacterCollider) collider).getCharacter();
								entity.setTarget(target.getPlayer());
								return;
							}
						}
						target = null;
					}
				}
			}
		};
	}

	@Override
	protected void spawn() {
		super.spawn();
		entity.addPotionEffect(SPEED);
		surroundings.setActive(true);
	}

	@Override
	protected void despawn() {
		super.despawn();
		surroundings.setActive(false);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		surroundings.setCenter(location.clone().add(0, 2, 0));
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtils.clamp(currentHealth / getMaxHealth(), 0.0, 1.0);
		bossBar.setProgress(progress);
	}

	@Override
	protected void onLive() {
		super.onLive();
		bossBar.setProgress(1);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		surroundings.setActive(false);
	}

	@Override
	protected double maxHealth() {
		return MAX_HEALTH;
	}

	@Override
	protected double damageAmount() {
		return DAMAGE_AMOUNT;
	}

}
