package com.mcmmorpg.common.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.item.Weapon;

/**
 * An event called when a player uses a weapon. Use this event to add effects to
 * weapons.
 */
public class PlayerCharacterUseWeaponEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final PlayerCharacter pc;
    private final Weapon weapon;

    public PlayerCharacterUseWeaponEvent(PlayerCharacter pc, Weapon weapon) {
        this.pc = pc;
        this.weapon = weapon;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    /**
     * Returns the player character that used the weapon.
     */
    public PlayerCharacter getPlayerCharacter() {
        return pc;
    }

    /**
     * Returns the weapon used in this event.
     */
    public Weapon getWeapon() {
        return weapon;
    }
}
