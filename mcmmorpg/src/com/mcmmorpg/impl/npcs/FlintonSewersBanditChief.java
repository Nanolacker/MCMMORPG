package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.impl.Items;

public class FlintonSewersBanditChief extends AbstractHumanEnemy {

	private static final int LEVEL = 6;
	private static final double MAX_HEALTH = 250;
	private static final double DAMAGE_AMOUNT = 15;
	private static final int XP_REWARD = 500;
	private static final double RESPAWN_TIME = 60;
	private static final int SPEED = 2;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODE2NDI5MDk5ODIsInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hZDZjMzk4NmY4N2YwYzNmZTRmMTk0NzZiYWI4MzQ0NDM5NDlmMzQ2MDFiYWNkMjk1YjljZTM5YTdiYjNjYzk4In19fQ==";
	private static final String TEXTURE_SIGNATURE = "yXcQay1LwWqkKfAdBsgvekvigWmy3GBdrMl5xnl/QfTWLIMX0yz9JTJkCJeSnyMDM/FAif+a7mtAtsuf83C56xkqTmWhsRcGvBjKfvU83h9ejfesYEDvUQvEjfaD7BxwGYsHp9+Dy/caS9lbH0E1hFCO373w92XIXBSzjo1dJPdnXK2XyqMJeVmtqVHva3mMLInHtWExzU65eliIyCztaKQ/7YxoiSBhRtyamzp6JnoA9lw3fSFqYwrjAWY9ppoVnAIqH0qjqx85wyET2x9p4uFP983keTjekYzvKew29lmgS0iije3+7Lj4WcgF4ZAYL1X/GTEgzlEVTMf0n6A8k3FTDWcRNOThnY39L+nmFBXKz4sho9THpE4tilFqAt+qOSRyeil6+m1vHnrnmLtk4HhnLeC3n0bWUZ6+zdeTseGK2ldUbgnhW043wjrI0rDcHJFxbN/5OWGMOv8rD0Jk0cPC+uUd2iZ39/PZ5hbMOX/JCtWfDD67iPguSl/DJ/gla9Wr+faDrtXYq6Jjm1+06T4QupQL0PoU3gV+oKg/Q8DlPTxoytac2UinFweRgOXlheVdAfrfyquEGP7azJ5fxSw7IvTBuUMF0rE549B2t1rtyyvMKO8QXQzcRuE3W58tgzuz1Fa8JSJAYymxsaZxbKkscssqiJfQ3J0F53BsG5E=";
	protected static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	protected static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);

	public FlintonSewersBanditChief(Location spawnLocation) {
		super(ChatColor.RED + "Bandit Chief", LEVEL, spawnLocation, MAX_HEALTH, DAMAGE_AMOUNT, XP_REWARD, RESPAWN_TIME,
				SPEED, TEXTURE_DATA, TEXTURE_SIGNATURE);
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		HURT_NOISE.play(getLocation());
	}

	@Override
	public void onDeath() {
		super.onDeath();
		Location location = getLocation();
		DEATH_NOISE.play(getLocation());
		Items.BATTERED_MAIL_BOOTS.drop(location, 0.2);
		Items.BATTERED_MAIL_CUIRASS.drop(location, 0.2);
		Items.BATTERED_MAIL_GREAVES.drop(location, 0.2);
		Items.BATTERED_MAIL_BOOTS.drop(location, 0.2);
		Items.BANDITS_BATTLE_AXE.drop(location, 0.3);
		Items.POTION_OF_LESSER_HEALING.drop(location, 0.5);
	}

}
