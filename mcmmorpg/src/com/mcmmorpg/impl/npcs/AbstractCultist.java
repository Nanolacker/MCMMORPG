package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
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

public abstract class AbstractCultist extends AbstractHumanEnemy {

	private static final double RESPAWN_TIME = 30;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODcyNTQ0MzcxMzUsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q5NmM4NjA3NWFhOTJiYjAwYmI2NzZlNzQ5MWM5NWUxYzY5YjU0ZjlmNzY3MzU1MjlhMGY2NmUwNGQzZDI1ZmUifX19";
	private static final String TEXTURE_SIGNATURE = "uks+6CegMiDE3DNcnJ5ZMf0iA6AtGTGUkncv9DbukLgzAYp9gmgWsm0TKaRbtOcH9TSWNYid2jr2XyezYwIxqDwZGdYLno2cqtdjwE+EzPhhvGZX5YkEHwyQtcRiPp1Yz1Mp5XBFfBPfAa6p+YTw9ry5+V4cEGfoxuxFZ3LZny8MngLnVuNro80H17Hb1QNzCSoJ224z3M9J5thNs5gliz9KO1cotbd4g9ejiBF8u+OgpU57U+0steLy8MyTGtJw1vfiRnmZ69a73BjwYkM+BIhGpR63N9Zt3GcJIn56Uwpn1ACFjHIyzVjjKrM6XTH/pXy/GQ277cmaULKBQuL0ryNb1EeLV6gkNfEqH+DFUy4wPSBnR9hKaLMP8hXJaV/JkQJk/AzzGItqyxNk8j3YzQjqwOvzJkUDUvf+rNqJMp9dZ6ZmQlH6jnvTxRlLFnTpgd0T00qjrRqQaHgE2bWsVkftsGJBg46uXKOBl/togm+1cIsHO3FfXB9gCbgBIFMYdm3rkHqc0h0otLyefd5qBZmxZQmbNv3FWCWUx/STmCVmfpZCsMu8JgWmvTRoFT2HqQEVx/uUn5zQI/EK1EXbLk+BzUBueYVDDsH5cz0fpKuUDxHI7lB4YVFfASN5kzvwNl8pNQEq02HYI5RFDsVrB0OoFFhm0EqKTI04VeYDx9M=";
	private static final PotionEffect SLOW_EFFECT = new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 5,
			false);

	private final String spellName;
	private final double spellChannelDuration;
	private ProgressBar spellProgressBar;

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

	public AbstractCultist(String name, Location spawnLocation, String spellName, double spellChannelDuration) {
		super(name, 10, spawnLocation, RESPAWN_TIME, TEXTURE_DATA, TEXTURE_SIGNATURE);
		this.spellName = spellName;
		this.spellChannelDuration = spellChannelDuration;
	}

	@Override
	protected double maxHealth() {
		return 10;
	}

	@Override
	protected double damageAmount() {
		return 5;
	}

	@Override
	protected int xpToGrantOnDeath() {
		return 10;
	}

	private void chargeSpell() {
		ai.addPotionEffect(SLOW_EFFECT);
		spellProgressBar = new ProgressBar(getLocation().add(0, 2.75, 0), ChatColor.AQUA + spellName, 18,
				ChatColor.AQUA) {
			@Override
			protected void onComplete() {
				useSpell();
				chargeSpell();
			}
		};
		spellProgressBar.setRate(1 / spellChannelDuration);
	}

	protected abstract void useSpell();

	private void cancelSpell() {
		ai.removePotionEffect(PotionEffectType.SLOW);
		if (spellProgressBar != null) {
			spellProgressBar.dispose();
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
