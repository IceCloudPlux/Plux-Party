package com.plux.party.party;

import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface Party {

    String getId();

    String getName();

    void setName(String name);

    UUID getLeader();

    void setLeader(UUID leader);

    Collection<UUID> getMembers();

    Collection<Player> getOnlineMembers();

    int getMemberCount();

    int getOnlineMemberCount();

    boolean isLeader(UUID player);

    boolean isMember(UUID player);

    boolean isFull();

    boolean addMember(UUID player);

    boolean removeMember(UUID player);

    void kickMember(UUID player);

    void disband();

    void broadcast(String message);

    void broadcastWithout(UUID exclude, String message);

    void transferLeadership(UUID newLeader);
}