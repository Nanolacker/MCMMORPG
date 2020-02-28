package com.mcmmorpg.common.item;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;
import com.mcmmorpg.common.utils.Debug;

class ItemListener implements Listener {

	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

	/**
	 * Used to ensure that players only use weapons once when multiple types of
	 * events are fired.
	 */
	private final Set<PlayerCharacter> swingingHands = new HashSet<>();

	@EventHandler
	private void onClickItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItemStack = event.getCurrentItem();
		ItemStack cursorItemStack = event.getCursor();
		if (ItemFactory.staticInteractables.contains(clickedItemStack)) {
			event.setCancelled(true);
			if (cursorItemStack.getType() == Material.AIR) {
				StaticInteractableEvent consumableUseEvent = new StaticInteractableEvent(player, clickedItemStack);
				EventManager.callEvent(consumableUseEvent);
				return;
			}
		}

		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}

		Item clickedItem = Item.forItemStack(clickedItemStack);
		if (event.isShiftClick()) {
			event.setCancelled(true);
			if (clickedItem instanceof ConsumableItem) {
				ConsumableItem consumable = (ConsumableItem) clickedItem;
				handlePlayerCharacterUseConsumable(pc, consumable, clickedItemStack);
			}
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
	private void onSwapHands(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
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
		if (swingingHands.contains(pc)) {
			return;
		}
		swingingHands.add(pc);
		new DelayedTask(0.1) {
			@Override
			protected void run() {
				swingingHands.remove(pc);
			}
		}.schedule();
		Weapon weapon = pc.getWeapon();
		// if weapon == null, punch
		if (pc.isDisarmed()) {
			return;
		}
		if (weapon != null) {
			if (pc.getLevel() < weapon.getLevel() || pc.getPlayerClass() != weapon.getPlayerClass()) {
				pc.sendMessage(ChatColor.GRAY + "Unable to wield " + weapon);
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
			handlePlayerCharacterUseConsumable(pc, consumable, itemStack);
		}
		player.getInventory().setHeldItemSlot(0);
	}

	private void handlePlayerCharacterUseConsumable(PlayerCharacter pc, ConsumableItem consumable,
			ItemStack itemStack) {
		if (pc.isSilenced()) {
		} else if (pc.getLevel() < consumable.getLevel()) {
			pc.sendMessage(ChatColor.GRAY + "Your level is too low to use this item");
		} else {
			itemStack.setAmount(itemStack.getAmount() - 1);
			PlayerCharacterUseConsumableItemEvent consumableEvent = new PlayerCharacterUseConsumableItemEvent(pc,
					consumable);
			EventManager.callEvent(consumableEvent);
		}
		CLICK_NOISE.play(pc);
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
		}
	}

	@EventHandler
	private void onOpenLootChest(PlayerInteractEvent event) {
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
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
		PlayerCharacter owner = chest.getOwner();
		if (owner == null || chest.getOwner() == pc) {
			chest.open(pc);
			chest.remove();
		} else {
			pc.sendMessage(ChatColor.GRAY + "This chest belongs to " + owner.getName());
		}
	}

	@EventHandler
	private void onClickInLootChest(InventoryClickEvent event) {
		Inventory inventory = event.getInventory();
		Inventory clickedInventory = event.getClickedInventory();
		if (LootChest.inventories.contains(inventory)) {
			event.setCancelled(true);
		}
		if (LootChest.inventories.contains(clickedInventory)) {
			ItemStack itemStack = event.getCurrentItem();
			if (itemStack == null) {
				return;
			}
			int slot = event.getSlot();
			clickedInventory.setItem(slot, null);
			Player player = (Player) event.getWhoClicked();
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			// pc should never be null
			pc.getPlayer().getInventory().addItem(itemStack);
			boolean empty = inventoryIsEmpty(clickedInventory);
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
