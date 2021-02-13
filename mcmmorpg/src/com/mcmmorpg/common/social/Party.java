package com.mcmmorpg.common.social;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import com.mcmmorpg.common.character.PlayerCharacter;

public final class Party {
    private final List<PlayerCharacter> members;

    private Party(PlayerCharacter leader) {
        this.members = new ArrayList<>();
        members.add(leader);
        leader.setParty(this);
    }

    public static Party create(PlayerCharacter leader) {
        Party party = new Party(leader);
        leader.sendMessage(ChatColor.GREEN + "Party created!");
        return party;
    }

    public PlayerCharacter getLeader() {
        return members.get(0);
    }

    public void setLeader(PlayerCharacter leader) {
        PlayerCharacter currentLeader = members.get(0);
        if (leader == currentLeader) {
            throw new IllegalArgumentException("New leader is already the leader");
        }
        if (!members.contains(leader)) {
            throw new IllegalArgumentException("New leader must be a party member before being promoted");
        }
        members.add(0, leader);
        announce(ChatColor.GREEN + leader.getName() + " is now the party leader!");
    }

    public PlayerCharacter[] getMembers() {
        return members.toArray(new PlayerCharacter[members.size()]);
    }

    public void add(PlayerCharacter member) {
        if (members.contains(member)) {
            throw new IllegalArgumentException("Party already contains member");
        }
        members.add(member);
        announce(ChatColor.GREEN + member.getName() + " has joined the party!");
        member.setParty(this);
    }

    public void remove(PlayerCharacter member) {
        if (!members.contains(member)) {
            throw new IllegalArgumentException("Party does not contain member");
        }
        PlayerCharacter currentLeader = members.get(0);
        members.remove(member);
        announce(ChatColor.RED + member.getName() + " has left the party.");
        if (member == currentLeader) {
            if (members.isEmpty()) {
                disband();
            } else {
                PlayerCharacter newLeader = members.get(0);
                announce(newLeader.getName() + " is now the party leader!");
            }
        }
    }

    public void disband() {
        announce(ChatColor.RED + "The party has been disbanded.");
        for (PlayerCharacter member : members) {
            member.setParty(null);
        }
    }

    public void announce(String message) {
        for (PlayerCharacter member : members) {
            member.sendMessage(message);
        }
    }
}
