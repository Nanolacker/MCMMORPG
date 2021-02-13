package com.mcmmorpg.common.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.entity.Player;

import com.mcmmorpg.common.time.RepeatingTask;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Display text on a player's action bar.
 */
public class ActionBarText {
    private static final double APPLY_TASK_PERIOD_SECONDS = 2.0;

    private static final Map<Player, ActionBarText> actionBarMap = new HashMap<>();

    static {
        RepeatingTask applyTask = new RepeatingTask(APPLY_TASK_PERIOD_SECONDS) {
            @Override
            public void run() {
                Set<Player> keySet = actionBarMap.keySet();
                for (Player player : keySet) {
                    ActionBarText text = actionBarMap.get(player);
                    text.apply0(player);
                }
            }
        };
        applyTask.schedule();
    }

    private BaseComponent[] text;

    /**
     * Display text on a player's action bar.
     */
    public ActionBarText(String text) {
        this.text = TextComponent.fromLegacyText(text);
    }

    /**
     * Applies to the player for an infinite duration, or until another action bar
     * is applied.
     */
    public void apply(Player player) {
        actionBarMap.put(player, this);
        // Needs initial manual application.
        apply0(player);
    }

    private void apply0(Player player) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, text);
    }

    /**
     * Clears the action bar of any text for the player.
     */
    public static void clear(Player player) {
        actionBarMap.remove(player);
    }
}
