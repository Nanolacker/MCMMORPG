package com.mcmmorpg.impl.listeners;

import org.bukkit.event.EventHandler;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.SkillUseEvent;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.playerClass.Skill;

public class NecromancerListener {

	private PlayerClass necromancer;
	private Skill raiseSkeleton;

	public NecromancerListener() {
		necromancer = PlayerClass.forName("Necromancer");
		raiseSkeleton = necromancer.skillForName("Raise Skeleton");
	}

	@EventHandler
	private void onUseSkill(SkillUseEvent event) {
		Skill skill = event.getSkill();
		PlayerCharacter pc = event.getPlayerCharacter();
		if (skill == raiseSkeleton) {
			onRaiseSkeletonUse(pc);
		}
	}

	private void onRaiseSkeletonUse(PlayerCharacter pc) {

	}

}
