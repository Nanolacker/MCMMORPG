package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.InteractionSequence;
import com.mcmmorpg.impl.Quests;

public class MelcherBartender extends StaticHuman {

	private static final int LEVEL = 2;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODgwNjc0MjU5NjcsInByb2ZpbGVJZCI6IjcyY2IwNjIxZTUxMDQwN2M5ZGUwMDU5NGY2MDE1MjJkIiwicHJvZmlsZU5hbWUiOiJNb3M5OTAiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RlMTI2YzhjMDUxZjI0MmFlZmZlZDFhYWU2NzU4ZDllOGE2YmJhZWNkMWQ3ZDRiN2YwZmVjZWVmMjE2NjVjMDAifX19";
	private static final String TEXTURE_SIGNATURE = "CMwLaniychk/MT4d/0zB10Q4xxvaDu17BnaYUI1V+8halCO4tTaZWpmtd/oJm0lQ6CrH9eIFDR4bVjFuTUHMMS7QuQSz+JSFyDSHMJda6tHl4OfDKF22jSrnFTQXap110V7F8pogTyWFC4EXU7zmSbTcyiSLhk7H6CSnAH5DqH/gQyzujh7bVCLFvdHcHCxx2KLp5fkB/Odhw4MI7PgweD56ODEUIRnWTCMoKwkJjBPWDtdGIKXxzaiO2jhCFUhP4tDS++IzrMyrymXzcA3KVAZQzIF3QlnlVI0vya0Ld8pa9ob0BySSCeG+se2G5NuHugqHBRXUZGIpjN1VycDNR2h/xmoWgmL/ihLE9UncrkwDlrfZ7x2+ayHWwYnnyJxTjbZxiMDkJ0vTRbfcYd7uuvDBpkg/TGUzYnHd3cgzJeaARUFKUufeRpEvrPh4yCJTGfVQ3jhGPsKWHNRtpPQp8zOFJRr5PIAoGF+UDZ8uBtVaczy808dg68uCxMG4/+CfojNgRnezMS1VmHwZF5+2atiguDwEVZsOWlH9N62oKqFjG441cN2rO/pTpCecH/voZAoKUVRUHwFOnYN8IrxAKC/gpx0RCtsw37mueYWh3o7uMobTEj+w+oaNarnEzBHX4emg/4Q7YK3eZUVDYVsBbfgABk0eJWTvJz3GZgTJYO4=";
	private static final int CALMING_THE_TAVERN_XP = 75;
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence startCalmingTheTavernInteraction;
	private final InteractionSequence completeCalmingTheTavernInteraction;

	// I ASKED YOU TO CALM HIM, NOT KILL HIM! YOU JUST MURDERED MY BIGGEST CUSTOMER!
	// THERE GOES HALF OF MY INCOME! AND WHO'S
	// GONNA CLEAN UP THIS MESS? THERE'S BLOOD SPLATTERED ACROSS THE FLOOR! HOW AM I
	// SUPPOSED TO ATTRACT CUSTOMERS WHEN MY TAVERN LOOKS LIKE THIS?
	public MelcherBartender(Location location) {
		super(ChatColor.GREEN + "Bartender", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
		startCalmingTheTavernInteraction = new InteractionSequence(1) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Psst. That guy over there has been causing trouble.", pc);
					break;
				}
			}
		};
		completeCalmingTheTavernInteraction = new InteractionSequence(1) {

			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {

				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		QuestStatus status = Quests.CALMING_THE_TAVERN.getStatus(pc);
		switch (status) {
		case COMPLETED:
			say("Welcome to my tavern.", pc);
			break;
		case IN_PROGRESS:
			if (Quests.CALMING_THE_TAVERN.getObjective(0).isComplete(pc)) {
				Quests.CALMING_THE_TAVERN.getObjective(1).complete(pc);
				pc.giveXp(CALMING_THE_TAVERN_XP);
			} else {
				say("He's over there.", pc);
			}
			break;
		case NOT_STARTED:
			say("Help.", pc);
			Quests.CALMING_THE_TAVERN.start(pc);
		default:
			break;
		}
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

}
