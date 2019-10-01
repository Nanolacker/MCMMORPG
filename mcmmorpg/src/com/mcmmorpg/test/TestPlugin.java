package com.mcmmorpg.test;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import com.google.gson.Gson;
import com.mcmmorpg.common.Debug;
import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.utils.StringUtils;

public class TestPlugin extends MMORPGPlugin {

	@Override
	protected void onMMORPGStart() {
		Debug.log("Starting");

//		File fighterFile = new File(
//				"C:\\Users\\conno\\git\\MCMMORPG\\mcmmorpg\\src\\com\\mcmmorpg\\test\\Fighter.json");
//		PlayerClass fighter = JsonUtils.jsonFromFile(fighterFile, PlayerClass.class);
//		fighter.initialize();
//
//		registerEvents(new PCListener());

		Player player = Debug.getFirstPlayer();
		Inventory inventory = player.getInventory();

		Gson gson = new Gson();
		String json = gson.toJson(inventory);
		System.out.println(json);
	}

	@Override
	protected void onMMORPGStop() {
		Debug.log("Stopping");
	}

	private void paragraphTest(String text) {
		List<String> parag = StringUtils.paragraph(text);
		for (String line : parag) {
			Debug.log(line);
		}
	}

}
