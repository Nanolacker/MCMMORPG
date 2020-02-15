package com.mcmmorpg.impl.playerClassListeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Projectile;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.utils.Debug;

public class MageListener implements Listener {

	private static final Noise FIREBALL_CONJURE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise FIREBALL_HIT = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise ICEBALL_CONJURE = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);
	private static final Noise ICEBALL_HIT = new Noise(Sound.BLOCK_LAVA_EXTINGUISH);

	private final PlayerClass mage;
	private final Skill fireball;
	private final Skill iceball;

	public MageListener() {
		mage = PlayerClass.forName("Mage");
		fireball = mage.skillForName("Fireball");
		iceball = mage.skillForName("Iceball");
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		if (pc.getPlayerClass() != mage) {
			return;
		}
		Debug.log("rt");
		Skill skill = event.getSkill();
		if (skill == fireball) {
			useFireball(pc);
		} else if (skill == iceball) {
			useIceball(pc);
		}
	}

	private void useFireball(PlayerCharacter pc) {
		Debug.log("FB");
		Location start = pc.getLocation();
		World world = start.getWorld();
		Vector lookDirection = start.getDirection();
		start.add(lookDirection).add(0, 1, 0);
		FIREBALL_CONJURE.play(start);
		Vector velocity = lookDirection.multiply(8);
		Player player = pc.getPlayer();
		Block target = player.getTargetBlock(null, 15);
		Debug.log("target is " + target);
		// ensure we don't shoot through walls
		double maxDistance = start.distance(target.getLocation());
		double hitSize = 1;
		//Fireball fireballEntity = ;
		Projectile projectile = new Projectile(start, velocity, maxDistance, hitSize) {
			@Override
			protected void setLocation(Location location) {
				super.setLocation(location);
				//fireballEntity.setLocation(location);
			}

			@Override
			protected void onHit(Collider hit) {
				if (hit instanceof CharacterCollider) {
					AbstractCharacter character = ((CharacterCollider) hit).getCharacter();
					if (!character.isFriendly(pc)) {
						character.damage(10, pc);
					}
				}
			}
		};
		projectile.fire();
	}

	private void useIceball(PlayerCharacter pc) {

	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		int level = event.getNewLevel();
		if (level == 1) {
			// starting gear
		}
	}

	@EventHandler
	private void onWeaponUse(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
	}

}
