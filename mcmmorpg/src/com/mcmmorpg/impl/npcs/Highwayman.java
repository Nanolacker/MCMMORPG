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

public class Highwayman extends AbstractHumanEnemy {

	private static final double MAX_HEALTH = 125;
	private static final double DAMAGE_AMOUNT = 7;
	private static final int XP_REWARD = 10;
	private static final int LEVEL = 5;
	private static final double RESPAWN_TIME = 60;
	private static final int SPEED = 2;
	private static final String TEXTURE_DATA = "ewogICJ0aW1lc3RhbXAiIDogMTU5MDI4OTc4NzI4MiwKICAicHJvZmlsZUlkIiA6ICJmMjc0YzRkNjI1MDQ0ZTQxOGVmYmYwNmM3NWIyMDIxMyIsCiAgInByb2ZpbGVOYW1lIiA6ICJIeXBpZ3NlbCIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9iN2NhOGU4NWViYmFjYjM2N2ExMGUyMmRiMTg0YWNhNGE5MGY4ZGIzZmU4YzM4YWRlMTRkYmEzNGU4NTBlYmJiIgogICAgfQogIH0KfQ==";
	private static final String TEXTURE_SIGNATURE = "mlQoWa/DUmlDlUbnt92/+a1UmVzIyRTUq6t+eUGw21A7yGazbmWRJB/4pXs22sNpVTU6Q5bO3W6M+9Am7+z2+qV6/S5iIIog9u9iyFvgApKR0bdddSrYlwu/8jDi3nkp7HcahEMB3EaLrjwtn9XabIjYU7rmGVbEsfxkoHsT+uUzp5bVpzlS5Pxbd7p5zcS05nv2GlmDS/4CM2EQ5c+Bju1CEp+akKjLXUcjnqlUoT7irn692J5XtL3eyxEPFviv/HfPQMlX4Tdh0nnzwhBImpviejJb33V5zd1Liua/t3ufVZL+mpBi0hp9NSegkdpA//0euvjU0LQxU2jwfspmPn/enKuJs/5XA44Tr6dueon6rRFo0CEpnsA/z7+bQz3Dvv3opo/vdtJhvaTBsb88Tlbo1Wp5OaFPNYi5Ycit2tWXSGIbHbSythMJTGLZn6qKx3Epi7WNE9M/zqVPvCU7c8iqOqJoMDUc4e26lJOE9qaPyIysTF36UvxKilSqLSVcx/jplbGqBZJW7+2uRz5CeX11LesKdKZQtkM98EDApkzHDdE9S6dlqK2+qPBMdYcqtN+VrSUrs80BkapFnHIXfRVSt3RlfBsAsIOEf4wghHRq0FxrYHfrN0lMbQ9WdakW40Y6ZwwmaL7TKIhwguo1EM73yZ8XSrr9Y8cRyBVqrFU=";
	protected static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	protected static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);

	public Highwayman(Location spawnLocation) {
		super(ChatColor.RED + "Highwayman", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD, RESPAWN_TIME,
				SPEED, TEXTURE_DATA, TEXTURE_SIGNATURE);
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
		List<PlayerCharacter> nearbyPcs = PlayerCharacter.getNearbyPlayerCharacters(location, 25);
		for (PlayerCharacter pc : nearbyPcs) {
			Quests.CLEARING_THE_ROAD.getObjective(0).addProgress(pc, 1);
		}
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
		Items.POTION_OF_MINOR_HEALING.drop(location, 0.1);
	}

}
