package com.plux.party.party;

import com.plux.party.PluxParty;
import com.plux.party.api.events.PartyDisbandEvent;
import com.plux.party.api.events.PartyMemberJoinEvent;
import com.plux.party.api.events.PartyMemberLeaveEvent;
import com.plux.party.api.events.PartyTransferEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerParty implements Party {

    private final String id;
    private String name;
    private UUID leader;
    private final Set<UUID> members;
    private final PluxParty plugin;
    private final Map<UUID, Long> offlineTime;

    public PlayerParty(String id, String name, UUID leader, PluxParty plugin) {
        this.id = id;
        this.name = name;
        this.leader = leader;
        this.members = ConcurrentHashMap.newKeySet();
        this.plugin = plugin;
        this.offlineTime = new ConcurrentHashMap<>();
        this.members.add(leader);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public UUID getLeader() {
        return leader;
    }

    @Override
    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    @Override
    public Collection<UUID> getMembers() {
        return Collections.unmodifiableSet(members);
    }

    @Override
    public Collection<Player> getOnlineMembers() {
        List<Player> online = new ArrayList<>();
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                online.add(player);
            }
        }
        return online;
    }

    @Override
    public int getMemberCount() {
        return members.size();
    }

    @Override
    public int getOnlineMemberCount() {
        int count = 0;
        for (UUID uuid : members) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean isLeader(UUID player) {
        return leader.equals(player);
    }

    @Override
    public boolean isMember(UUID player) {
        return members.contains(player);
    }

    @Override
    public boolean isFull() {
        int maxMembers = plugin.getConfigManager().getInt("party.max-members", 8);
        return members.size() >= maxMembers;
    }

    @Override
    public boolean addMember(UUID player) {
        if (isFull()) return false;
        if (members.contains(player)) return false;

        members.add(player);
        offlineTime.remove(player);

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            PartyMemberJoinEvent event = new PartyMemberJoinEvent(this, bukkitPlayer);
            Bukkit.getPluginManager().callEvent(event);
        }

        return true;
    }

    @Override
    public boolean removeMember(UUID player) {
        if (!members.contains(player)) return false;

        boolean wasLeader = isLeader(player);
        members.remove(player);
        offlineTime.remove(player);

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null && bukkitPlayer.isOnline()) {
            PartyMemberLeaveEvent event = new PartyMemberLeaveEvent(this, bukkitPlayer, wasLeader);
            Bukkit.getPluginManager().callEvent(event);
        }

        if (wasLeader && !members.isEmpty()) {
            UUID newLeader = members.iterator().next();
            transferLeadership(newLeader);
        } else if (members.isEmpty()) {
            disband();
        }

        return true;
    }

    @Override
    public void kickMember(UUID player) {
        removeMember(player);
    }

    @Override
    public void disband() {
        PartyDisbandEvent event = new PartyDisbandEvent(this);
        Bukkit.getPluginManager().callEvent(event);

        for (UUID uuid : new ArrayList<>(members)) {
            plugin.getPartyManager().removePlayerFromParty(uuid);
        }

        plugin.getPartyManager().removeParty(id);
    }

    @Override
    public void broadcast(String message) {
        for (Player player : getOnlineMembers()) {
            player.sendMessage(message);
        }
    }

    @Override
    public void broadcastWithout(UUID exclude, String message) {
        for (Player player : getOnlineMembers()) {
            if (!player.getUniqueId().equals(exclude)) {
                player.sendMessage(message);
            }
        }
    }

    @Override
    public void transferLeadership(UUID newLeader) {
        UUID oldLeader = this.leader;
        this.leader = newLeader;

        Player newLeaderPlayer = Bukkit.getPlayer(newLeader);
        if (newLeaderPlayer != null && newLeaderPlayer.isOnline()) {
            PartyTransferEvent event = new PartyTransferEvent(this, oldLeader, newLeader);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    public void setOffline(UUID player) {
        offlineTime.put(player, System.currentTimeMillis());
    }

    public void setOnline(UUID player) {
        offlineTime.remove(player);
    }

    public long getLeaderOfflineTime() {
        Long time = offlineTime.get(leader);
        return time != null ? System.currentTimeMillis() - time : 0;
    }

    public boolean isLeaderOnline() {
        Player player = Bukkit.getPlayer(leader);
        return player != null && player.isOnline();
    }
}