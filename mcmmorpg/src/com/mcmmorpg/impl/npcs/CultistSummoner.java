package com.mcmmorpg.impl.npcs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.CharacterDeathEvent;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Quests;

public class CultistSummoner extends AbstractCultist {

	private static final int LEVEL = 10;
	private static final double MAX_HEALTH = 100;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODcyNTQ0MzcxMzUsInByb2ZpbGVJZCI6ImRlNTcxYTEwMmNiODQ4ODA4ZmU3YzlmNDQ5NmVjZGFkIiwicHJvZmlsZU5hbWUiOiJNSEZfTWluZXNraW4iLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Q5NmM4NjA3NWFhOTJiYjAwYmI2NzZlNzQ5MWM5NWUxYzY5YjU0ZjlmNzY3MzU1MjlhMGY2NmUwNGQzZDI1ZmUifX19";
	private static final String TEXTURE_SIGNATURE = "uks+6CegMiDE3DNcnJ5ZMf0iA6AtGTGUkncv9DbukLgzAYp9gmgWsm0TKaRbtOcH9TSWNYid2jr2XyezYwIxqDwZGdYLno2cqtdjwE+EzPhhvGZX5YkEHwyQtcRiPp1Yz1Mp5XBFfBPfAa6p+YTw9ry5+V4cEGfoxuxFZ3LZny8MngLnVuNro80H17Hb1QNzCSoJ224z3M9J5thNs5gliz9KO1cotbd4g9ejiBF8u+OgpU57U+0steLy8MyTGtJw1vfiRnmZ69a73BjwYkM+BIhGpR63N9Zt3GcJIn56Uwpn1ACFjHIyzVjjKrM6XTH/pXy/GQ277cmaULKBQuL0ryNb1EeLV6gkNfEqH+DFUy4wPSBnR9hKaLMP8hXJaV/JkQJk/AzzGItqyxNk8j3YzQjqwOvzJkUDUvf+rNqJMp9dZ6ZmQlH6jnvTxRlLFnTpgd0T00qjrRqQaHgE2bWsVkftsGJBg46uXKOBl/togm+1cIsHO3FfXB9gCbgBIFMYdm3rkHqc0h0otLyefd5qBZmxZQmbNv3FWCWUx/STmCVmfpZCsMu8JgWmvTRoFT2HqQEVx/uUn5zQI/EK1EXbLk+BzUBueYVDDsH5cz0fpKuUDxHI7lB4YVFfASN5kzvwNl8pNQEq02HYI5RFDsVrB0OoFFhm0EqKTI04VeYDx9M=";

	private static final Noise ZOMBIE_SPAWN_NOISE = new Noise(Sound.BLOCK_FIRE_EXTINGUISH);

	private Set<BulskanUndead> spawnedUndead;

	public CultistSummoner(Location spawnLocation) {
		super(ChatColor.RED + "Cultist Summoner", LEVEL, spawnLocation, MAX_HEALTH, TEXTURE_DATA, TEXTURE_SIGNATURE,
				"Summon Undead", 3);
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

	@Override
	protected void onDeath() {
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.CULLING_THE_CULT.getObjective(1).addProgress(pc, 1);
		}
	}

}
