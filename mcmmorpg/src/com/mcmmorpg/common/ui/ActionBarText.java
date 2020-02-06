package com.mcmmorpg.common.ui;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.time.RepeatingTask;

import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;

public class ActionBarText {

	private static final double APPLY_TASK_PERIOD_SECONDS = 2.0;

	private static final Map<Player, ActionBarText> actionBarMap = new HashMap<>();

	static {
		RepeatingTask applyTask = new RepeatingTask(APPLY_TASK_PERIOD_SECONDS) {
			@Override
			public void run() {
				for (Player player : actionBarMap.keySet()) {
					ActionBarText text = actionBarMap.get(player);
					text.apply0(player);
				}
			}
		};
		applyTask.schedule();
	}

	private PacketPlayOutChat packet;

	public ActionBarText(String text) {
		packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.a((byte) 2));
	}

	/**
	 * Applies to the player for an infinite duration, or until another action bar
	 * is applied.
	 */
	public void apply(Player player) {
		actionBarMap.put(player, this);
		// Need that initial manual application.
		apply0(player);
	}

	private void apply0(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
