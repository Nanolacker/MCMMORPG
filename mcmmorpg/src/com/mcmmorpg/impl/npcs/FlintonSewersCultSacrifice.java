package com.mcmmorpg.impl.npcs;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.RepeatingTask;

public class FlintonSewersCultSacrifice extends AbstractFriendlyHuman {

	private static final int LEVEL = 1;
	private static final String MALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4Nzc3MjU0MjU4NSwKICAicHJvZmlsZUlkIiA6ICJlNzkzYjJjYTdhMmY0MTI2YTA5ODA5MmQ3Yzk5NDE3YiIsCiAgInByb2ZpbGVOYW1lIiA6ICJUaGVfSG9zdGVyX01hbiIsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS85YTMzZmFmNzAyMThjYjMzNmY1NWQ1MDViNzRmMTczYzNmNGUyYWU0OGU0ZGYwNDVhODM4MWYzY2EyNzA5ZmEyIgogICAgfQogIH0KfQ==";
	private static final String MALE_TEXTURE_SIGNATURE = "xsQYuJeScX9L+KlyhS/ZbZUVS41E6LFqeFxHu1u9AmUpRJduFVAcv15lootifLS7DyL8nQsYlbW874PeHNlA7UjCnvlakmDyBPqaAFhdaVhmcGniGfI/Tai9TOGyV328yTLpjPRtjcOf2iOzQKfdoByA0DmeyDidvEHSx26muuRf6bpl4wvHUyVB/5WHDTq24i0mdu2aoJs2NenkrEX6XaoQDKxR+LGXceB9P5pcncP1TCkxcvwGxzfBojDSyOIfm31VKsaHEe1udLzWQgWxAxKNQksQj2GndIM1g9YnESqizt/8mCkOZ6zsNa8TakoofC1MC7u69G/m3WZF5kt3/S+Mcn+vNx/dlgDmYaUmCx+8FptuyfwOdd5E3VX9QUiCH+aLhdjg8lvOPh09fG5fTxJF3pN71EPw+MBxuYzg+1oaqtfUr7KkgRvNUIZDP5vrzBH0I5DFdxdBuPzvXM9R4/R4S3+vRNGZI3tnaDPwKZUroazMyE4xpV2yzJXKLtUaYVHD131pCzV4XRGvib3DhlSGZdmzsdygFv4Kf5/tKJ4HRMtOYOzReHw4qtpEN102q9uFV2S4USUaMy4okfXitge0S+lFQ/62N+pmlbc+QAOFsVsFo1J73cBypmHFTThcfSRETlXPGuwMFfY+pYK0tfBFK/DlWVmZdPlINq6XTDo=";
	private static final String FEMALE_TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU4Nzc3MDAxMzQwNywKICAicHJvZmlsZUlkIiA6ICIzZmM3ZmRmOTM5NjM0YzQxOTExOTliYTNmN2NjM2ZlZCIsCiAgInByb2ZpbGVOYW1lIiA6ICJZZWxlaGEiLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWFmMTc3OTBiNThiMmU0ODk4YzBiMjhiNzliODE1YTA1M2FiZDQ0ODY0Y2UzZTA2MmI4OWM0YWQ1ZTY4OTY5OSIKICAgIH0KICB9Cn0=";
	private static final String FEMALE_TEXTURE_SIGNATURE = "NS1n+aFDINUCULJ7cB8XKea2KPlQRI8YqjKEElFHebXS3iD+2qXGYsRoh4vwErjypj7NAhfqO7eNb0ZiYHhKgAzH/y1JcrjLOuPN8mdxhBNWUM6SY0pND49OCp2JW3gMEShD7GY0zJ4gyb7Az+XcH8cocLqFlHVuBVyKa5PWFvrKZPj8hZOyQUntFxOi4r6JyABEV9FhN4fn89xwVqMIpxMU7T3hznhL+pthobgJWAXSkZy9DFELqRtBNXO9yj7qsl5ukWOarYBsQoGU6ZvNdv7rhZ1uW7gqI+PnlpixtK//twuEgW+2uvrO3NQj8qrN6SElM1tbZQJ+f7AvDESM+tScYDO6lPeOi5vqswmCSexfe6vuAb1NRRTXIPTRMdeKFluLYYSpgRt0aT03wzvZWKfKp59S1m5pk/uKGV9wTG491fnqytINho28hrPWjHbRt7Cu89aoGhkpVgLERGXajlCcIPlu8Y7otnVHUSoA8RVhbficO5LOMM3uLc3ikfbmtjpcpmV2rqIY4+aej1tn15eM4q1pyTRQSNcYfoblL3IsPEQUPfGX4CHu9sbiZCIHgnZlOO106h//Ekbs1BV+moTTAVPSrbxm/oCLVBF8uQnJC22Z33Z9TR9S6FizaB38CV24JWJhrL74WGNFh6e74MpHTYDrgAfwWDgG+S0Dv40=";
	private static final String[] DIALOGUE_OPTIONS = { "Help me!", "Hey, up here!", "Let me down!",
			"I don't want to die!" };
	private static final double DIALOGUE_PERIOD = 20;

	private static final List<FlintonSewersCultSacrifice> sacrifices = new ArrayList<>();

	static {
		RepeatingTask dialogueTask = new RepeatingTask(DIALOGUE_PERIOD) {
			@Override
			protected void run() {
				FlintonSewersCultSacrifice sacrifice = sacrifices.get((int) (Math.random() * sacrifices.size()));
				String dialogue = DIALOGUE_OPTIONS[(int) (Math.random() * DIALOGUE_OPTIONS.length)];
				sacrifice.speak(dialogue, 25);
			}
		};
		dialogueTask.schedule();
	}

	public FlintonSewersCultSacrifice(Location location, boolean male) {
		super(ChatColor.GREEN + "Sacrifice", LEVEL, location, male ? MALE_TEXTURE_DATA : FEMALE_TEXTURE_DATA,
				male ? MALE_TEXTURE_SIGNATURE : FEMALE_TEXTURE_SIGNATURE);
		sacrifices.add(this);
	}

	@Override
	protected void onInteract(PlayerCharacter pc) {
		// nothing
	}

}
