package com.mcmmorpg.impl.listeners;

import java.io.File;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.PersistentSoundSequenceDataContainer;
import com.mcmmorpg.common.sound.SoundSequence;
import com.mcmmorpg.common.ui.TitleMessage;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.impl.Worlds;
import com.mcmmorpg.impl.npcs.BulskanUndead;

public class BulskanRuinsListener implements Listener {

	private static String ZONE_NAME = "Bulskan Ruins";

	private SoundSequence soundtrack;

	public BulskanRuinsListener() {
		getSoundtrack();
		setUpBounds();
		spawnNpcs();
	}

	private void getSoundtrack() {
		File file = new File(IOUtils.getDataFolder(), "resources\\soundtracks\\bulskan_ruins.json");
		SoundSequence soundtrack = IOUtils.objectFromJsonFile(file, PersistentSoundSequenceDataContainer.class)
				.toSoundSequence();
		this.soundtrack = soundtrack;
	}

	private void setUpBounds() {
		TitleMessage entranceMessage = new TitleMessage(ChatColor.GRAY + "Bulskan Ruins", ChatColor.GOLD + "Level 5");
		TitleMessage exitMessage = new TitleMessage(ChatColor.GREEN + "Melcher", ChatColor.GOLD + "Level 1");
		Noise entranceNoise = new Noise(Sound.AMBIENT_CAVE);

		Collider entranceCollider = new Collider(Worlds.ELADRADOR, 135, 65, -57, 207, 85, -17) {
			@Override
			public void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					if (pc.getZone().equals(ZONE_NAME)) {
						return;
					}
					pc.setZone(ZONE_NAME);
					Player player = pc.getPlayer();
					entranceMessage.sendTo(player);
					entranceNoise.play(player);
					pc.getSoundTrackPlayer().setSoundtrack(soundtrack);
				}
			}
		};

		Collider exitCollider = new Collider(Worlds.ELADRADOR, 129, 0, -63, 210, 85, -13) {
			@Override
			public void onCollisionExit(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					if (!pc.getZone().equals(ZONE_NAME)) {
						return;
					}
					pc.setZone("Melcher");
					pc.getSoundTrackPlayer().setSoundtrack(null);
					Player player = pc.getPlayer();
					exitMessage.sendTo(player);
				}
			}
		};

		entranceCollider.setActive(true);
		exitCollider.setActive(true);
	}

	private void spawnNpcs() {
		Location[] undeadLocations = { new Location(Worlds.ELADRADOR, 174, 67, -35),
				new Location(Worlds.ELADRADOR, 170, 67, -33) };
		for (Location location : undeadLocations) {
			BulskanUndead undead = new BulskanUndead(4, location, true, 30);
			undead.setAlive(true);
		}
	}

	@EventHandler
	private void onButtonPress(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		// Block clicked = event.getClickedBlock();
		// Location location = clicked.getLocation();
		// if (location == null) {
		// Lever lever = (Lever) clicked;
		// lever.setPowered(true);
		// }
	}

	@EventHandler
	private void onLevel1(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		Quest quest = Quest.forName("Saving the Farm");
		quest.start(pc);
		pc.setHealthRegenRate(50);
		pc.setMaxHealth(5000);
	}

}
