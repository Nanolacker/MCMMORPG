package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.ui.MessageSequence;
import com.mcmmorpg.common.utils.Debug;

public class TutorialGuide extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	private MessageSequence messageSequence;

	public TutorialGuide(Location location) {
		super(ChatColor.GREEN + "Tutorial Guide", 5, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		getHuman().setHelmet(new ItemStack(Material.IRON_SWORD));

		String name = getName();
		String[] messages = { formatDialogue("Hello there!"), formatDialogue("How do you do?") };
		messageSequence = new MessageSequence(messages, 2) {
			@Override
			protected void onAdvance(PlayerCharacter pc, int messageIndex) {
				if (messageIndex == messages.length - 1) {
					Quest quest = Quest.forName("Skills Tutorial");
					quest.start(pc);
				}
			}
		};
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		messageSequence.advance(pc);
	}

}
