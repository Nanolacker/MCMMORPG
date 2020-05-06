package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;
import com.mcmmorpg.impl.Quests;

public class MelcherDrunkard extends AbstractHumanEnemy {

	private static final int LEVEL = 2;
	private static final double RESPAWN_TIME = 30;
	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final double MAX_HEALTH = 30;
	private static final double DAMAGE_AMOUNT = 5;

	private final PlayerCharacterInteractionCollider interactionCollider;

	public MelcherDrunkard(Location spawnLocation) {
		super(ChatColor.YELLOW + "Drunkard", LEVEL, spawnLocation, RESPAWN_TIME, TEXTURE_DATA, TEXTURE_SIGNATURE);
		interactionCollider = new PlayerCharacterInteractionCollider(spawnLocation.clone().add(0, 1, 0), 1, 2, 1) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				if (!isEnraged()) {
					setEnraged(true);
				}
			}
		};
	}

	@Override
	protected void spawn() {
		super.spawn();
		setEnraged(false);
	}

	@Override
	protected void despawn() {
		super.despawn();
		interactionCollider.setActive(false);
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		setAlive(true);
		PlayerCharacter pc = null;
		Quests.CALMING_THE_TAVERN.getObjective(0).complete(pc);
	}

	@Override
	protected double maxHealth() {
		return MAX_HEALTH;
	}

	@Override
	protected double damageAmount() {
		return DAMAGE_AMOUNT;
	}

	@Override
	protected int xpToGrantOnDeath() {
		return 0;
	}

	private boolean isEnraged() {
		return ai.hasAI();
	}

	private void setEnraged(boolean enraged) {
		ai.setAI(enraged);
		interactionCollider.setActive(!enraged);
		if (enraged) {
			setName(ChatColor.RED + "Drunkard");
		} else {
			setName(ChatColor.YELLOW + "Drunkard");
		}
	}

}
