package com.mcmmorpg.common.item;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.StaticInteractableEvent;

class ItemListener implements Listener {

	private Map<org.bukkit.entity.Item, PlayerCharacter> droppedItemMap = new HashMap<>();

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
		event.setCancelled(true);
		if (event.getHand() != EquipmentSlot.HAND) {
			return;
		}
		Player player = event.getPlayer();
		ItemStack itemStack = event.getItem();
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

		if (action == Action.LEFT_CLICK_AIR || action == Action.LEFT_CLICK_BLOCK) {
			handlePlayerCharacterUseWeapon(pc);
		}
	}

	@EventHandler
	private void onWeaponHit(EntityDamageByEntityEvent event) {
		Entity damager = event.getDamager();
		if (damager instanceof Player) {
			Player player = (Player) damager;
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			if (pc == null) {
				return;
			}
			handlePlayerCharacterUseWeapon(pc);
		}
	}

	private void handlePlayerCharacterUseWeapon(PlayerCharacter pc) {
		Weapon weapon = pc.getWeapon();
		// if weapon == null, punch
		if (weapon != null) {
			if (pc.getPlayerClass() != weapon.getPlayerClass()) {
				pc.sendMessage(ChatColor.RED + "Your class cannot use this item!");
				return;
			} else if (pc.getLevel() < weapon.getLevel()) {
				pc.sendMessage(ChatColor.RED + "Your level is too low to use this item!");
				return;
			}
		}
		EventManager.callEvent(new PlayerCharacterUseWeaponEvent(pc, weapon));
	}

	@EventHandler
	private void onChangeHeldItem(PlayerItemHeldEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		Inventory inventory = player.getInventory();
		int slot = event.getNewSlot();
		ItemStack itemStack = inventory.getItem(slot);
		Item item = Item.forItemStack(itemStack);
		if (item instanceof ConsumableItem) {
			ConsumableItem consumable = (ConsumableItem) item;
			int level = consumable.getLevel();
			if (pc.getLevel() < level) {
				pc.sendMessage(ChatColor.RED + "Your level is too low to use this item!");
			} else {
				PlayerCharacterUseConsumableItemEvent consumableEvent = new PlayerCharacterUseConsumableItemEvent(pc,
						consumable);
				EventManager.callEvent(consumableEvent);
			}
		}
		player.getInventory().setHeldItemSlot(0);
	}

	@EventHandler
	private void onThrowItem(PlayerDropItemEvent event) {
		org.bukkit.entity.Item itemEntity = event.getItemDrop();
		ItemStack itemStack = itemEntity.getItemStack();
		if (ItemFactory.staticInteractables.contains(itemStack)) {
			event.setCancelled(true);
			return;
		}
		Item item = Item.forItemStack(itemStack);
		if (item == null) {
			itemEntity.remove();
			return;
		}
		Player player = (Player) event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		droppedItemMap.put(itemEntity, pc);
	}

	@EventHandler
	private void onPickupItem(PlayerPickupItemEvent event) {
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		org.bukkit.entity.Item item = event.getItem();
		if (droppedItemMap.get(item) == pc) {
			droppedItemMap.remove(item);
		} else {
			event.setCancelled(true);
		}
	}

	@EventHandler
	private void onItemDespawn(ItemDespawnEvent event) {
		org.bukkit.entity.Item item = event.getEntity();
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
