package com.plux.party.listeners;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class PartyChatListener implements Listener {

    private final PluxParty plugin;

    public PartyChatListener(PluxParty plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        String prefix = plugin.getConfigManager().getString("party.chat-prefix", "@");
        if (!message.startsWith(prefix)) {
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            return;
        }

        String actualMessage = message.substring(prefix.length()).trim();
        if (actualMessage.isEmpty()) {
            return;
        }

        event.setCancelled(true);
        String format = plugin.getConfigManager().getMessage("party.chat-format");
        party.broadcast(format.replace("%player%", player.getName()).replace("%message%", actualMessage));
    }
}
