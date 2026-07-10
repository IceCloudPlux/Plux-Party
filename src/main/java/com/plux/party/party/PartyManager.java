package com.plux.party.party;

import com.plux.party.PluxParty;
import com.plux.party.api.events.PartyCreateEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private final PluxParty plugin;
    private final Map<String, Party> parties;
    private final Map<UUID, String> playerPartyMap;
    private final Map<UUID, List<Invite>> pendingInvites;

    public PartyManager(PluxParty plugin) {
        this.plugin = plugin;
        this.parties = new ConcurrentHashMap<>();
        this.playerPartyMap = new ConcurrentHashMap<>();
        this.pendingInvites = new ConcurrentHashMap<>();
    }

    public Party createParty(UUID leader, String name) {
        if (playerPartyMap.containsKey(leader)) {
            return null;
        }

        String id = generateRandomId();
        Party party = new PlayerParty(id, id, leader, plugin);
        parties.put(id, party);
        playerPartyMap.put(leader, id);

        Player leaderPlayer = Bukkit.getPlayer(leader);
        if (leaderPlayer != null && leaderPlayer.isOnline()) {
            PartyCreateEvent event = new PartyCreateEvent(party, leaderPlayer);
            Bukkit.getPluginManager().callEvent(event);
        }

        return party;
    }

    private String generateRandomId() {
        Random random = new Random();
        String id;
        do {
            id = String.format("%06d", random.nextInt(1000000));
        } while (parties.containsKey(id));
        return id;
    }

    public Party getParty(String id) {
        return parties.get(id);
    }

    public Party getPartyByPlayer(UUID player) {
        String partyId = playerPartyMap.get(player);
        if (partyId == null) return null;
        return parties.get(partyId);
    }

    public Collection<Party> getParties() {
        return Collections.unmodifiableCollection(parties.values());
    }

    public Collection<Party> getOpenParties() {
        List<Party> open = new ArrayList<>();
        for (Party party : parties.values()) {
            if (!party.isFull()) {
                open.add(party);
            }
        }
        return open;
    }

    public boolean removeParty(String id) {
        Party party = parties.remove(id);
        if (party != null) {
            for (UUID member : party.getMembers()) {
                playerPartyMap.remove(member);
            }
            return true;
        }
        return false;
    }

    public void removePlayerFromParty(UUID player) {
        String partyId = playerPartyMap.remove(player);
        if (partyId != null) {
            Party party = parties.get(partyId);
            if (party != null) {
                party.removeMember(player);
            }
        }
    }

    public void sendInvite(UUID inviter, UUID invitee) {
        Player inviterPlayer = Bukkit.getPlayer(inviter);
        Player inviteePlayer = Bukkit.getPlayer(invitee);

        if (inviterPlayer == null || !inviterPlayer.isOnline()) return;
        if (inviteePlayer == null || !inviteePlayer.isOnline()) return;
        if (inviter.equals(invitee)) return;
        if (playerPartyMap.containsKey(invitee)) return;

        Party party = getPartyByPlayer(inviter);
        if (party == null || !party.isLeader(inviter)) return;
        if (party.isFull()) return;

        List<Invite> invites = pendingInvites.computeIfAbsent(invitee, k -> new ArrayList<>());
        int maxPending = plugin.getConfigManager().getInt("invites.max-pending", 5);
        if (invites.size() >= maxPending) return;

        Invite existing = invites.stream()
                .filter(i -> i.getPartyId().equals(party.getId()))
                .findFirst()
                .orElse(null);
        if (existing != null) return;

        Invite invite = new Invite(party.getId(), inviter, System.currentTimeMillis());
        invites.add(invite);

        int timeout = plugin.getConfigManager().getInt("invites.timeout", 60);
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            List<Invite> current = pendingInvites.get(invitee);
            if (current != null) {
                current.remove(invite);
            }
        }, timeout * 20L);
    }

    public boolean acceptInvite(UUID invitee, UUID inviter) {
        List<Invite> invites = pendingInvites.get(invitee);
        if (invites == null || invites.isEmpty()) return false;

        Invite invite = invites.stream()
                .filter(i -> i.getInviter().equals(inviter))
                .findFirst()
                .orElse(null);

        if (invite == null) return false;

        Party party = getParty(invite.getPartyId());
        if (party == null) {
            invites.remove(invite);
            return false;
        }
        if (party.isFull()) {
            invites.remove(invite);
            return false;
        }

        invites.remove(invite);
        boolean success = party.addMember(invitee);
        if (success) {
            playerPartyMap.put(invitee, party.getId());
        }
        return success;
    }

    public boolean denyInvite(UUID invitee, UUID inviter) {
        List<Invite> invites = pendingInvites.get(invitee);
        if (invites == null || invites.isEmpty()) return false;

        Invite invite = invites.stream()
                .filter(i -> i.getInviter().equals(inviter))
                .findFirst()
                .orElse(null);

        if (invite == null) return false;

        invites.remove(invite);
        return true;
    }

    public List<Invite> getPendingInvites(UUID player) {
        return pendingInvites.getOrDefault(player, Collections.emptyList());
    }

    public Party quickMatch(UUID player) {
        if (playerPartyMap.containsKey(player)) {
            return null;
        }

        Collection<Party> openParties = getOpenParties();
        if (openParties.isEmpty()) {
            return createParty(player, null);
        }

        Party target = null;
        int maxMembers = Integer.MAX_VALUE;
        for (Party party : openParties) {
            if (!party.isLeader(player) && party.getMemberCount() < maxMembers) {
                maxMembers = party.getMemberCount();
                target = party;
            }
        }

        if (target != null) {
            if (target.addMember(player)) {
                playerPartyMap.put(player, target.getId());
                return target;
            }
        }

        return createParty(player, null);
    }

    public boolean isInParty(UUID player) {
        return playerPartyMap.containsKey(player);
    }

    public void disbandAll() {
        for (Party party : new ArrayList<>(parties.values())) {
            party.disband();
        }
        parties.clear();
        playerPartyMap.clear();
        pendingInvites.clear();
    }

    public static class Invite {
        private final String partyId;
        private final UUID inviter;
        private final long timestamp;

        public Invite(String partyId, UUID inviter, long timestamp) {
            this.partyId = partyId;
            this.inviter = inviter;
            this.timestamp = timestamp;
        }

        public String getPartyId() {
            return partyId;
        }

        public UUID getInviter() {
            return inviter;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}