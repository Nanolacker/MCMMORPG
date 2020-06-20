package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class FlintonAlchemistAssistant extends StaticHuman {

	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MTgzNjM2MzcxNSwKICAicHJvZmlsZUlkIiA6ICI2ZmQyNGJlNDk4ZjA0MDJlOTZhYWQ2MWUzY2VmYjZmMCIsCiAgInByb2ZpbGVOYW1lIiA6ICJBbmdlbGFsbHhfIiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ExOTA4NDk0ZTc0YWQyYmIyNzBhMTc3OGNhNGRkZjg4NmU5MzYxMWZkNjAyMzJmN2UwYWVlMzJlYjkwNzc1ZTIiCiAgICB9CiAgfQp9";
	private static final String TEXTURE_SIGNATURE = "iYAFiagV2UyQTKbMr34+cSMi4YR/qBdweLpYccMgGBMpCRZ2o2gMHTf0YCx9uoHpQcwo4MRtWO5eq7mVLvNEInxPsgIXLMTkr3dSI868/mz0cz5rtTqL8hORAU74+s/XNRujET7jtcfcKvE1NwINrMk1szbvQUjzR5fVB765DGzIIipeu5eG8OsvgDH4hmlqWlwZgRk/nfiNNIplZesnKlzP67ZBE73/aV2bYdHE9n7InCZBpB/Ym/lw4IUVwrJMoehaDcZamv2r/YAw3N2XgfI68GVJKbh25cObiZA68LdzmM0N1BqfvUpsCX+FckqH76kBWbSLSAgkdgCi7gthefPqGXrvV8z5CSaE0aZlL5U+1xKUypKwkzUb9A1xHsafW+BLrSh0xbm3gqpwizH/QwqV9JjIhxzPX0+BjN1wcNoA32W/5j2vmK970d7YEb1d4bzM4gJL7r8zvI227xqOkg6oSbkUdKbYHVVVqvd4wFw7ojKl2W77CZ6Y2JHC2e3FSj7xFXhe/Ooa50peRsuigVleQYX1EMWgo01PVLP3j0AA2ycijVYyFcxMN2bTB+P8LUygSL2KvWHoca5Smla6xI/bIobT1cYGlxuKUSS6MtcMgfwucfixQpQ16XgCxfBWg41raE/6WNz1yvw03mZPaLXSn59HK168TUez1Foj24Q=";
	private static final String[] DIALOGUE_OPTIONS = { "Hello there. Interested in potion making?",
			"Welcome to the potion factory." };
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);
	private static final ItemStack POTION = new ItemStack(Material.POTION);

	public FlintonAlchemistAssistant(Location spawnLocation) {
		super(ChatColor.GREEN + "Assistant", 15, spawnLocation, TEXTURE_DATA, TEXTURE_SIGNATURE);
		entity.setMainHand(POTION);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		speak(dialogue, pc);
		SPEAK_NOISE.play(pc);
	}

}
