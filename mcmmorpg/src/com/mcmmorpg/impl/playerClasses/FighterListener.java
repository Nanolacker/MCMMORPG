package com.mcmmorpg.impl.playerClasses;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.ItemRarity;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.time.RepeatingTask;

public class FighterListener implements Listener {

	private static final Noise BASH_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);
	private static final Noise SELF_HEAL_NOISE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise SWEEP_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);

	private final PlayerClass fighter;
	private final Skill bash;
	private final Skill selfHeal;
	private final Skill lunge;
	private final Skill cyclone;
	private final Skill overheadStrike;

	public FighterListener() {
		fighter = PlayerClass.forName("Fighter");
		bash = fighter.skillForName("Bash");
		selfHeal = fighter.skillForName("Self Heal");
		lunge = fighter.skillForName("Lunge");
		cyclone = fighter.skillForName("Cyclone");
		overheadStrike = fighter.skillForName("Overhead Strike");
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		int level = event.getNewLevel();
		if (level == 1) {
			Weapon weapon = (Weapon) Item.forID(0);
			pc.giveItem(weapon);
			pc.setMaxHealth(25);
			pc.setCurrentHealth(25);
			pc.setHealthRegenRate(0.2);
			pc.setMaxMana(15);
			pc.setCurrentMana(15);
			pc.setManaRegenRate(1);
			pc.grantXp(Integer.MAX_VALUE);
			ConsumableItem consumable = new ConsumableItem(1, "Healing Potion", ItemRarity.UNCOMMON,
					Material.GLASS_BOTTLE, "Heal yourself a small amount", 10);
			consumable.initialize();
			Item item = new Item(1, "Grass", ItemRarity.LEGENDARY, Material.GRASS, "Treasure of the fields of bleh");
			item.initialize();
			pc.giveItem(item);
			pc.giveItem(consumable, 10);
			ArmorItem armor = new ArmorItem(4, "Helmet", ItemRarity.COMMON, Material.IRON_HELMET, "A terrible helmet",
					"Fighter", 1, 10);
			armor.initialize();
			pc.giveItem(armor);
		}
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != fighter) {
			return;
		}
		Skill skill = event.getSkill();
		if (skill == bash) {
			useBash(pc);
		} else if (skill == selfHeal) {
			useSelfHeal(pc);
		} else if (skill == lunge) {
			useLunge(pc);
		} else if (skill == cyclone) {
			useCyclone(pc);
		} else if (skill == overheadStrike) {
			useOverheadStrike(pc);
		}
	}

	private void useBash(PlayerCharacter pc) {
		Location location = pc.getLocation();
		Vector lookDirection = location.getDirection();
		World world = location.getWorld();
		location.add(lookDirection).add(0, 1, 0);
		world.spawnParticle(Particle.EXPLOSION_LARGE, location, 1);
		BASH_NOISE.play(location);
		Collider hitbox = new Collider(location, 2, 2, 2) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(10 + bash.getUpgradeLevel(pc) * 10, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	private void useSelfHeal(PlayerCharacter pc) {
		double healAmount = selfHeal.getUpgradeLevel(pc) * 8;
		pc.heal(healAmount, pc);
		SELF_HEAL_NOISE.play(pc.getLocation());
	}

	private void useCyclone(PlayerCharacter pc) {
		Location location = pc.getLocation();
		new RepeatingTask(0.1) {
			int count = 0;

			@Override
			protected void run() {
				Location loc = pc.getLocation();
				loc.setYaw(loc.getYaw() + 60);
				pc.getPlayer().teleport(loc);
				count++;
				if (count == 6) {
					cancel();
				}
			}
		}.schedule();
		createCycloneEffect(location);
		for (int i = 0; i < 5; i++) {
			new DelayedTask(0.1 * i) {
				@Override
				protected void run() {
					createCycloneEffect(location);
				}
			}.schedule();
		}
		Collider hitbox = new Collider(location, 8, 8, 8) {
			@Override
			protected void onCollisionEnter(Collider other) {
				if (other instanceof CharacterCollider) {
					AbstractCharacter target = ((CharacterCollider) other).getCharacter();
					if (!target.isFriendly(pc)) {
						target.damage(10, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	private void createCycloneEffect(Location location) {
		World world = location.getWorld();
		Location effectLocation = location.clone();
		// add some variation
		double xOffset = 6 * (Math.random() - 0.5);
		double zOffset = 6 * (Math.random() - 0.5);
		effectLocation.add(xOffset, 1, zOffset);
		world.spawnParticle(Particle.EXPLOSION_LARGE, effectLocation, 1);
		SWEEP_NOISE.play(location);
	}

	private final Set<Player> playersUsingOverheadStrike = new HashSet<>();

	private void useOverheadStrike(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		Location location = player.getLocation();
		Vector direction = location.getDirection();
		direction.setY(1);
		player.setVelocity(direction);
		new DelayedTask(0.1) {
			@Override
			protected void run() {
				playersUsingOverheadStrike.add(player);
			}
		}.schedule();
	}

	@EventHandler
	private void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (playersUsingOverheadStrike.contains(player)) {
			if (player.isOnGround()) {
				PlayerCharacter pc = PlayerCharacter.forPlayer(player);
				Location location = player.getLocation();
				Collider hitbox = new Collider(location.clone().add(0, 1, 0), 4, 2, 4) {
					@Override
					protected void onCollisionEnter(Collider other) {
						if (other instanceof CharacterCollider) {
							AbstractCharacter character = ((CharacterCollider) other).getCharacter();
							if (!character.isFriendly(pc)) {
								character.damage(20, pc);
							}
						}
					}
				};
				hitbox.setActive(true);
				hitbox.setActive(false);
				new Noise(Sound.ENTITY_GENERIC_EXPLODE).play(location);
				location.getWorld().spawnParticle(Particle.EXPLOSION_HUGE, location, 1);
				playersUsingOverheadStrike.remove(player);
			}
		}
	}

	private void useLunge(PlayerCharacter pc) {
		Player player = pc.getPlayer();
		Location location = player.getLocation();
		Vector direction = location.getDirection();
		direction.setY(0);
		direction.multiply(3);
		player.setVelocity(direction);

	}

	@EventHandler
	private void onUseWeapon(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != fighter) {
			return;
		}
		if (weapon == null) {
			useFists(pc);
		} else if (weapon.getID() == 0) {
			useShortSword(pc);
		}

	}

	private void useFists(PlayerCharacter pc) {
		double damage = 1;
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Ray ray = new Ray(start, direction, 3);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
			}
		}
		pc.silence(0.75);
	}

	private void useShortSword(PlayerCharacter pc) {
		double damage = 5;
		Location start = pc.getLocation().add(0, 1.5, 0);
		Vector direction = start.getDirection();
		Ray ray = new Ray(start, direction, 3);
		Raycast raycast = new Raycast(ray, CharacterCollider.class);
		Collider[] hits = raycast.getHits();
		for (Collider hit : hits) {
			AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
			if (!character.isFriendly(pc)) {
				character.damage(damage, pc);
			}
		}
		pc.silence(0.75);
	}

}
