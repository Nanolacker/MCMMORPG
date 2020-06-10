package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;

public class FlintonAlchemyMaster extends AbstractFriendlyHuman {

	private static final int LEVEL = 20;
	private static final String TEXTURE_DATA = "";
	private static final String TEXTURE_SIGNATURE = "";

	protected FlintonAlchemyMaster(Location location) {
		super(ChatColor.GREEN + "Alchemy Master", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {

	}

}
