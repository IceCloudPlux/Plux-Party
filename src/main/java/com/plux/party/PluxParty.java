package com.plux.party;

import com.plux.party.commands.PartyAdminCommand;
import com.plux.party.commands.PartyCommand;
import com.plux.party.config.ConfigManager;
import com.plux.party.listeners.PartyChatListener;
import com.plux.party.listeners.PartyPlayerListener;
import com.plux.party.party.Party;
import com.plux.party.party.PlayerParty;
import com.plux.party.party.PartyManager;
import com.plux.party.placeholder.PartyPlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class PluxParty extends JavaPlugin {

    private static PluxParty instance;
    private PartyManager partyManager;
    private ConfigManager configManager;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        configManager.loadAll();

        partyManager = new PartyManager(this);

        registerCommands();
        registerListeners();

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PartyPlaceholderExpansion(this).register();
        }

        startAutoDisbandTask();

        getLogger().info("PluxParty v" + getDescription().getVersion() + " 已加载！");
    }

    @Override
    public void onDisable() {
        if (partyManager != null) {
            partyManager.disbandAll();
        }
        getLogger().info("PluxParty 已卸载！");
    }

    private void startAutoDisbandTask() {
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            long maxOfflineTime = 10 * 60 * 1000L;

            for (Party party : partyManager.getParties()) {
                if (!(party instanceof PlayerParty)) continue;
                PlayerParty playerParty = (PlayerParty) party;

                boolean shouldDisband = false;

                if (!playerParty.isLeaderOnline()) {
                    if (playerParty.getLeaderOfflineTime() >= maxOfflineTime) {
                        shouldDisband = true;
                    }
                }

                if (!shouldDisband && party.getMemberCount() == 1) {
                    UUID member = party.getMembers().iterator().next();
                    if (playerParty.getLeaderOfflineTime() >= maxOfflineTime) {
                        shouldDisband = true;
                    }
                }

                if (shouldDisband) {
                    party.broadcast("&c队伍因队长离线时间过长或队伍人数不足已自动解散");
                    party.disband();
                }
            }
        }, 0L, 600L);
    }

    private void registerCommands() {
        getCommand("party").setExecutor(new PartyCommand(this));
        getCommand("party").setTabCompleter(new PartyCommand(this));
        getCommand("partyadmin").setExecutor(new PartyAdminCommand(this));
        getCommand("partyadmin").setTabCompleter(new PartyAdminCommand(this));
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PartyChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PartyPlayerListener(this), this);
    }

    public static PluxParty getInstance() {
        return instance;
    }

    public PartyManager getPartyManager() {
        return partyManager;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }
}