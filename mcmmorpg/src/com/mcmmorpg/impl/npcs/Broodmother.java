package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.util.MathUtility;
import com.mcmmorpg.impl.constants.Quests;

public class Broodmother extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final int SPEED = 2;
	private static final double MAX_HEALTH = 1000;
	private static final double DAMAGE_AMOUNT = 12;
	private static final int XP_REWARD = 100;
	private final BossBar bossBar;

	public Broodmother(Location spawnLocation) {
		super(ChatColor.RED + "Broodmother", LEVEL, spawnLocation, EntityType.SPIDER, SPEED, 1.75, 1, 1.75, XP_REWARD);
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);
		super.setHeight(1.5);
	}

	@Override
	public void setCurrentHealth(double currentHealth) {
		super.setCurrentHealth(currentHealth);
		double progress = MathUtility.clamp(currentHealth / getMaxHealth(), 0.0, 1.0);
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
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.ARACHNOPHOBIA.getObjective(1).complete(pc);
		}
	}

	@Override
	protected void onEnterRange(PlayerCharacter pc) {
		super.onEnterRange(pc);
		bossBar.addPlayer(pc.getPlayer());
	}

	@Override
	protected void onExitRange(PlayerCharacter pc) {
		super.onExitRange(pc);
		bossBar.removePlayer(pc.getPlayer());

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
