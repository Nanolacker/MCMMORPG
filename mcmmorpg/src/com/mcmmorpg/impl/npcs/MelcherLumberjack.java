package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class MelcherLumberjack extends StaticHuman {

	private static final String TEXTURE_DATA = "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJpZCIgOiAiMDM4MjJjY2NkNWE5NDdkZTgwMzdmMjI3OTI4YzhiNmEiLAogICAgICAidHlwZSIgOiAiU0tJTiIsCiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWUwYzk1ZGFkOTIxZGQyNWY4M2U0ZTdmOWZiZTcwNTljNTZhYWIxYmE4ZWM0NzM5ODFlYWI5OWY5Y2QyZDA4YyIsCiAgICAgICJwcm9maWxlSWQiIDogImI3NDc5YmFlMjljNDRiMjNiYTU2MjgzMzc4ZjBlM2M2IiwKICAgICAgInRleHR1cmVJZCIgOiAiOWUwYzk1ZGFkOTIxZGQyNWY4M2U0ZTdmOWZiZTcwNTljNTZhYWIxYmE4ZWM0NzM5ODFlYWI5OWY5Y2QyZDA4YyIKICAgIH0KICB9LAogICJza2luIiA6IHsKICAgICJpZCIgOiAiMDM4MjJjY2NkNWE5NDdkZTgwMzdmMjI3OTI4YzhiNmEiLAogICAgInR5cGUiIDogIlNLSU4iLAogICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85ZTBjOTVkYWQ5MjFkZDI1ZjgzZTRlN2Y5ZmJlNzA1OWM1NmFhYjFiYThlYzQ3Mzk4MWVhYjk5ZjljZDJkMDhjIiwKICAgICJwcm9maWxlSWQiIDogImI3NDc5YmFlMjljNDRiMjNiYTU2MjgzMzc4ZjBlM2M2IiwKICAgICJ0ZXh0dXJlSWQiIDogIjllMGM5NWRhZDkyMWRkMjVmODNlNGU3ZjlmYmU3MDU5YzU2YWFiMWJhOGVjNDczOTgxZWFiOTlmOWNkMmQwOGMiCiAgfSwKICAiY2FwZSIgOiBudWxsCn0=";
	private static final String TEXTURE_SIGNATURE = "ppSx5C2jbkpZb55b74AJSxoPTFqBeBzJHoDrwVV78X4QTrU08udEnh/T4xjt/OIirfKGlz+v7j1vqwC80+Y0wt+xO1wyMsFywgYnR2liqgw42jrGbnxJV9lJ1zUd68d7HbePbdPxp8ZylXA5Sv1wA3qFyOHzrzvIQLTEc+CodNZhpH3roIFgzU3D747XblmfB0CU1eJFqjK4unAO9FxRYErnRmHcLQYBhHLMb3vZ2ilzWALizACyn/ZyjkIuJl7cyk/qck8mh4sv7sVDSGl1Ioo9DYpIvCumFeniLz8GyT+HNgCPB8QTL1uofW5wqsxb3NkkY5BVcxhvM69YL1z3KPswH5V3Y5QkGo3DI6qXLw8vsGrs2FvsRbp98sSUMLoFCyn/XkglV7cSD3k8+8TuOgyCvsHs2xjx2fvnX7EBMYR30awCEhZdRfKpZvuAUOm8lzdrzA5HyRsV8uQcwvEkXjAlRLunwT+mQyJuBCN/w3GkVAYeHHBn32jIXx4tLxLikiegJ2+D8VHvZZYrrILCZ0EXUxhe+nwlzbEgSLepwllLuMs+GOLuvJE8uGvEX0cZg9h0Z99ozgu3XPvmjAod4YXgnjgKyhWO5Yhl5uC4IcHrPPFEGUBjqm79QfjfaO4sbTZP6aFox+4kB7/YTxZ/393Cb3GqYzQsghFM2VUMhIU=";
	private static final String[] DIALOGUE_OPTIONS = { "What?", "You wish you had muscles like me.",
			"I've been chopping wood all day." };
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1f, 0.6f);
	private static final ItemStack AXE = new ItemStack(Material.IRON_AXE);

	public MelcherLumberjack(Location spawnLocation) {
		super(ChatColor.GREEN + "Lumberjack", 3, spawnLocation, TEXTURE_DATA, TEXTURE_SIGNATURE);
		entity.setMainHand(AXE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		say(dialogue, pc);
		SPEAK_NOISE.play(pc);
	}

}
