package com.mcmmorpg.impl.npcs;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.NPCHuman;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.item.LootChest;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.quest.Quest;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class Thief extends NonPlayerCharacter implements Listener {

	private static final Quest SLAYING_THE_THIEVES = Quest.forName("Slaying the Thieves");
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODE2NDI5MDk5ODIsInByb2ZpbGVJZCI6IjgyYzYwNmM1YzY1MjRiNzk4YjkxYTEyZDNhNjE2OTc3IiwicHJvZmlsZU5hbWUiOiJOb3ROb3RvcmlvdXNOZW1vIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9hZDZjMzk4NmY4N2YwYzNmZTRmMTk0NzZiYWI4MzQ0NDM5NDlmMzQ2MDFiYWNkMjk1YjljZTM5YTdiYjNjYzk4In19fQ==";
	private static final String TEXTURE_SIGNATURE = "yXcQay1LwWqkKfAdBsgvekvigWmy3GBdrMl5xnl/QfTWLIMX0yz9JTJkCJeSnyMDM/FAif+a7mtAtsuf83C56xkqTmWhsRcGvBjKfvU83h9ejfesYEDvUQvEjfaD7BxwGYsHp9+Dy/caS9lbH0E1hFCO373w92XIXBSzjo1dJPdnXK2XyqMJeVmtqVHva3mMLInHtWExzU65eliIyCztaKQ/7YxoiSBhRtyamzp6JnoA9lw3fSFqYwrjAWY9ppoVnAIqH0qjqx85wyET2x9p4uFP983keTjekYzvKew29lmgS0iije3+7Lj4WcgF4ZAYL1X/GTEgzlEVTMf0n6A8k3FTDWcRNOThnY39L+nmFBXKz4sho9THpE4tilFqAt+qOSRyeil6+m1vHnrnmLtk4HhnLeC3n0bWUZ6+zdeTseGK2ldUbgnhW043wjrI0rDcHJFxbN/5OWGMOv8rD0Jk0cPC+uUd2iZ39/PZ5hbMOX/JCtWfDD67iPguSl/DJ/gla9Wr+faDrtXYq6Jjm1+06T4QupQL0PoU3gV+oKg/Q8DlPTxoytac2UinFweRgOXlheVdAfrfyquEGP7azJ5fxSw7IvTBuUMF0rE549B2t1rtyyvMKO8QXQzcRuE3W58tgzuz1Fa8JSJAYymxsaZxbKkscssqiJfQ3J0F53BsG5E=";
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PILLAGER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_PILLAGER_DEATH);
	private static final double RESPAWN_TIME = 30;

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final NPCHuman human;
	private Zombie ai;
	private final MovementSyncer aiSyncer;

	public Thief(int level, Location spawnLocation) {
		super(ChatColor.RED + "Thief", level, spawnLocation);
		super.setMaxHealth(maxHealth(level));
		this.spawnLocation = spawnLocation;
		hitbox = new CharacterCollider(this, spawnLocation.clone().add(0, 1, 0), 1, 2, 1);
		human = new NPCHuman("", spawnLocation, TEXTURE_DATA, TEXTURE_SIGNATURE);
		aiSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		EventManager.registerEvents(this);
	}

	private static double maxHealth(int level) {
		return 10 + 2 * level;
	}

	@Override
	public void spawn() {
		super.spawn();
		hitbox.setActive(true);
		human.setVisible(true);
		ai = (Zombie) spawnLocation.getWorld().spawnEntity(spawnLocation, EntityType.ZOMBIE);
		ai.setBaby(true);
		ai.setSilent(true);
		ai.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
		ai.eject();
		Entity vehicle = ai.getVehicle();
		if (vehicle != null) {
			vehicle.remove();
		}
		ai.setRemoveWhenFarAway(false);
		ai.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_SWORD));
		ai.getEquipment().setItemInMainHandDropChance(0f);
		aiSyncer.setEntity(ai);
		aiSyncer.setEnabled(true);
	}

	@Override
	public void despawn() {
		super.despawn();
		hitbox.setActive(false);
		aiSyncer.setEnabled(false);
		human.setVisible(false);
		ai.remove();
	}

	@Override
	public void setLocation(Location location) {
		super.setLocation(location);
		hitbox.setCenter(location.clone().add(0, 1, 0));
		human.setLocation(location);
	}

	@Override
	public void damage(double amount, Source source) {
		super.damage(amount, source);
		// for light up red effect
		human.hurt();
		HURT_NOISE.play(getLocation());
	}

	@Override
	protected void onDeath() {
		super.onDeath();
		grantXpToNearbyPlayers();
		hitbox.setActive(false);
		human.setVisible(false);
		ai.remove();
		Location location = getLocation();
		DEATH_NOISE.play(location);
		location.getWorld().spawnParticle(Particle.CLOUD, location, 10);
		setLocation(spawnLocation);
		LootChest.spawnLootChest(location);
		DelayedTask respawn = new DelayedTask(RESPAWN_TIME) {
			@Override
			protected void run() {
				setAlive(true);
			}
		};
		respawn.schedule();
	}

	private void grantXpToNearbyPlayers() {
		Collider xpBounds = new Collider(getLocation(), 25, 25, 25) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof PlayerCharacterCollider) {
					PlayerCharacter pc = ((PlayerCharacterCollider) other).getCharacter();
					SLAYING_THE_THIEVES.getObjective(0).addProgress(pc, 1);
					pc.grantXp(getXpToGrant());
				}
			}
		};
		xpBounds.setActive(true);
		xpBounds.setActive(false);
	}

	private int getXpToGrant() {
		return 5 + getLevel() * 2;
	}

	@EventHandler
	private void onHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		Entity damaged = event.getEntity();
		if (damager == this.ai) {
			if (damaged instanceof Player) {
				Player player = (Player) damaged;
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				if (pc == null) {
					return;
				}
				pc.damage(getDamageAmount(), this);
				human.swingHand();
			}
		} else if (damaged == this.ai) {
			DelayedTask cancelKnockback = new DelayedTask(0.1) {
				@Override
				protected void run() {
					ai.setVelocity(new Vector());
				}
			};
			cancelKnockback.schedule();
		}
	}

	private double getDamageAmount() {
		return getLevel() * 2;
	}

}
