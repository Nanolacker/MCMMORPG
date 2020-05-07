package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.quest.QuestMarker;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class MelcherFarmer extends StaticHuman {

	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4Nzc3MjU0MjU4NSwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YTMzZmFmNzAyMThjYjMzNmY1NWQ1MDViNzRmMTczYzNmNGUyYWU0OGU0ZGYwNDVhODM4MWYzY2EyNzA5ZmEyIgogICAgfQogIH0KfQ==";
	private static final String TEXTURE_SIGNATURE = "xsQYuJeScX9L+KlyhS/ZbZUVS41E6LFqeFxHu1u9AmUpRJduFVAcv15lootifLS7DyL8nQsYlbW874PeHNlA7UjCnvlakmDyBPqaAFhdaVhmcGniGfI/Tai9TOGyV328yTLpjPRtjcOf2iOzQKfdoByA0DmeyDidvEHSx26muuRf6bpl4wvHUyVB/5WHDTq24i0mdu2aoJs2NenkrEX6XaoQDKxR+LGXceB9P5pcncP1TCkxcvwGxzfBojDSyOIfm31VKsaHEe1udLzWQgWxAxKNQksQj2GndIM1g9YnESqizt/8mCkOZ6zsNa8TakoofC1MC7u69G/m3WZF5kt3/S+Mcn+vNx/dlgDmYaUmCx+8FptuyfwOdd5E3VX9QUiCH+aLhdjg8lvOPh09fG5fTxJF3pN71EPw+MBxuYzg+1oaqtfUr7KkgRvNUIZDP5vrzBH0I5DFdxdBuPzvXM9R4/R4S3+vRNGZI3tnaDPwKZUroazMyE4xpV2yzJXKLtUaYVHD131pCzV4XRGvib3DhlSGZdmzsdygFv4Kf5/tKJ4HRMtOYOzReHw4qtpEN102q9uFV2S4USUaMy4okfXitge0S+lFQ/62N+pmlbc+QAOFsVsFo1J73cBypmHFTThcfSRETlXPGuwMFfY+pYK0tfBFK/DlWVmZdPlINq6XTDo=";
	private static final Noise SPEAK_NOISE = new Noise(Sound.ENTITY_VILLAGER_AMBIENT);

	public MelcherFarmer(Location location) {
		super(ChatColor.GREEN + "Melcher Farmer", 1, location, TEXTURE_DATA, TEXTURE_SIGNATURE);
		QuestMarker.createMarker(location.clone().add(0, 2.25, 0));
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		say("Help me recover some " + Items.FOOD_SUPPLIES.formatName() + " from the bandits.", pc);
		Quests.RECOVERING_THE_FOOD.start(pc);
		SPEAK_NOISE.play(pc);
	}

}
