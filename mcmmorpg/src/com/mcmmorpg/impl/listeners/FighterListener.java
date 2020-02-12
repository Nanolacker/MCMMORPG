package com.mcmmorpg.impl.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.AbstractCharacter;
import com.mcmmorpg.common.character.CharacterCollider;
import com.mcmmorpg.common.character.NonPlayerHumanEntity;
import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.PlayerCharacterLevelUpEvent;
import com.mcmmorpg.common.event.PlayerCharacterRegisterEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.physics.Collider;
import com.mcmmorpg.common.physics.Ray;
import com.mcmmorpg.common.physics.Raycast;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.RepeatingTask;

public class FighterListener implements Listener {

	private static final Noise BASH_NOISE = new Noise(Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1f);

	private PlayerClass fighter;
	private Skill bash;

	public FighterListener() {
		fighter = PlayerClass.forName("Fighter");
		bash = fighter.skillForName("Bash");
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		Skill skill = event.getSkill();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (skill == bash) {
			useBash(pc);
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
						target.damage(10, pc);
					}
				}
			}
		};
		hitbox.setActive(true);
		hitbox.setActive(false);
	}

	@EventHandler
	private void onLevelUp(PlayerCharacterLevelUpEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		int level = event.getNewLevel();
		if (level == 1) {
			Weapon weapon = (Weapon) Item.forID(0);
			pc.getInventory().addItem(weapon.getItemStack());
		}
	}

	@EventHandler
	private void onUseSword(PlayerCharacterUseWeaponEvent event) {
		Weapon weapon = event.getWeapon();
		if (weapon.getID() != 0) {
			return;
		}
		PlayerCharacter pc = event.getPlayerCharacter();
		double damage = pc.getLevel() * 0.25;
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
	}

	@EventHandler
	private void onJoin(PlayerCharacterRegisterEvent event) {
		PlayerCharacter pc = event.getPlayerCharacter();
		String textureData = "eyJ0aW1lc3RhbXAiOjE1ODE0NzY1MTc1OTksInByb2ZpbGVJZCI6IjNmYzdmZGY5Mzk2MzRjNDE5MTE5OWJhM2Y3Y2MzZmVkIiwicHJvZmlsZU5hbWUiOiJZZWxlaGEiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2Y0MjdlY2NlMmQ2MmJiZmE5MDhjMzQ5OTMwYjliMzc1OWY5OWJhN2NmYTMwODUyMTExZDNiOWM2ODY3MWYxOTcifX19";
		String textureSignature = "glN0pymsMkayUL6u8zoGPyo7Y7vgMUteNhWJ6ou66sDFu9Y6r9ODGzt2USsAgPllJRDSNCE/P1InCGjuR6SGuNHm62gazQ2J+H9MZmfsKjmEsw0xIEWNo+kF1GJz3KO8DrUn9FJtaK7O1phmatYSkCJua1fRPm7faazfoNLHMpOVWhfHAZR4SVNaWbGqAev/zF4+qH6sfPH/kt4Z9k6bLZEEuu8fWvhEZZ1g7u9GzJhprG5/G4nS7BWoVKnl3zQjfgAD+IRbYWV3cA3ZjohQvT7a5kvDLfbkhT5SGKCezSNvJSSCs8WEt9srIPtjSZVkwMh7kOy2IsyTDatqEXCVV/RcMy40YIbFjLYHn/+bT3k+wS+DhiAk99uRpNt35xrpH2k3N6n0PGih4VwvK4mycT+E3BwDaDsahvzNVH1P1PCWG7CW88XdIF35sfGcXMHtnjuNjoaPfW8NymTMx+hTVLqJFmwAt9+YUaCEPEW6S5NcNjlImroEN6HnufzyyJ3x/0H9CLCUvj6knMMRFjMblk2Pck5KtKDN7jLJasddz5htODOgfB1d+F18T/wEEHuTI/8FvPBBvB6QPzlYur9r1T+RojCCPrzuuedsQ4iCVfLiFJLsn5EPWpMlHYyy/ujgH0C+hyzl6wFTKIB2EZ9QGbNc/xEuAuaZaNgE7AqQFho=";
		NonPlayerHumanEntity entity = new NonPlayerHumanEntity("", pc.getLocation(), textureData, textureSignature);
		entity.spawn();
		entity.show(pc.getPlayer());
		RepeatingTask move = new RepeatingTask(0.2) {
			protected void run() {
				Location newLoc = entity.getLocation().clone();
				newLoc.setYaw(pc.getLocation().getYaw());
				entity.setLocation(newLoc);
			}
		};
		move.schedule();
	}

}
