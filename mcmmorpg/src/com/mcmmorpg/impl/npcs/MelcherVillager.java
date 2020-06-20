package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.sound.Noise;

public class MelcherVillager extends AbstractFriendlyHuman {

	private static final String MALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4Nzc3MjU0MjU4NSwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YTMzZmFmNzAyMThjYjMzNmY1NWQ1MDViNzRmMTczYzNmNGUyYWU0OGU0ZGYwNDVhODM4MWYzY2EyNzA5ZmEyIgogICAgfQogIH0KfQ==";
	private static final String MALE_TEXTURE_SIGNATURE = "xsQYuJeScX9L+KlyhS/ZbZUVS41E6LFqeFxHu1u9AmUpRJduFVAcv15lootifLS7DyL8nQsYlbW874PeHNlA7UjCnvlakmDyBPqaAFhdaVhmcGniGfI/Tai9TOGyV328yTLpjPRtjcOf2iOzQKfdoByA0DmeyDidvEHSx26muuRf6bpl4wvHUyVB/5WHDTq24i0mdu2aoJs2NenkrEX6XaoQDKxR+LGXceB9P5pcncP1TCkxcvwGxzfBojDSyOIfm31VKsaHEe1udLzWQgWxAxKNQksQj2GndIM1g9YnESqizt/8mCkOZ6zsNa8TakoofC1MC7u69G/m3WZF5kt3/S+Mcn+vNx/dlgDmYaUmCx+8FptuyfwOdd5E3VX9QUiCH+aLhdjg8lvOPh09fG5fTxJF3pN71EPw+MBxuYzg+1oaqtfUr7KkgRvNUIZDP5vrzBH0I5DFdxdBuPzvXM9R4/R4S3+vRNGZI3tnaDPwKZUroazMyE4xpV2yzJXKLtUaYVHD131pCzV4XRGvib3DhlSGZdmzsdygFv4Kf5/tKJ4HRMtOYOzReHw4qtpEN102q9uFV2S4USUaMy4okfXitge0S+lFQ/62N+pmlbc+QAOFsVsFo1J73cBypmHFTThcfSRETlXPGuwMFfY+pYK0tfBFK/DlWVmZdPlINq6XTDo=";
	private static final String FEMALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MTgxNjk0MTA0NywKICAicHJvZmlsZUlkIiA6ICIyZGM3N2FlNzk0NjM0ODAyOTQyODBjODQyMjc0YjU2NyIsCiAgInByb2ZpbGVOYW1lIiA6ICJzYWR5MDYxMCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS83MDUzMjRiMzQ5NmZjMzg5OTA4ZWUwMzFiNDc4MjE2MzBlNzkyMTMyNGE1OGVlZjlhMTg3NTY1MGIyZGU5NzZkIgogICAgfQogIH0KfQ==";
	private static final String FEMALE_TEXTURE_SIGNATURE = "j5veRq+dkpQCn1d14a4WzjCv6H1FZ890cs9dagIIc8JiOMhElAmuD7ukywSSvGLvGlilbKvTrlM/k7Zs3evjTwm4reF3vazj713XDBjksFsrH96YnwH0YDXofIv4XA46ElNGD31KfVRCjAxDwzYhlUOK9zB8v4jFFifBCQCh7vZvWt5FejmX5A+/AYnAXwS7mQFKhX+nButYZ08WAaGsTmePjIIDidWrrne5nrdVurQ8cuqoU2dwS9doXJLfscQYzovCVN7D3/yoVoJGkaFWa+QMkcav4bNotx2sOJAF7w11KO3MfZ84xy9L1G2ttEjaEAbrwQvFJTYrpq1svwXGFd3Y4y7ub71IFYzZ8J08aKDU7vCeamqbkRUKMq1TVch9otCzt2opLejZlgoNkjhd6+f/Zv3cQ6hAWZnzVO9H5E7/Y4eLdJ/l2JCmhzmeqOorigdvirckmFjBLktVzTOIGRQamOqrJMY0OC1oBrJzlqIhn5Wou9PHKq+BXLdW+50u4isP6mgBizP9+DuXsjV3IqZCDVgMsVrV6O0slIHgaoOFe/f8RnxXfxKu/msa4qXHM7869hMsOWHRL232meY80CfOulPejJ3KDflDejkuuJvv7MugLG1uqBs+cPE8u6F0sUptFKB5Yld/ZfabP9xe+v7L3Nn7HbHJFOIuhbiOj1E=";
	private static final Noise MALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 0.75f);
	private static final Noise FEMALE_SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT, 1, 1.25f);
	private static final String[] DIALOGUE_OPTIONS = { "Greetings, adventurer.", "How's it going?", "What do you need?",
			"Can I help you?", "I hear thieves have been causing trouble around here.", "Welcome to Melcher.",
			"How do you do?" };

	private final boolean male;

	public MelcherVillager(Location location, boolean male) {
		super(ChatColor.GREEN + "Melcher Villager", 1, location, male ? MALE_TEXTURE_DATA : FEMALE_TEXTURE_DATA,
				male ? MALE_TEXTURE_SIGNATURE : FEMALE_TEXTURE_SIGNATURE);
		this.male = male;
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		int i = (int) (Math.random() * DIALOGUE_OPTIONS.length);
		String dialogue = DIALOGUE_OPTIONS[i];
		speak(dialogue, pc);
		if (male) {
			MALE_SPEAK_NOISE.play(pc);
		} else {
			FEMALE_SPEAK_NOISE.play(pc);
		}
	}

}
