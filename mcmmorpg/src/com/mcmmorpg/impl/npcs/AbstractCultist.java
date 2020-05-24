package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.ui.ProgressBar;
import com.mcmmorpg.common.ui.ProgressBar.ProgressBarColor;

public abstract class AbstractCultist extends AbstractHumanEnemy {

	private static final int XP_REWARD = 60;
	private static final double RESPAWN_TIME = 60;
	private static final int SPEED = 2;
	private static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5,
			false);

	private final String spellName;
	private final double spellChannelDuration;
	protected ProgressBar spellProgressBar;

	static {
		Listener listener = new Listener() {
			@EventHandler
			private void onTarget(EntityTargetEvent event) {
				Entity targeter = event.getEntity();
				if (aiMap.containsKey(targeter)) {
					AbstractHumanEnemy human = aiMap.get(targeter);
					if (human instanceof AbstractCultist) {
						AbstractCultist cultist = (AbstractCultist) human;
						Entity target = event.getTarget();
						if (target == null) {
							cultist.cancelSpell();
						} else {
							if (target instanceof Player) {
								cultist.chargeSpell();
							}
						}
					}
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	public AbstractCultist(String name, int level, Location spawnLocation, double maxHealth, String textureData,
			String textureSignature, String spellName, double spellChannelDuration) {
		super(name, level, spawnLocation, maxHealth, 0, XP_REWARD, RESPAWN_TIME, SPEED, textureData, textureSignature);
		this.spellName = spellName;
		this.spellChannelDuration = spellChannelDuration;
	}

	protected void chargeSpell() {
		ai.addPotionEffect(SLOW_EFFECT);
		spellProgressBar = new ProgressBar(spellName, ProgressBarColor.WHITE) {
			@Override
			protected void onComplete() {
				useSpell();
				chargeSpell();
			}
		};
		spellProgressBar.setRate(1 / spellChannelDuration);
		spellProgressBar.display(getLocation().add(0, 2.75, 0));
	}

	protected abstract void useSpell();

	private void cancelSpell() {
		ai.removePotionEffect(PotionEffectType.SLOW);
		if (spellProgressBar != null) {
			spellProgressBar.dispose();
		}
	}

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
