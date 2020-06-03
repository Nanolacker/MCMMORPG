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

public class MelcherBartender extends StaticHuman {

	private static final int LEVEL = 2;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODgwNjc0MjU5NjcsInByb2ZpbGVJZCI6IjcyY2IwNjIxZTUxMDQwN2M5ZGUwMDU5NGY2MDE1MjJkIiwicHJvZmlsZU5hbWUiOiJNb3M5OTAiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2RlMTI2YzhjMDUxZjI0MmFlZmZlZDFhYWU2NzU4ZDllOGE2YmJhZWNkMWQ3ZDRiN2YwZmVjZWVmMjE2NjVjMDAifX19";
	private static final String TEXTURE_SIGNATURE = "CMwLaniychk/MT4d/0zB10Q4xxvaDu17BnaYUI1V+8halCO4tTaZWpmtd/oJm0lQ6CrH9eIFDR4bVjFuTUHMMS7QuQSz+JSFyDSHMJda6tHl4OfDKF22jSrnFTQXap110V7F8pogTyWFC4EXU7zmSbTcyiSLhk7H6CSnAH5DqH/gQyzujh7bVCLFvdHcHCxx2KLp5fkB/Odhw4MI7PgweD56ODEUIRnWTCMoKwkJjBPWDtdGIKXxzaiO2jhCFUhP4tDS++IzrMyrymXzcA3KVAZQzIF3QlnlVI0vya0Ld8pa9ob0BySSCeG+se2G5NuHugqHBRXUZGIpjN1VycDNR2h/xmoWgmL/ihLE9UncrkwDlrfZ7x2+ayHWwYnnyJxTjbZxiMDkJ0vTRbfcYd7uuvDBpkg/TGUzYnHd3cgzJeaARUFKUufeRpEvrPh4yCJTGfVQ3jhGPsKWHNRtpPQp8zOFJRr5PIAoGF+UDZ8uBtVaczy808dg68uCxMG4/+CfojNgRnezMS1VmHwZF5+2atiguDwEVZsOWlH9N62oKqFjG441cN2rO/pTpCecH/voZAoKUVRUHwFOnYN8IrxAKC/gpx0RCtsw37mueYWh3o7uMobTEj+w+oaNarnEzBHX4emg/4Q7YK3eZUVDYVsBbfgABk0eJWTvJz3GZgTJYO4=";
	private static final int BAR_FIGHT_XP = 100;
	private static final int PEST_CONTROL_XP = 100;
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);

	private final InteractionSequence startBarFightInteraction;
	private final InteractionSequence completeBarFightInteraction;
	private final InteractionSequence completePestControlInteraction;

	public MelcherBartender(Location location) {
		super(ChatColor.GREEN + "Bartender", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
		startBarFightInteraction = new InteractionSequence(5) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Hey there adventurer! Welcome to my tavern.", pc);
					break;
				case 1:
					say("Why, after all your travelin' I suppose you fancy yourself a drink.", pc);
					break;
				case 2:
					say("Unfortunately I've a rowdy drunkard on my hands, scarin' away my other patrons.", pc);
					break;
				case 3:
					say("Surely you've dealt with all sorts on your travels. Could you have a word with him and escort him out for me?",
							pc);
					break;
				case 4:
					Quests.BAR_FIGHT.start(pc);
					break;
				}
			}
		};
		completeBarFightInteraction = new InteractionSequence(6) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("BY THE GODS!", pc);
					break;
				case 1:
					say("Well aren't you just a bloodthirsty barbarian. I said ESCORT, not EXTERMINATE.", pc);
					break;
				case 2:
					say("That man was half of my income, you scoundrel!", pc);
					break;
				case 3:
					say("You know what? Now you owe me a favor. I have a rat problem in my basement that needs taken care of.",
							pc);
					break;
				case 4:
					say("Judging by your last performance, I think you're perfect for the job. Now get to it!", pc);
					break;
				case 5:
					Quests.BAR_FIGHT.getObjective(1).complete(pc);
					pc.giveXp(BAR_FIGHT_XP);
					Quests.PEST_CONTROL.start(pc);
					break;
				}
			}
		};
		completePestControlInteraction = new InteractionSequence(4) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int interactionIndex) {
				switch (interactionIndex) {
				case 0:
					say("Well, well, well. If it ain't the butcher themself.", pc);
					break;
				case 1:
					say("I'd ask you to patch up the wall, but you'd probably manage to blow it up somehow.", pc);
					break;
				case 2:
					say("Here's your drink. Now put that darned weapon of yours away.", pc);
					break;
				case 3:
					Quests.PEST_CONTROL.getObjective(2).complete(pc);
					pc.giveItem(Items.MELCHER_MEAD, 3);
					pc.giveXp(PEST_CONTROL_XP);
					break;
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		if (Quests.BAR_FIGHT.compareStatus(pc, QuestStatus.NOT_STARTED)) {
			startBarFightInteraction.advance(pc);
		} else if (Quests.BAR_FIGHT.compareStatus(pc, QuestStatus.IN_PROGRESS)
				&& Quests.BAR_FIGHT.getObjective(0).isComplete(pc)) {
			completeBarFightInteraction.advance(pc);
		} else if (Quests.PEST_CONTROL.compareStatus(pc, QuestStatus.IN_PROGRESS)) {
			if (Quests.PEST_CONTROL.getObjective(0).isComplete(pc)
					&& Quests.PEST_CONTROL.getObjective(1).isComplete(pc)) {
				completePestControlInteraction.advance(pc);
			} else {
				say("What are you doing here? Get rid of those rats for me.", pc);
			}
		} else if (Quests.PEST_CONTROL.compareStatus(pc, QuestStatus.COMPLETED)) {
			say("Welcome to my tavern.", pc);
		}
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play(recipient);
	}

}
