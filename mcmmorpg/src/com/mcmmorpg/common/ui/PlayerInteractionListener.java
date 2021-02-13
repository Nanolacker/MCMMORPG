package com.mcmmorpg.common.ui;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerAnimationType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.event.PlayerCharacterDropItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterPickUpItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseConsumableItemEvent;
import com.mcmmorpg.common.event.PlayerCharacterUseWeaponEvent;
import com.mcmmorpg.common.item.ArmorItem;
import com.mcmmorpg.common.item.ConsumableItem;
import com.mcmmorpg.common.item.Item;
import com.mcmmorpg.common.item.Weapon;
import com.mcmmorpg.common.playerClass.PlayerClass;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.time.DelayedTask;

public class PlayerInteractionListener implements Listener {
    private static final Noise EQUIP_NOISE = new Noise(Sound.ITEM_ARMOR_EQUIP_CHAIN);

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
    private void tempAddPCToSet(Set<PlayerCharacter> set, PlayerCharacter pc) {
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
        PlayerCharacterMap map = pc.getMap();
        if (map.isOpen()) {
            map.close();
        }
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHeldItemSlot() != 0) {
            // make sure they click with weapon in hand
            inventory.setHeldItemSlot(0);
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

        Button button = Button.forItemStack(itemStack);
        if (button != null) {
            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                button.onInteract(player);
            }
            return;
        }
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc == null) {
            return;
        }

        if (action != Action.LEFT_CLICK_AIR && action != Action.LEFT_CLICK_BLOCK) {
            tempAddPCToSet(falseAttackers, pc);
        }
    }

    @EventHandler
    private void onDropItem(PlayerDropItemEvent event) {
        org.bukkit.entity.Item itemEntity = event.getItemDrop();
        ItemStack itemStack = itemEntity.getItemStack();

        Button button = Button.forItemStack(itemStack);
        if (button != null) {
            event.setCancelled(true);
            return;
        }

        Item item = Item.forItemStack(itemStack);
        if (item == null) {
            itemEntity.remove();
        } else {
            Player player = event.getPlayer();
            PlayerCharacter pc = PlayerCharacter.forPlayer(player);
            tempAddPCToSet(falseAttackers, pc);
            if (player.getInventory().getItem(0) == null) {
                // when the player tries to throw their weapon
                event.setCancelled(true);
            } else {
                itemEntity.setCustomName(item.getRarity().getColor() + item.getName());
                itemEntity.setCustomNameVisible(true);
                int amount = itemStack.getAmount();
                if (amount == 1) {
                    pc.sendMessage(ChatColor.GRAY + "Dropped " + item);
                } else {
                    pc.sendMessage(ChatColor.GRAY + "Dropped " + item + ChatColor.GRAY + " (" + amount + ")");
                }
                PlayerCharacterDropItemEvent dropItemEvent = new PlayerCharacterDropItemEvent(pc, item, amount);
                EventManager.callEvent(dropItemEvent);
            }
        }
    }

    @EventHandler
    private void onPickupItem(EntityPickupItemEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            PlayerCharacter pc = PlayerCharacter.forPlayer((Player) entity);
            if (pc == null) {
                return;
            }
            org.bukkit.entity.Item itemEntity = event.getItem();
            ItemStack itemStack = itemEntity.getItemStack();
            Item item = Item.forItemStack(itemStack);
            if (item != null) {
                int amount = itemStack.getAmount();
                if (amount == 1) {
                    pc.sendMessage(ChatColor.GRAY + "Picked up " + item);
                } else {
                    pc.sendMessage(ChatColor.GRAY + "Picked up " + item + ChatColor.GRAY + " (" + amount + ")");
                }
                PlayerCharacterPickUpItemEvent pickUpItemEvent = new PlayerCharacterPickUpItemEvent(pc, item, amount);
                EventManager.callEvent(pickUpItemEvent);
            }
        }
    }

    private void handlePlayerCharacterUseWeapon(PlayerCharacter pc) {
        if (swingingHands.contains(pc)) {
            return;
        }
        tempAddPCToSet(swingingHands, pc);
        if (pc.isDisarmed()) {
            return;
        }
        Weapon weapon = pc.getWeapon();
        PlayerCharacterUseWeaponEvent useWeaponEvent = new PlayerCharacterUseWeaponEvent(pc, weapon);
        EventManager.callEvent(useWeaponEvent);
    }

    @EventHandler
    private void onClickItem(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItemStack = event.getCurrentItem();
        ItemStack cursorItemStack = event.getCursor();

        Button button = Button.forItemStack(clickedItemStack);
        if (button != null) {
            event.setCancelled(true);
            if (cursorItemStack.getType() == Material.AIR) {
                button.onInteract(player);
                return;
            }
        }

        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc == null) {
            return;
        }

        int rawSlot = event.getRawSlot();
        if (rawSlot == 36 || rawSlot == 45 || (rawSlot < 9 && rawSlot >= 0)) {
            // don't let the player fiddle with throwing away their equipment
            // 45 = offhand
            event.setCancelled(true);
        }

        Item clickedItem = Item.forItemStack(clickedItemStack);
        if (event.isShiftClick()) {
            if (event.getClickedInventory() == player.getInventory()) {
                event.setCancelled(true);
                if (clickedItem instanceof ConsumableItem) {
                    ConsumableItem consumable = (ConsumableItem) clickedItem;
                    handlePlayerCharacterUseConsumable(pc, consumable, clickedItemStack);
                } else if (clickedItem instanceof Weapon) {
                    if (rawSlot == 36 || clickedItem == pc.getWeapon()) {
                        pc.sendMessage(clickedItem.toString() + ChatColor.GRAY + " is already equipped");
                    } else {
                        Weapon weapon = (Weapon) clickedItem;
                        PlayerClass weaponPlayerClass = weapon.getPlayerClass();
                        int weaponLevel = weapon.getLevel();
                        if (pc.getPlayerClass() != weaponPlayerClass) {
                            pc.sendMessage(ChatColor.GRAY + "Only " + ChatColor.GOLD + weaponPlayerClass.getName()
                                    + "s " + ChatColor.GRAY + "can wield " + weapon);
                        } else if (pc.getLevel() < weaponLevel) {
                            pc.sendMessage(ChatColor.GRAY + "You must be " + ChatColor.GOLD + "level " + weaponLevel
                                    + ChatColor.GRAY + " to wield " + weapon);
                        } else {
                            pc.getMap().close();
                            // equip new weapon
                            Inventory inventory = player.getInventory();
                            // must go back to non-raw slots here
                            int slot = event.getSlot();
                            ItemStack currentWeaponItemStack = inventory.getItem(0);
                            inventory.setItem(0, clickedItemStack);
                            inventory.setItem(slot, currentWeaponItemStack);
                            pc.sendMessage(ChatColor.GRAY + "Equipped " + weapon);
                            EQUIP_NOISE.play(pc);
                        }
                    }
                    Noise.CLICK.play(player);
                } else if (clickedItem instanceof ArmorItem) {
                    ArmorItem armorItem = (ArmorItem) clickedItem;
                    Inventory inventory = player.getInventory();
                    int slot = event.getSlot();
                    if (rawSlot >= 5 && rawSlot < 9) {
                        // 5-8 = armor
                        // unequip
                        inventory.setItem(slot, null);
                        inventory.addItem(clickedItemStack);
                        pc.sendMessage(ChatColor.GRAY + "Unequipped " + armorItem);
                        EQUIP_NOISE.play(pc);
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
                            EQUIP_NOISE.play(pc);
                            inventory.setItem(slot, currentArmorItemStack);
                        }
                    }
                    Noise.CLICK.play(player);
                }
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
        if (rawSlot == 45 || rawSlot < 9) {
            // 45 = offhand
            // 5-8 = armor
            // don't let players drop items into their equipment slots
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
        if (pc.getLevel() < consumable.getLevel()) {
            pc.sendMessage(ChatColor.GRAY + "Your level is too low to use this item");
        } else {
            itemStack.setAmount(itemStack.getAmount() - 1);
            pc.sendMessage(ChatColor.GRAY + "Used " + consumable);
            PlayerCharacterUseConsumableItemEvent consumableEvent = new PlayerCharacterUseConsumableItemEvent(pc,
                    consumable);
            EventManager.callEvent(consumableEvent);
        }
        Noise.CLICK.play(pc);
    }

    @EventHandler
    private void onPressF(PlayerSwapHandItemsEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        PlayerInventory inventory = player.getInventory();
        if (inventory.getHeldItemSlot() != 0) {
            // to prevent map duplication
            inventory.setHeldItemSlot(0);
        }
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        if (pc == null) {
            return;
        }
        PlayerCharacterMap map = pc.getMap();
        if (map.isOpen()) {
            map.close();
        } else {
            map.open();
        }
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);
    }

    @EventHandler
    private void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage(null);
    }
}
