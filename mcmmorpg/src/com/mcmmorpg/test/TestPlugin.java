package com.mcmmorpg.test;

import java.io.File;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.ui.ActionBar;
import com.mcmmorpg.common.utils.IOUtils;
import com.mcmmorpg.common.utils.StringUtils;

public class TestPlugin extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

		File fighterFile = new File(
				"C:\\Users\\conno\\git\\MCMMORPG\\mcmmorpg\\src\\com\\mcmmorpg\\test\\Fighter.json");
		PlayerClass fighter = IOUtils.jsonFromFile(fighterFile, PlayerClass.class);
		fighter.initialize();

		registerEvents(new PCListener());
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

}
