package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Quests;

public class MelcherAngeredDrunkard extends AbstractHumanEnemy {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 100;
	private static final double DAMAGE_AMOUNT = 8;
	private static final double RESPAWN_TIME = 30;
	private static final int SPEED = 1;
	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_PILLAGER_AMBIENT);

	private final PlayerCharacterInteractionCollider interactionCollider;
	private final InteractionSequence interaction;

	public MelcherAngeredDrunkard(Location spawnLocation) {
		super(ChatColor.YELLOW + "Angered Drunkard", LEVEL, spawnLocation, RESPAWN_TIME, SPEED, TEXTURE_DATA,
				TEXTURE_SIGNATURE);
		interaction = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("What's your problem?", pc);
					SPEAK_NOISE.play(pc);
					break;
				case 1:
					say("What do you mean calm down? Nobody tells me what to do!", pc);
					SPEAK_NOISE.play(pc);
					break;
				case 2:
					setEnraged(true);
					break;
				}
			}
		};
		interactionCollider = new PlayerCharacterInteractionCollider(spawnLocation.clone().add(0, 1, 0), 1, 2, 1) {
			@Override
			protected void onInteract(PlayerCharacter pc) {
				if (Quests.CALMING_THE_TAVERN.compareStatus(pc, QuestStatus.IN_PROGRESS) && !isEnraged()) {
					interaction.advance(pc);
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
		PlayerCharacter[] nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.CALMING_THE_TAVERN.getObjective(0).complete(pc);
		}
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return !isEnraged();
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
