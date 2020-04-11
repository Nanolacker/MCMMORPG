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
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.MovementSyncer;
import com.mcmmorpg.common.character.MovementSyncer.MovementSyncMode;
import com.mcmmorpg.common.character.HumanEntity;
import com.mcmmorpg.common.character.NonPlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.character.PlayerCharacter.PlayerCharacterCollider;
import com.mcmmorpg.common.character.Source;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class CultistAcolyte extends NonPlayerCharacter {

	private static final double RESPAWN_TIME = 30;
	private static final String TEXTURE_DATA = "eyJ0aW1lc3RhbXAiOjE1ODIyNzUyNTQ0MjksInByb2ZpbGVJZCI6ImU3OTNiMmNhN2EyZjQxMjZhMDk4MDkyZDdjOTk0MTdiIiwicHJvZmlsZU5hbWUiOiJUaGVfSG9zdGVyX01hbiIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzA5YmQ4NWY4NzdiMzI4OWJhZDk5NmRhMTI4NDZlMzFmOGY3MjFmNGRiZTA4MThjYzFhOGU0MDg5MGQwZjllNiJ9fX0=";
	private static final String TEXTURE_SIGNATURE = "k0xqaka2s5GiIGXts/k6HlRs/tBlgBSMf+ry8sJcM7i3nHNkdFT2tl3v0X14yC1Mu0qspt0NCLt7RT3FzfQRjOHN+u6gl6MNOdnT5Ej9tgAHfpv6yc05Fv6a4JXo5B8R6i88djSBdAvho+sdJ5+w+ElRXEvovuZVxRQ2Bqelyzjggm2jNoGud2BRYOJdbYYrcwSqkfxnukfSJUE5bCnhyX5sKGIteEzJtxGs5wMjDew7aMYCaAvip2PEYXjY8tIXu5Xj21qwzkSI5kI3bv9eH7ZDYhJ7BNExPsMk1XXTsFZY1sdwLLk3stNBjczySwDv6aEARtfjvrt9xQNQ3tMClUblkTVKb9umg+P1B08mkmTYm4jyqElCgI0DuoCtG4sVRPtaR9fyO5xlyxMm8u8bl74hKRERI7btJOG/ZmmMjyYAJk+4iq2rfSItCYNdXhq9i0c64/9o+PbDJlX2oihABSpCgsi/0xW2lty2LznGFrwRmMgoFGZxTQou2jXoZgjORKRnd4E2FMuKUcZy0zoy5lR+/DsMSOBek+QODFEi5thOS1f7XJ2ipNpQeUR2aYMEozDXnvxqjc8e/CIw5ynksKp8Wqk3Dni+/U4626w59X85qb2WUlkv7BQshcRSpNHbWUp2xVH3IysCEpw6/3rlop+T36HZSlH0n4L1YzqunvY=";
	private static final Noise HURT_NOISE = new Noise(Sound.ENTITY_PLAYER_HURT);
	private static final Noise DEATH_NOISE = new Noise(Sound.ENTITY_VILLAGER_DEATH);

	private final Location spawnLocation;
	private final CharacterCollider hitbox;
	private final HumanEntity human;
	private Zombie ai;
	private final MovementSyncer aiSyncer;

	public CultistAcolyte(int level, Location spawnLocation) {
		super(ChatColor.RED + "Cultist Acolye", level, spawnLocation);
		super.setMaxHealth(20);
		this.spawnLocation = spawnLocation;
		this.hitbox = new CharacterCollider(this, spawnLocation, 1, 2, 1);
		aiSyncer = new MovementSyncer(this, null, MovementSyncMode.CHARACTER_FOLLOWS_ENTITY);
		human = new HumanEntity(spawnLocation, TEXTURE_DATA, TEXTURE_SIGNATURE);
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
