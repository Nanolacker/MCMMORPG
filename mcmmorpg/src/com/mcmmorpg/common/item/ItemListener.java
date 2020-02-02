package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
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
		Player player = event.getPlayer();
		if (ItemFactory.staticInteractables.contains(itemStack)) {
			event.setCancelled(true);
			handleStaticInteractableInteraction(player, itemStack, event.getAction());
			return;
		}
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		// don't let people place blocks and what not
		event.setCancelled(true);
		if (ItemFactory.weapons.contains(itemStack)) {
			handleWeaponInteraction(pc, itemStack, event.getAction());
		} else if (ItemFactory.consumables.contains(itemStack)) {
			handleConsumableInteraction(pc, itemStack, event.getAction());
		}
	}

	private void handleStaticInteractableInteraction(Player player, ItemStack interactable, Action action) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			StaticInteractableEvent event = new StaticInteractableEvent(player, interactable);
			EventManager.callEvent(event);
		}
	}

	private void handleWeaponInteraction(PlayerCharacter pc, ItemStack weapon, Action action) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			PlayerCharacterUseWeaponEvent event = new PlayerCharacterUseWeaponEvent(pc, weapon);
			EventManager.callEvent(event);
		}
	}

	private void handleConsumableInteraction(PlayerCharacter pc, ItemStack consumable, Action action) {
		if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
			PlayerCharacterUseConsumableEvent event = new PlayerCharacterUseConsumableEvent(pc, consumable);
			EventManager.callEvent(event);
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
		if (chest != null) {
			chest.open(pc);
			chest.remove();
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
