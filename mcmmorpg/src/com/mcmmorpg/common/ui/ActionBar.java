package com.mcmmorpg.common.ui;

import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;

public class ActionBar {

	private PacketPlayOutChat packet;

	public ActionBar(String text) {
		packet = new PacketPlayOutChat(ChatSerializer.a("{\"text\":\"" + text + "\"}"), ChatMessageType.a((byte) 2));
	}

	public void apply(Player player) {
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

}
