package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseMainHandItemEvent;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.utils.Debug;

class ItemListener implements Listener {

	private Map<Item, PlayerCharacter> droppedItemMap = new HashMap<>();

	@EventHandler
	private void onClickItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack itemStack = event.getCurrentItem();
		if (ItemFactory.staticInteractables.contains(itemStack)) {
			event.setCancelled(true);
			StaticInteractableEvent consumableUseEvent = new StaticInteractableEvent(player, itemStack);
			EventManager.callEvent(consumableUseEvent);
			return;
		}
	}

	@EventHandler
	private void onInteractWithItem(PlayerInteractEvent event) {
		ItemStack itemStack = event.getItem();
		if (itemStack == null) {
			return;
		}
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = event.getPlayer();
		Action action = event.getAction();
		if (ItemFactory.staticInteractables.contains(itemStack)) {
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				EventManager.callEvent(new StaticInteractableEvent(player, itemStack));
			}
			return;
		}
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		event.setCancelled(true);

		MainHandItem mainHandItem = MainHandItem.forItemStack(itemStack);
		if (mainHandItem != null) {
			if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
				EventManager.callEvent(new PlayerCharacterUseMainHandItemEvent(pc, mainHandItem));
			}
			return;
		}

		ConsumableItem consumableItem = ConsumableItem.forItemStack(itemStack);
		if (consumableItem != null) {
			if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
				itemStack.setAmount(itemStack.getAmount() - 1);
				EventManager.callEvent(new PlayerCharacterUseConsumableItemEvent(pc, consumableItem));
			}
			return;
		}
	}

	@EventHandler
	private void onThrowItem(PlayerDropItemEvent event) {
		Item item = event.getItemDrop();
		ItemStack itemStack = item.getItemStack();
		if (ItemFactory.staticInteractables.contains(itemStack)) {
			event.setCancelled(true);
			return;
		}
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		droppedItemMap.put(item, pc);
	}

	@EventHandler
	private void onPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Item item = event.getItem();
		if (droppedItemMap.get(item) == pc) {
			droppedItemMap.remove(item);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onItemDespawn(ItemDespawnEvent event) {
		Item item = event.getEntity();
		droppedItemMap.remove(item);
	}

	@EventHandler
	private void onOpenLootChest(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		Location location = block.getLocation();
		LootChest chest = LootChest.forLocation(location);
		if (chest == null) {
			return;
		}
		if (chest.getOwner() == null || chest.getOwner() == pc) {
			chest.open(pc);
			chest.remove();
		} else {
			pc.sendMessage(ChatColor.RED + "This chest does not belong to you!");
		}
	}

	@EventHandler
	private void onClickInLootChest(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		if (LootChest.inventories.contains(inventory)) {
			event.setCancelled(true);
			ItemStack itemStack = event.getCurrentItem();
			if (itemStack == null) {
				return;
			}
			int slot = event.getSlot();
			inventory.setItem(slot, null);
			Player player = (Player) event.getWhoClicked();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			// pc should never be null
			pc.getInventory().addItem(itemStack);
			boolean empty = inventoryIsEmpty(inventory);
			if (empty) {
				player.closeInventory();
			}
		}
	}

	private boolean inventoryIsEmpty(Inventory inventory) {
		ItemStack[] contents = inventory.getContents();
		for (ItemStack itemStack : contents) {
			if (itemStack != null) {
				return false;
			}
		}
		return true;
	}

	@EventHandler
	private void onCloseLootChest(InventoryCloseEvent event) {
		Inventory inventory = event.getInventory();
		if (LootChest.inventories.contains(inventory)) {
			LootChest.inventories.remove(inventory);
		}
	}

}
