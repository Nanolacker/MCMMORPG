package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class FlintonSewersAlchemist extends StaticHuman {

	private static final int LEVEL = 10;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4OTc2NzU1NTcxMCwKICAicHJvZmlsZUlkIiA6ICI3NTE0NDQ4MTkxZTY0NTQ2OGM5NzM5YTZlMzk1N2JlYiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGFua3NNb2phbmciLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGQxM2Y3OGE1OWE3OTMxMGFlM2M4ZjAyZjE3ZTk2MDgzYzJjYmQ4NWU2ZjU4MGM0ODQyZjQ3MGM3ZGMwYWY3NiIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "XlBqJu0cKzn5E7qLko904GYrPxayoVlq06+bwa5D2M10OgzvzJn+DtXt/wvzHalFpQe6IS04H3qdxH4KCKdmLZKjwSgsnFZAQ3Ny8dQG2rK0IG5INglK58hyCQ4YFGWRIqn1rTAriFaeruUBE5xON24Pt0ZpNO3wQVa28/6FMwyPrtvN2F6/yrc1pxni7xJPQgDVTvmIxmszbaIoRzhUT9Vz+enIJsEULNf0xXmRbhyXBcaTC4Tw+XuXuEsg111iPnkx0w3Feu15qspv9YZlZ3wJ1O2kCLROcMgQ3fvgYV0Vinp7P8mDgXoDgdPJaBS2j/dEjKFpDTtTub48IFzA1Td48sHQ8lT6gDDU4ziPJN4THSIEIY6d9vU5OYrEIf80kRZRUdqDrv0tz3jUH6/WoqDn7iJ8As+5Zk806fsJvS5KiQOoUG7XSzHn2+c79I3K9AH1F2RNTK2rwsosR+TCacvXpCCQNPNOwZ82u8qe0DXnG4DRM1fNLsrhgh9ufPVOsFUWDUHwyRFeT6OkyApkEvuwofUFckStPRZFZIArNT9pnRAh1zXUes6UWZxWc8WiOmsu6h7jJP/6BLaxdoUvxYmqMvzhDzFuBaMrTQYYi7par/0p5b9eRRTAUcwK012oDYegw1z20bJRlQ8JcfRvbZV7xzAomdAXivg4Rt+TM3A=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);

	private final InteractionSequence startSamplingSludge;
	private final InteractionSequence completeSamplingSludge;

	public FlintonSewersAlchemist(Location location) {
		super(ChatColor.GREEN + "Alchemist", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
		startSamplingSludge = new InteractionSequence(7) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Don't you find sludge so interesting? It has many peculiar properties.", pc);
					break;
				case 1:
					say("Dissolves bones, sears flesh, and makes adorable sludge monsters!", pc);
					break;
				case 2:
					say("Er, perhaps you don't find the gelatinous cubes very cute.", pc);
					break;
				case 3:
					say("Regardless, I think it's absolutely fascinating. I would love to get samples to study, but alas, I am a scientist, not a fighter.",
							pc);
					break;
				case 4:
					say("You, however, seem to have some fight in you. I can tell.", pc);
					break;
				case 5:
					say("So how 'bout it? Get me some sludge samples and I'll reward you handsomely.", pc);
					break;
				case 6:
					Quests.SAMPLING_SLUDGE.start(pc);
					break;
				}
			}
		};
		completeSamplingSludge = new InteractionSequence(3) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("By golly, you've done it! The last few I asked never made it back, haha!", pc);
					break;
				case 1:
					say("As promised, here's your reward! Don't mind the stickiness. Everything gets sticky down here.",
							pc);
					break;
				case 2:
					pc.removeItem(Items.SLUDGE, 25);
					pc.removeItem(Items.COLOSSAL_SLUDGE, 2);
					Quests.SAMPLING_SLUDGE.getObjective(2).complete(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.SAMPLING_SLUDGE.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startSamplingSludge.advance(pc);
		} else if (Quests.SAMPLING_SLUDGE.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (pc.getItemCount(Items.SLUDGE) >= 25 && pc.getItemCount(Items.COLOSSAL_SLUDGE) >= 2) {
				completeSamplingSludge.advance(pc);
			} else {
				say("Come on now, I need way more sludge than that!", pc);
			}
		} else {
			say("Well met.", pc);
		}
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
