package com.plux.party.placeholder;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PartyPlaceholderExpansion extends PlaceholderExpansion {

    private final PluxParty plugin;

    public PartyPlaceholderExpansion(PluxParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "party";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Plux";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());

        switch (identifier) {
            case "name":
                return party != null ? party.getName() : "";
            case "members":
                return party != null ? String.valueOf(party.getMemberCount()) : "0";
            case "online":
                return party != null ? String.valueOf(party.getOnlineMemberCount()) : "0";
            case "leader":
                return party != null ? Bukkit.getOfflinePlayer(party.getLeader()).getName() : "";
            case "ready":
                return party != null ? party.getOnlineMemberCount() + "/" + party.getMemberCount() : "0/0";
            case "in_party":
                return party != null ? "true" : "false";
            default:
                return "";
        }
    }
}
