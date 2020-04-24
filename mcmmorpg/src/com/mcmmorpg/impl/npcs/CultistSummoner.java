package com.mcmmorpg.impl.npcs;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.event.CharacterDeathEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;

public class CultistSummoner extends AbstractCultist {

	private static final Noise ZOMBIE_SPAWN_NOISE = new Noise(Sound.BLOCK_FIRE_EXTINGUISH);

	private Set<BulskanUndead> spawnedUndead;

	public CultistSummoner(Location spawnLocation) {
		super(ChatColor.RED + "Cultist Summoner", spawnLocation, "Summon Undead", 5);
		spawnedUndead = new HashSet<>();
		Listener listener = new Listener() {
			@EventHandler
			private void onUndeadDeath(CharacterDeathEvent event) {
				AbstractCharacter character = event.getCharacter();
				if (spawnedUndead.contains(character)) {
					spawnedUndead.remove(character);
				}
			}
		};
		EventManager.registerEvents(listener);
	}

	@Override
	protected void useSpell() {
		if (spawnedUndead.size() >= 3) {
			return;
		}
		Location location = getLocation();
		Location undeadSpawnLocation = location.add(location.getDirection().multiply(2));
		BulskanUndead undead = new BulskanUndead(undeadSpawnLocation, false);
		undead.setAlive(true);
		spawnedUndead.add(undead);
		ZOMBIE_SPAWN_NOISE.play(undeadSpawnLocation);
	}

}
