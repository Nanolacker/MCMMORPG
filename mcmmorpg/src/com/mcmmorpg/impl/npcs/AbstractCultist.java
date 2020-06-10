package com.mcmmorpg.impl.npcs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Zombie;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.ui.ProgressBar;
import com.mcmmorpg.common.ui.ProgressBar.ProgressBarColor;

public abstract class AbstractCultist extends AbstractHumanEnemy {

	private static final double RESPAWN_TIME = 60;
	private static final double SPELL_RECHARGE_TIME = 1;
	private static final int SPEED = 2;
	protected static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5,
			false);

	private static final List<AbstractCultist> cultists = new ArrayList<>();

	protected String spellName;
	protected double spellChannelDuration;
	protected boolean spellIsRecharging;
	protected ProgressBar spellProgressBar;

	static {
		RepeatingTask aggroTask = new RepeatingTask(1) {
			@Override
			protected void run() {
				for (int i = 0; i < cultists.size(); i++) {
					AbstractCultist cultist = cultists.get(i);
					if (!cultist.isSpawned()) {
						continue;
					}
					Zombie ai = cultist.ai;
					Entity target = ai.getTarget();
					if (target == null || !ai.hasLineOfSight(target)) {
						if (cultist.isChannellingSpell()) {
							cultist.cancelSpell();
						}
					} else {
						if (!cultist.isChannellingSpell() && !cultist.spellIsRecharging) {
							cultist.chargeSpell();
						}
					}
				}
			}
		};
		aggroTask.schedule();
	}

	public AbstractCultist(String name, int level, Location spawnLocation, double maxHealth, int xpReward,
			String textureData, String textureSignature, String spellName, double spellChannelDuration) {
		super(name, level, spawnLocation, maxHealth, 0, xpReward, RESPAWN_TIME, SPEED, textureData, textureSignature);
		this.spellName = spellName;
		this.spellChannelDuration = spellChannelDuration;
		this.spellIsRecharging = false;
		cultists.add(this);
	}

	private final boolean isChannellingSpell() {
		return spellProgressBar != null && spellProgressBar.getRate() != 0;
	}

	protected void chargeSpell() {
		ai.addPotionEffect(SLOW_EFFECT);
		spellProgressBar = new ProgressBar(spellName, ProgressBarColor.WHITE) {
			@Override
			protected void onComplete() {
				useSpell();
				spellIsRecharging = true;
				new DelayedTask(SPELL_RECHARGE_TIME) {
					@Override
					protected void run() {
						spellIsRecharging = false;
					}
				}.schedule();
			}
		};
		spellProgressBar.setRate(1 / spellChannelDuration);
		spellProgressBar.display(getLocation().add(0, 2.75, 0));
	}

	protected void cancelSpell() {
		ai.removePotionEffect(PotionEffectType.SLOW);
		if (spellProgressBar != null) {
			spellProgressBar.dispose();
		}
	}

	protected abstract void useSpell();

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		if (spellProgressBar != null) {
			spellProgressBar.display(getLocation().add(0, 2.75, 0));
		}
	}

	@Override
	protected void despawn() {
		super.despawn();
		cancelSpell();
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		cancelSpell();
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return !(other instanceof PlayerCharacter);
	}

}
