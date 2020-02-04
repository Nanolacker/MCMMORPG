package com.mcmmorpg.impl.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;

public class FighterListener implements Listener {

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
	}

}
