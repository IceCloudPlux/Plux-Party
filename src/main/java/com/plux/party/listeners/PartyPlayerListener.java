package com.plux.party.listeners;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import com.plux.party.party.PlayerParty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PartyPlayerListener implements Listener {

    private final PluxParty plugin;

    public PartyPlayerListener(PluxParty plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());

        if (party == null) {
            return;
        }

        if (party instanceof PlayerParty) {
            ((PlayerParty) party).setOffline(player.getUniqueId());
        }

        party.broadcastWithout(player.getUniqueId(), plugin.getConfigManager().getMessage("party.member-left").replace("%player%", player.getName()));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());

        if (party == null) {
            return;
        }

        if (party instanceof PlayerParty) {
            ((PlayerParty) party).setOnline(player.getUniqueId());
        }
    }
}