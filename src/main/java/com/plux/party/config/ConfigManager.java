package com.plux.party.config;

import com.plux.party.PluxParty;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.List;

public class ConfigManager {

    private final PluxParty plugin;
    private FileConfiguration config;
    private FileConfiguration messages;

    public ConfigManager(PluxParty plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        loadConfig();
        loadMessages();
    }

    private void loadConfig() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            plugin.saveResource("config.yml", false);
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public String getString(String path, String def) {
        return config.getString(path, def);
    }

    public int getInt(String path, int def) {
        return config.getInt(path, def);
    }

    public boolean getBoolean(String path, boolean def) {
        return config.getBoolean(path, def);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(path);
    }

    public String getMessage(String path) {
        String message = messages.getString(path);
        if (message == null) {
            return "&c消息未找到: " + path;
        }
        return message.replace("&", "§");
    }
}