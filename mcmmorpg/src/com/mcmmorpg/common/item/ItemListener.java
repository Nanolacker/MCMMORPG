package com.mcmmorpg.common.item;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterOpenLootChestEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.event.StaticInteractableEvent;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

class ItemListener implements Listener {

	private static final Noise CLICK_NOISE = new Noise(Sound.BLOCK_LEVER_CLICK);

	/**
	 * Used to ensure that players only use weapons once when intended.
	 */
	private final Set<PlayerCharacter> swingingHands = new HashSet<>();
	/**
	 * Used to ensure that PlayerAnimationEvents are being used correctly.
	 */
	private final Set<PlayerCharacter> falseAttackers = new HashSet<>();

	/**
	 * Removes the player character after a very short duration.
	 */
	private void addPCToSet(Set<PlayerCharacter> set, PlayerCharacter pc) {
		set.add(pc);
		new DelayedTask(0.1) {
			@Override
			protected void run() {
				set.remove(pc);
			}
		}.schedule();
	}

	@EventHandler
	private void onSwingArm(PlayerAnimationEvent event) {
		if (event.getAnimationType() != PlayerAnimationType.ARM_SWING) {
			// future proof
			return;
		}
		Player player = event.getPlayer();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		if (falseAttackers.contains(pc)) {
			return;
		}
		handlePlayerCharacterUseWeapon(pc);
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

		if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
			addPCToSet(falseAttackers, pc);
		}
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
		} else {
			Player player = event.getPlayer();
			if (player.getInventory().getItem(0) == null) {
				// when the player tries to throw their weapon
				event.setCancelled(true);
			}
			PlayerCharacter pc = PlayerCharacter.forPlayer(player);
			addPCToSet(falseAttackers, pc);
			itemEntity.setCustomName(item.getRarity().getColor() + item.getName());
			itemEntity.setCustomNameVisible(true);
		}
	}

	private void handlePlayerCharacterUseWeapon(PlayerCharacter pc) {
		if (swingingHands.contains(pc)) {
			return;
		}
		addPCToSet(swingingHands, pc);
		Weapon weapon = pc.getWeapon();
		if (pc.isDisarmed()) {
			return;
		}
		EventManager.callEvent(new PlayerCharacterUseWeaponEvent(pc, weapon));
	}

	@EventHandler
	private void onClickItem(InventoryClickEvent event) {
		Player player = (Player) event.getWhoClicked();
		ItemStack clickedItemStack = event.getCurrentItem();
		ItemStack cursorItemStack = event.getCursor();
		if (ItemFactory.staticInteractables.contains(clickedItemStack)) {
			event.setCancelled(true);
			if (cursorItemStack.getType() == Material.AIR) {
				EventManager.callEvent(new StaticInteractableEvent(player, clickedItemStack));
				return;
			}
		}

		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}

		int rawSlot = event.getRawSlot();
		if (rawSlot == 36 || rawSlot == 45 || (rawSlot >= 5 && rawSlot < 8)) {
			// don't let the player fiddle with throwing away their equipment
			// 45 = offhand
			event.setCancelled(true);
		}

		Item clickedItem = Item.forItemStack(clickedItemStack);
		if (event.isShiftClick()) {
			event.setCancelled(true);
			if (clickedItem instanceof ConsumableItem) {
				ConsumableItem consumable = (ConsumableItem) clickedItem;
				handlePlayerCharacterUseConsumable(pc, consumable, clickedItemStack);
			} else if (clickedItem instanceof Weapon) {
				if (rawSlot == 36 || clickedItem == pc.getWeapon()) {
					pc.sendMessage(clickedItem + " is already equipped");
				} else {
					Weapon weapon = (Weapon) clickedItem;
					PlayerClass weaponPlayerClass = weapon.getPlayerClass();
					int weaponLevel = weapon.getLevel();
					if (pc.getPlayerClass() != weaponPlayerClass) {
						pc.sendMessage(ChatColor.GRAY + "Only " + ChatColor.GOLD + weaponPlayerClass.getName() + "s "
								+ ChatColor.GRAY + "can wield " + weapon);
					} else if (pc.getLevel() < weaponLevel) {
						pc.sendMessage(ChatColor.GRAY + "You must be " + ChatColor.GOLD + "level " + weaponLevel
								+ ChatColor.GRAY + " to wield " + weapon);
					} else {
						// equip new weapon
						Inventory inventory = player.getInventory();
						// must go back to non-raw slots here
						int slot = event.getSlot();
						ItemStack currentWeaponItemStack = inventory.getItem(0);
						inventory.setItem(0, clickedItemStack);
						inventory.setItem(slot, currentWeaponItemStack);
						pc.sendMessage(ChatColor.GRAY + "Equipped " + weapon);
					}
				}
				CLICK_NOISE.play(player);
			} else if (clickedItem instanceof ArmorItem) {
				ArmorItem armorItem = (ArmorItem) clickedItem;
				Inventory inventory = player.getInventory();
				int slot = event.getSlot();
				if (rawSlot >= 5 && rawSlot < 9) {
					// unequip
					inventory.setItem(slot, null);
					inventory.addItem(clickedItemStack);
					pc.sendMessage(ChatColor.GRAY + "Unequipped " + armorItem);
				} else {
					// equip
					PlayerClass armorPlayerClass = armorItem.getPlayerClass();
					int weaponLevel = armorItem.getLevel();
					if (pc.getPlayerClass() != armorPlayerClass) {
						pc.sendMessage(ChatColor.GRAY + "Only " + ChatColor.GOLD + armorPlayerClass.getName() + "s "
								+ ChatColor.GRAY + "can equip " + armorItem);
					} else if (pc.getLevel() < weaponLevel) {
						pc.sendMessage(ChatColor.GRAY + "You must be " + ChatColor.GOLD + "level " + weaponLevel
								+ ChatColor.GRAY + " to equip " + armorItem);
					} else {
						inventory.setItem(slot, null);
						int armorSlot;
						switch (armorItem.getType()) {
						case FEET:
							armorSlot = 36;
							break;
						case LEGS:
							armorSlot = 37;
							break;
						case CHEST:
							armorSlot = 38;
							break;
						case HEAD:
							armorSlot = 39;
							break;
						default:
							armorSlot = -1;
							break;
						}
						ItemStack currentArmorItemStack = inventory.getItem(armorSlot);
						inventory.setItem(armorSlot, clickedItemStack);
						pc.sendMessage(ChatColor.GRAY + "Equipped " + armorItem);
						inventory.setItem(slot, currentArmorItemStack);
					}
				}
				CLICK_NOISE.play(player);
			}
		}
	}

	@EventHandler
	private void onDragItem(InventoryDragEvent event) {
		Player player = (Player) event.getWhoClicked();
		PlayerCharacter pc = PlayerCharacter.forPlayer(player);
		if (pc == null) {
			return;
		}
		int rawSlot = (int) event.getRawSlots().toArray()[0];
		if (rawSlot == 45 || (rawSlot >= 5 && rawSlot < 8)) {
			// 45 = offhand
			event.setCancelled(true);
		}
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
			// do nothing
		} else if (pc.getLevel() < consumable.getLevel()) {
			pc.sendMessage(ChatColor.GRAY + "Your level is too low to use this item");
		} else {
			itemStack.setAmount(itemStack.getAmount() - 1);
			pc.sendMessage(ChatColor.GRAY + "Used " + consumable);
			PlayerCharacterUseConsumableItemEvent consumableEvent = new PlayerCharacterUseConsumableItemEvent(pc,
					consumable);
			EventManager.callEvent(consumableEvent);
		}
		CLICK_NOISE.play(pc);
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
		chest.open(pc);
		chest.remove();
		EventManager.callEvent(new PlayerCharacterOpenLootChestEvent(pc, chest));
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

	@EventHandler
	private void onSwapHands(PlayerSwapHandItemsEvent event) {
		event.setCancelled(true);
	}

}
