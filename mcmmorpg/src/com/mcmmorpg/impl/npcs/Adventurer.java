package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class Adventurer extends StaticHuman {

	private static final int LEVEL = 10;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDUyODM3MDM0NywKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE2OGZiYjMzNjE1NTlmYWFlZTM0ODI2YjYxMzc4M2E5N2Q5NGI4NzVlOGJjMzYwYjBkYjIzNGZmYmQyN2MzYzgiCiAgICB9CiAgfQp9";
	private static final String TEXTURE_SIGNATURE = "muUVNQ0if8UlwkCorxbYcyA963Uvc+CJagawKD2m25JYZqVLM3DFqe+o99FQzLmLdyNJ2W8hBM1QiUBY9YqnxgYm2+K0TZCa9LncGTHxuLDSjACUBJJzd3D6jjgLB8DmHEbkAu77+5KP8Ufm/9blrDZAbqi7oaTCQbS0Vl31vAuFMPpO4kEq9rtBtYn5e7lEwho6DF/OXJ0DiypBZALw+hzqgoiP3IpulZR5xQp1VwQrDcdato9RdwmzoeTsjElAQw1jDcw9TdceZ2Gul1TbeSBym6v8KHjYQH4tA8mk4tRyT+A2v7ED0LOdrM89cYWw5wjvBzjUNQzDkVX6i0e+oRDIhSPDPNd6haqixKWnsHTaXxTrLXnafDlTifBwM/cLfUNyGw6kwNpNsUjKoTaH6trlWWGpvNhNbE8v1SSRGPJFRx6p9CBDH9sinvowEepWiyNpbpsh65SRiRMdMMVstSH6mnD+vfHRs/WXXs+Qlcq7wB2IOVeiLW0T1W6WXPNAhj6DpztrFIj4jqaewxgGPa8GyIwfT1eBXqPvuAMThf33/2quqKoZfzJlIQ3ueAiwtcv4N+OaiyL/1NQZAtn5+Tb5Taw3lpX/gBBR00M08/586TJ/LpVz3wYdE0ihgkyEJsQN7xeawG29tkONTHLscCyrpUymJMQZ9syCCdduHTc=";
	private static final String[] DIALOGUE_OPTIONS = { "Greetings fellow adventurer.",
			"What is it you seek? Wealth, power, hero status?", "Be careful out there." };
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	public Adventurer(Location location) {
		super(ChatColor.GREEN + "Adventurer", LEVEL, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		say(dialogue, pc);
	}

	@Override
	public void say(String dialogue, AbstractCharacter recipient) {
		super.say(dialogue, recipient);
		SPEAK_NOISE.play((PlayerCharacter) recipient);
	}

}
