package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacterInteractionCollider;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Quests;

public class MelcherAngeredDrunkard extends AbstractHumanEnemy {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 50;
	private static final double DAMAGE_AMOUNT = 8;
	private static final int XP_REWARD = 25;
	private static final double RESPAWN_TIME = 30;
	private static final int SPEED = 1;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTA4ODI1OTM3NSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjc3MzRlNTU0MzE2ZTMwZGI4MzQ5YmRjN2M3ODY3YTgxNDg0YTQxYmIxZWRjZjE3ZWY2NGJjZTVlMDRhODRjNSIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "fRxtv87YSpnmToP4oIUy9iIGWcFQVccMDJ7Z+RCj2kG84fbbdz/50s/XC/fl8DTDEEEorIPfw5VGmEQykDwf4eD0iB2NPiPgmvfF0NVMUpiOQUXpGU68XBmI/5o5tKyQI0D9IeRdNah3xw938jxKx8QPqebifbCuRux4LFatE3nspSMwqO/dh1oBUbxHGAAetVnVTeRIe9qgveOFhW5hZS49rLAPn6LzpScdPlsVY7eX1mpiNDYwdTbynXsZZw+Gf4n3gde6Khf2klwKwE+cWgLIkvH9N2r0cefxixgkRT16CuTkkT72LiJbRZNhG3np2E0PeuQPtVmBleru874WxFWdMLEL1L4cuVWbWL2CWd+uCui/+to1zRyE+Ul1diFr+bRvwnjcvOhXBLaBi/Np/bSREPQPZbTSUMh7Ed6Th4Ohf8tpdJItkmS2De7RwuJYxj+EXgVgIOuoAdiiQZ73KcuKwFqVuBq0X/W/iXtizpJQP7YHVPFwm1+ACA8jWza10pPAOdnfNgxrbdkU9/g0SOFrl5oJkZE86RhzZiRLzk5VY12kxAl+KR3PmeF2HjiAo/ebuStYd3pkWXk5+SNN9OY3ngIdJ8F/Lt0xjroGsPjti93oxbRawlAlgjmTVmwNsHBde5Ucc3op0/wDJk50RHpBAMw+pBl1sPgYMJ1DgZU=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_PILLAGER_AMBIENT);

	private final PlayerCharacterInteractionCollider interactionCollider;
	private final InteractionSequence enrageInteraction;
	private Cow passiveAi;

	public MelcherAngeredDrunkard(Location spawnLocation) {
		super(ChatColor.YELLOW + "Angered Drunkard", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD,
				RESPAWN_TIME, SPEED, TEXTURE_DATA, TEXTURE_SIGNATURE);
		enrageInteraction = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("What's your problem?", pc);
					break;
				case 1:
					say("What do you mean calm down? Nobody tells me what to do!", pc);
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
				MelcherAngeredDrunkard.this.onInteract(pc);
			}
		};
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		interactionCollider.setCenter(location.clone().add(0, 1, 0));
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
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.CALMING_THE_TAVERN.getObjective(0).complete(pc);
		}
	}

	@Override
	public boolean isFriendly(AbstractCharacter other) {
		return !isEnraged();
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

	protected void onInteract(PlayerCharacter pc) {
		if (Quests.CALMING_THE_TAVERN.compareStatus(pc, QuestStatus.IN_PROGRESS) && !isEnraged()) {
			enrageInteraction.advance(pc);
		} else {
			say("Buzz off, would ya?", pc);
		}
	}

	private boolean isEnraged() {
		return ai.hasAI();
	}

	private void setEnraged(boolean enraged) {
		ai.setAI(enraged);
		interactionCollider.setActive(!enraged);
		if (enraged) {
			aiSyncer.setEntity(ai);
			passiveAi.remove();
			setName(ChatColor.RED + "Angered Drunkard");
		} else {
			setName(ChatColor.YELLOW + "Angered Drunkard");
			passiveAi = (Cow) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.COW);
			passiveAi.addPotionEffect(INVISIBILITY);
			passiveAi.setSilent(true);
			passiveAi.setCollidable(false);
			passiveAi.setInvulnerable(true);
			passiveAi.eject();
			Entity vehicle = passiveAi.getVehicle();
			if (vehicle != null) {
				vehicle.remove();
			}
			passiveAi.setAdult();
			passiveAi.setRemoveWhenFarAway(false);
			passiveAi.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 128));
			aiSyncer.setEntity(passiveAi);
		}
	}

}
