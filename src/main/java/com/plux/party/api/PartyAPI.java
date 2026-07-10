package com.plux.party.api;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public class PartyAPI {

    private final PluxParty plugin;

    public PartyAPI(PluxParty plugin) {
        this.plugin = plugin;
    }

    public Party getParty(Player player) {
        return plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
    }

    public Party getParty(UUID playerId) {
        return plugin.getPartyManager().getPartyByPlayer(playerId);
    }

    public Party getPartyById(String id) {
        return plugin.getPartyManager().getParty(id);
    }

    public Collection<Party> getAllParties() {
        return plugin.getPartyManager().getParties();
    }

    public Collection<Party> getOpenParties() {
        return plugin.getPartyManager().getOpenParties();
    }

    public boolean isInParty(Player player) {
        return plugin.getPartyManager().isInParty(player.getUniqueId());
    }

    public boolean isInParty(UUID playerId) {
        return plugin.getPartyManager().isInParty(playerId);
    }

    public Party createParty(Player leader) {
        return plugin.getPartyManager().createParty(leader.getUniqueId(), null);
    }

    public Party createParty(Player leader, String name) {
        return plugin.getPartyManager().createParty(leader.getUniqueId(), name);
    }

    public boolean invitePlayer(Player inviter, Player invitee) {
        if (!isInParty(inviter)) return false;
        Party party = getParty(inviter);
        if (!party.isLeader(inviter.getUniqueId())) return false;
        if (party.isFull()) return false;
        
        plugin.getPartyManager().sendInvite(inviter.getUniqueId(), invitee.getUniqueId());
        return true;
    }

    public boolean acceptInvite(Player invitee, Player inviter) {
        return plugin.getPartyManager().acceptInvite(invitee.getUniqueId(), inviter.getUniqueId());
    }

    public boolean kickPlayer(Player kicker, Player target) {
        if (!isInParty(kicker)) return false;
        Party party = getParty(kicker);
        if (!party.isLeader(kicker.getUniqueId())) return false;
        if (!party.isMember(target.getUniqueId())) return false;

        party.kickMember(target.getUniqueId());
        plugin.getPartyManager().removePlayerFromParty(target.getUniqueId());
        return true;
    }

    public boolean transferLeadership(Player oldLeader, Player newLeader) {
        if (!isInParty(oldLeader)) return false;
        Party party = getParty(oldLeader);
        if (!party.isLeader(oldLeader.getUniqueId())) return false;
        if (!party.isMember(newLeader.getUniqueId())) return false;

        party.transferLeadership(newLeader.getUniqueId());
        return true;
    }

    public void disbandParty(Party party) {
        party.disband();
    }

}
