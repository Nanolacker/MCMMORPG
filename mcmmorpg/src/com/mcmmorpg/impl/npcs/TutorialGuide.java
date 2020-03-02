package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.quest.QuestStatus;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.ui.MessageSequence;

public class TutorialGuide extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	private MessageSequence messageSequence;

	public TutorialGuide(Location location) {
		super(ChatColor.GREEN + "Tutorial Guide", 5, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		getHuman().setHelmet(new ItemStack(Material.IRON_SWORD));
		String[] messages = { formatDialogue("Hello there!"), formatDialogue("How do you do?"),
				formatDialogue("How is your day?"), formatDialogue("The weather is nice, yes?"),
				formatDialogue("I'm running out of things to say."), formatDialogue("Farewell!") };
		messageSequence = new MessageSequence(messages, 2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				new Noise(Sound.ENTITY_VILLAGER_AMBIENT).play(pc);
				if (messageIndex == messages.length - 1) {
					Quest quest = Quest.forName("Skills Tutorial");
					if (quest.getStatus(pc) == QuestStatus.NOT_STARTED) {
						quest.start(pc);
					}
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		messageSequence.advance(pc);
	}

}
