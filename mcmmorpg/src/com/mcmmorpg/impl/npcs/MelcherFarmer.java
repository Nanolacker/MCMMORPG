package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.navigation.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.constants.Items;
import com.mcmmorpg.impl.constants.Quests;

public class MelcherFarmer extends StaticHuman {

	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODY1MTE0Mzc5MTUsInByb2ZpbGVJZCI6ImMxYWYxODI5MDYwZTQ0OGRhNjYwOWRmZGM2OGEzOWE4IiwicHJvZmlsZU5hbWUiOiJCQVJLeDQiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZhYTM2NmU2ZGFiMjIzZjk3YTAyNGE3OTg1NTEwMzViMDczMWE5YjA5MmNhNzRkYjBiMmU4ZmE1MDYyZmFjN2EifX19";
	private static final String TEXTURE_SIGNATURE = "yIRmkUscRgF2j0OeOqQ5wEHGeVRzOu9XpmtjVhBZRuMxDi8TLlwI9jvHQFXhEeswJBft2H6XTTanc2Fj4SMUSc77u0wTzVTVpkK0oo63W1NvASBTnAaNx77h544ncc7pNJwufjS2EaYY09VTLmWYkUovoqrsFlik0a7z7z/suSVj8p5wfN4tFC+qcYUM5SCqkISVKvtMDt0MBk4TUdAYNtQbYGdzwHKJQeZvxv46bX2wT/763cpZC0pQo+1ZfMI10ohtSdD0Fp39CoU35Tz8FNF6OEmhG7k3450TPt/utB7QdIqJOVfPdGlYvpVIuFDytMoSxU4wqyoykY42rXwwZo6i9t5em3GDLSIi4bkWcghgtJLvJ4LjxDx0fFe5RcIwmE4f/QqCzUVVj0730I2rUUHeOGS/ttvPXqLKnq/Gnp5tz/D9IO7d8EX7y83v8ALnfRvbqr9CFV3IrxFSxbfsINp7UzL4mJzTroXsOgoEKxDO+kNNnEpcMHfmsK/xOX4HJe8bnk44SFjvEdwX9VytHVFzttpT8hV9ECsPNp5eT9JiRTpizsa2Xq9S2QLW2XEIX3KVBhg6cOLebpCd3ms3nKROToPN9xKgjzSRVtddvU21EeWPxM93HxotBqaeWpS4CewCQ2wJxaa6M0O8BpAIdG18vGHHaT32R/28/TEN7nM=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);
	private static final int FOOD_DELIVERY_XP_REWARD = 100;

	private final InteractionSequence startFoodDeilveryInteraction;
	private final InteractionSequence completeFoodDeliveryInteraction;

	public MelcherFarmer(Location location) {
		super(ChatColor.GREEN + "Farmer", 1, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		new QuestMarker(this) {
			@Override
			protected QuestMarkerDisplayType getDisplayType(PlayerCharacter pc) {
				return QuestMarkerDisplayType.HIDDEN;
			}
		};
		startFoodDeilveryInteraction = new InteractionSequence(5) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Oh thank the gods you're here.", pc);
					break;
				case 1:
					say("I manage Melcher's food supply, but thieves have been stealing our food deliveries and crops.",
							pc);
					break;
				case 2:
					say("The villagers have begun to blame me for the food shortage, even threatening to eat me and my family!",
							pc);
					break;
				case 3:
					say("Won't you please find the thieves responsible? I hear they lurk somewhere in the forest.", pc);
					break;
				case 4:
					Quests.FOOD_DELIVERY.start(pc);
					break;
				}
			}
		};
		completeFoodDeliveryInteraction = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Adventurer if I didn't have a wife I would kiss you right now.", pc);
					break;
				case 1:
					say("Instead, have this bread as my expression of gratitude.", pc);
					break;
				case 2:
					pc.removeItem(Items.STOLEN_FOOD, 10);
					Quests.FOOD_DELIVERY.getObjective(0).complete(pc);
					pc.giveXp(FOOD_DELIVERY_XP_REWARD);
					pc.giveItem(Items.GARLIC_BREAD, 5);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.FOOD_DELIVERY.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startFoodDeilveryInteraction.advance(pc);
		} else if (Quests.FOOD_DELIVERY.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (pc.getItemCount(Items.STOLEN_FOOD) >= 10) {
				completeFoodDeliveryInteraction.advance(pc);
			} else {
				say("Have you found them yet? And the food?", pc);
			}
		} else {
			say("Greetings! Mind the horses.", pc);
		}
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
