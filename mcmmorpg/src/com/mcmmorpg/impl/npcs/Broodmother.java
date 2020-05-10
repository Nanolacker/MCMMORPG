package com.mcmmorpg.impl.npcs;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.EntityType;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.utils.MathUtils;

public class Broodmother extends AbstractSpider {

	private static final int LEVEL = 5;
	private static final int SPEED = 2;
	private static final double MAX_HEALTH = 300;
	private static final double DAMAGE_AMOUNT = 12;
	private final BossBar bossBar;

	public Broodmother(Location spawnLocation) {
		super(ChatColor.RED + "Broodmother", LEVEL, spawnLocation, EntityType.SPIDER, SPEED);
		bossBar = Bukkit.createBossBar(getName(), BarColor.RED, BarStyle.SEGMENTED_10);
		super.setHeight(1.5);

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
