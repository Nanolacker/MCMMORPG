package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class Adventurer extends StaticHuman {

	private static final int LEVEL = 10;
	private static final String MALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDUyODM3MDM0NywKICAicHJvZmlsZUlkIiA6ICIyM2YxYTU5ZjQ2OWI0M2RkYmRiNTM3YmZlYzEwNDcxZiIsCiAgInByb2ZpbGVOYW1lIiA6ICIyODA3IiwKICAic2lnbmF0dXJlUmVxdWlyZWQiIDogdHJ1ZSwKICAidGV4dHVyZXMiIDogewogICAgIlNLSU4iIDogewogICAgICAidXJsIiA6ICJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzE2OGZiYjMzNjE1NTlmYWFlZTM0ODI2YjYxMzc4M2E5N2Q5NGI4NzVlOGJjMzYwYjBkYjIzNGZmYmQyN2MzYzgiCiAgICB9CiAgfQp9";
	private static final String MALE_TEXTURE_SIGNATURE = "muUVNQ0if8UlwkCorxbYcyA963Uvc+CJagawKD2m25JYZqVLM3DFqe+o99FQzLmLdyNJ2W8hBM1QiUBY9YqnxgYm2+K0TZCa9LncGTHxuLDSjACUBJJzd3D6jjgLB8DmHEbkAu77+5KP8Ufm/9blrDZAbqi7oaTCQbS0Vl31vAuFMPpO4kEq9rtBtYn5e7lEwho6DF/OXJ0DiypBZALw+hzqgoiP3IpulZR5xQp1VwQrDcdato9RdwmzoeTsjElAQw1jDcw9TdceZ2Gul1TbeSBym6v8KHjYQH4tA8mk4tRyT+A2v7ED0LOdrM89cYWw5wjvBzjUNQzDkVX6i0e+oRDIhSPDPNd6haqixKWnsHTaXxTrLXnafDlTifBwM/cLfUNyGw6kwNpNsUjKoTaH6trlWWGpvNhNbE8v1SSRGPJFRx6p9CBDH9sinvowEepWiyNpbpsh65SRiRMdMMVstSH6mnD+vfHRs/WXXs+Qlcq7wB2IOVeiLW0T1W6WXPNAhj6DpztrFIj4jqaewxgGPa8GyIwfT1eBXqPvuAMThf33/2quqKoZfzJlIQ3ueAiwtcv4N+OaiyL/1NQZAtn5+Tb5Taw3lpX/gBBR00M08/586TJ/LpVz3wYdE0ihgkyEJsQN7xeawG29tkONTHLscCyrpUymJMQZ9syCCdduHTc=";
	private static final String FEMALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDUzODYzOTkwNSwKICAicHJvZmlsZUlkIiA6ICI5MWZlMTk2ODdjOTA0NjU2YWExZmMwNTk4NmRkM2ZlNyIsCiAgInByb2ZpbGVOYW1lIiA6ICJoaGphYnJpcyIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9jMDkxNmExZDBkMmM1NjFmN2UwNjMzZGMyNjExN2RmYjAyYjdiZTY4ZjU5NTIwZmU1N2NmNmQ3NGFmODNkYzQ1IgogICAgfQogIH0KfQ==";
	private static final String FEMALE_TEXTURE_SIGNATURE = "fOwRoMcmJm1lfbVp0HcpBh3uPhshHzFKxWiLmrm15hHgQyBBaziPXggbk7kV8MwnuT2nFB9VCrqcFxQDAst/e1gnLF+ggA/NPjW4Mki+ezZrBH0jH/XOAWASCc9bnJ3vMwI+y35iOtkcfTtcmc2iPcZZo1i+/j3mbH9lqut7w9/iDY3KFkmQTGB3QGg500jCFQ0q5Acc//ekx6ZelIjsO+5zoHNB233tlnkk/2PEKttjVm3PbHmGhrQLlK3EStI3BBh5lDo5m+2ORG97hH3o4q7503rENFdTWyYFt/vkmg74sq9pKaZ0TGaVAFxSEr8HJIiujlfHxniYBAgb4qb1GqVyoGqe6nLGWdGqtCqOApykrtsnYCj5Agm64xQrXYjpFZaGaJJxea1mBZRyr7NqcqomGd84iTSeqkXQ+1v8WSDAXuYwya53QaNhFTJH2apCKB+prLskLNFhsdZruesxpK+jS/aNwray1kmcoXMPixPH1XohY/zUh8m9lXXuoWNBvsYYU26oWtUycNm8095y4lJkVqUVBr28MKyJv3qE/+AJMhlqSH0/vK6akwQYFIIOAEBDbAsNBPcwZPp+QF2Q8zOoQo5o6euatx5Nr8KvfEnZ3xTJZu8/qebj7e9nL8FhqdjoLtp9b/Hib5hBpiwLlltVJ/ctKdCiOT/1f9/pURg=";
	private static final Noise MALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final Noise FEMALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);

	private static final String[] DIALOGUE_OPTIONS = { "Greetings fellow adventurer.",
			"What is it you seek? Wealth, power, hero status?", "Be careful out there." };

	private final boolean male;

	public Adventurer(Location location, boolean male) {
		super(ChatColor.GREEN + "Adventurer", LEVEL, location, male ? MALE_TEXTURE_DATA : FEMALE_TEXTURE_DATA,
				male ? MALE_TEXTURE_SIGNATURE : FEMALE_TEXTURE_SIGNATURE);
		this.male = male;
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		say(dialogue, pc);
	}

	@Override
	public void say(String dialogue, PlayerCharacter recipient) {
		super.say(dialogue, recipient);
		if (male) {
			MALE_SPEAK_NOISE.play(recipient);
		} else {
			FEMALE_SPEAK_NOISE.play(recipient);
		}
	}

}
