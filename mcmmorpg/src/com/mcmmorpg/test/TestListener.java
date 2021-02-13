package com.mcmmorpg.test;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.persistence.PersistentPlayerCharacterData;

public class TestListener implements Listener {
    @EventHandler
    private void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PersistentPlayerCharacterData saveData = PersistentPlayerCharacterData.createFreshSaveData(player,
                Constants.TEST_PLAYER_CLASS, "Test", Constants.TEST_SPAWN_LOCATION, Constants.TEST_WEAPON);
        PlayerCharacter pc = PlayerCharacter.registerPlayerCharacter(player, saveData);
        pc.getChestArmor();
    }

    @EventHandler
    private void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerCharacter pc = PlayerCharacter.forPlayer(player);
        pc.remove();
    }
}
