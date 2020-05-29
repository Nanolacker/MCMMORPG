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

public class Thief extends AbstractHumanEnemy {

	private static final int LEVEL = 2;
	private static final double MAX_HEALTH = 30;
	private static final double DAMAGE_AMOUNT = 4;
	private static final int XP_REWARD = 5;
	private static final double RESPAWN_TIME = 60;
	private static final int SPEED = 2;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDI4ODE5ODY1MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg0YmY0Nzk4NTQ2MDdiNmFhM2I0ZjIyOGE4MjJiNzk4ZmUzMTg2Yjg2MWQzNDZlYTYwODdiZjFkYWRmNzQ1YyIKICAgIH0KICB9Cn0=";
	private static final String TEXTURE_SIGNATURE = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDI4ODE5ODY1MSwKICAicHJvZmlsZUlkIiA6ICI0NDAzZGM1NDc1YmM0YjE1YTU0OGNmZGE2YjBlYjdkOSIsCiAgInByb2ZpbGVOYW1lIiA6ICJGbGF3Q3JhQm90MDEiLAogICJzaWduYXR1cmVSZXF1aXJlZCIgOiB0cnVlLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg0YmY0Nzk4NTQ2MDdiNmFhM2I0ZjIyOGE4MjJiNzk4ZmUzMTg2Yjg2MWQzNDZlYTYwODdiZjFkYWRmNzQ1YyIKICAgIH0KICB9Cn0=";
	protected static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	protected static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);

	public Thief(Location spawnLocation) {
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
			Quests.SLAYING_THE_THIEVES.getObjective(0).addProgress(pc, 1);
		}
		int dropAmount = (int) (Math.random() * 3);
		Items.FOOD_SUPPLIES.drop(location, dropAmount);
		Items.THIEF_DAGGER.drop(getLocation(), 0.05);
		Items.BRITTLE_WAND.drop(getLocation(), 0.05);
		Items.HIDE_BOOTS.drop(location, 0.05);
		Items.HIDE_HEADGEAR.drop(location, 0.05);
		Items.HIDE_LEGGINGS.drop(location, 0.05);
		Items.HIDE_TUNIC.drop(location, 0.05);
		Items.TORN_HOOD.drop(location, 0.05);
		Items.TORN_LEGGINGS.drop(location, 0.05);
		Items.TORN_ROBES.drop(location, 0.05);
		Items.TORN_SHOES.drop(location, 0.05);
	}

}
