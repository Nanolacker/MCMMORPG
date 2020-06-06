package com.mcmmorpg.impl.npcs;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Items;
import com.mcmmorpg.impl.Quests;

public class MelcherThief extends AbstractHumanEnemy {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 30;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 5;
	private static final double RESPAWN_TIME = 60;
	private static final int SPEED = 2;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDI4ODE5ODY1MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg0YmY0Nzk4NTQ2MDdiNmFhM2I0ZjIyOGE4MjJiNzk4ZmUzMTg2Yjg2MWQzNDZlYTYwODdiZjFkYWRmNzQ1YyIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "nnXW+GRWr48UEapAZKGvyqXigWQRs0XBKbHjOmJ5c41kmM+qulE4ue6y8GlDPE3vNtVqfTm79G6hKCMQXxm5yVr8wQlAO/N9+a1Qe3Bwid36pTCZjeTD3P8YxKQXvuwmO9nzwuc2IvEMcde2BXAiFBpWuGXqBFr/sYKR3b5naSO0TecpGgCh+YnqZca4Eip684hrOeDovq759smd8AsyIcUd72mt+/uBUtq2AkAgrjgi/JeINHg4FXqq2tfdMeDu0AQFSCkSw9wi+68aonSTSB1a1l006hdd/efOzp9zTfcV9HKxfisVZSLO8FbVFUf51wMpIZ86tyG32mTx/pqNbjXYX5eiByYqRuqsSsPCjCiTJ8PfBDru00J0sUNZBjrC98Gy8Ja+rPiSlVE/ReYgKkLsW0/yRA4cA3fDGnMsvTa//9URZdtQ4UQBMaZeKuA5DptXFOxl9Sgvt9Ka7uK8k/AB1UFWRVeCSjWiYxB0154s6AzfUp5uDakLxiHs0GzFdgQUerU+fBxMlEGo79nR1QtafDt8YBu+rwIlbPLxzgA9vplhAmEVnckTvsPvz641WZD4XY46uZ2c/PUxVr3k3gdtASrO9kj235GMLGO0KpQl3YXZc6/pOEslUWGBuNhljCt2ZeJzKugcTHQ42qt0uhhH3qAybclAb9shbbIA6r4=";
	protected static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	protected static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);

	public MelcherThief(Location spawnLocation) {
		super(ChatColor.RED + "Thief", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD, RESPAWN_TIME, SPEED,
				TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(getLocation(), 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.THWARTING_THE_THIEVES.getObjective(0).addProgress(pc, 1);
		}
		int stolenFoodDropAmount = (int) (Math.random() * 3);
		Items.STOLEN_FOOD.drop(location, stolenFoodDropAmount);
		Items.THIEF_DAGGER.drop(getLocation(), 0.02);
		Items.BRITTLE_WAND.drop(getLocation(), 0.02);
		Items.HIDE_BOOTS.drop(location, 0.02);
		Items.HIDE_HEADGEAR.drop(location, 0.02);
		Items.HIDE_LEGGINGS.drop(location, 0.02);
		Items.HIDE_TUNIC.drop(location, 0.02);
		Items.TORN_HOOD.drop(location, 0.02);
		Items.TORN_LEGGINGS.drop(location, 0.02);
		Items.TORN_ROBES.drop(location, 0.02);
		Items.TORN_SHOES.drop(location, 0.02);
		Items.STALE_BREAD.drop(location, 0.2);
	}

}