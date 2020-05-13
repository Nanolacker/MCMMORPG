package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class MelcherFarmer extends StaticHuman {

	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODY1MTE0Mzc5MTUsInByb2ZpbGVJZCI6ImMxYWYxODI5MDYwZTQ0OGRhNjYwOWRmZGM2OGEzOWE4IiwicHJvZmlsZU5hbWUiOiJCQVJLeDQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhYTM2NmU2ZGFiMjIzZjk3YTAyNGE3OTg1NTEwMzViMDczMWE5YjA5MmNhNzRkYjBiMmU4ZmE1MDYyZmFjN2EifX19";
	private static final String TEXTURE_SIGNATURE = "yIRmkUscRgF2j0OeOqQ5wEHGeVRzOu9XpmtjVhBZRuMxDi8TLlwI9jvHQFXhEeswJBft2H6XTTanc2Fj4SMUSc77u0wTzVTVpkK0oo63W1NvASBTnAaNx77h544ncc7pNJwufjS2EaYY09VTLmWYkUovoqrsFlik0a7z7z/suSVj8p5wfN4tFC+qcYUM5SCqkISVKvtMDt0MBk4TUdAYNtQbYGdzwHKJQeZvxv46bX2wT/763cpZC0pQo+1ZfMI10ohtSdD0Fp39CoU35Tz8FNF6OEmhG7k3450TPt/utB7QdIqJOVfPdGlYvpVIuFDytMoSxU4wqyoykY42rXwwZo6i9t5em3GDLSIi4bkWcghgtJLvJ4LjxDx0fFe5RcIwmE4f/QqCzUVVj0730I2rUUHeOGS/ttvPXqLKnq/Gnp5tz/D9IO7d8EX7y83v8ALnfRvbqr9CFV3IrxFSxbfsINp7UzL4mJzTroXsOgoEKxDO+kNNnEpcMHfmsK/xOX4HJe8bnk44SFjvEdwX9VytHVFzttpT8hV9ECsPNp5eT9JiRTpizsa2Xq9S2QLW2XEIX3KVBhg6cOLebpCd3ms3nKROToPN9xKgjzSRVtddvU21EeWPxM93HxotBqaeWpS4CewCQ2wJxaa6M0O8BpAIdG18vGHHaT32R/28/TEN7nM=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	public MelcherFarmer(Location location) {
		super(ChatColor.GREEN + "Farmer", 1, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		say("Help me recover some " + Items.FOOD_SUPPLIES.formatName() + " from the bandits.", pc);
		Quests.RECOVERING_THE_FOOD.start(pc);
		SPEAK_NOISE.play(pc);
	}

}
