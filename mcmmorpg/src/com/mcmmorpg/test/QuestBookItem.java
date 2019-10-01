package com.mcmmorpg.test;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.GameItem;

public class QuestBookItem extends GameItem {

	public QuestBookItem() {
		super(new ItemStack(Material.BOOK));
	}

	@Override
	protected void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		player.openInventory(player.getInventory());
	}

}
