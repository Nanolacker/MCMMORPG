package com.mcmmorpg.impl.npcs;

import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.playerClass.PlayerClass;

import net.md_5.bungee.api.ChatColor;

public class TutorialWeaponsDealer extends StaticHuman {

	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	private static final Weapon APPRENTICE_SWORD = (Weapon) Item.forID(0);
	private static final Weapon APPRENTICE_STAFF = (Weapon) Item.forID(1);

	public TutorialWeaponsDealer(Location location) {
		super(ChatColor.GREEN + "Weapons Dealer", 5, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		PlayerClass playerClass = pc.getPlayerClass();
		if (playerClass.getName().equals("Fighter")) {
			if (pc.getItemCount(APPRENTICE_SWORD) == 0) {
				pc.giveItem(APPRENTICE_SWORD);
				say("Here you go", pc);
			} else {
				say("You already have a sword!", pc);
			}
		} else if (playerClass.getName().equals("Mage")) {
			pc.giveItem(APPRENTICE_STAFF);
			say("Here you go", pc);
		} else {
			say("You already have a staff!", pc);
		}
	}

}
