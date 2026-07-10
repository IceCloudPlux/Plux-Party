package com.plux.party.commands;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PartyAdminCommand implements CommandExecutor, TabCompleter {

    private final PluxParty plugin;

    public PartyAdminCommand(PluxParty plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("party.admin")) {
            sender.sendMessage(color("&c你没有权限使用此命令"));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(color("&6PluxParty 管理员指令:"));
            sender.sendMessage(color("&7/partyadmin reload - 重载配置"));
            sender.sendMessage(color("&7/partyadmin force <队伍ID> - 强制解散队伍"));
            sender.sendMessage(color("&7/partyadmin list - 查看所有队伍"));
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                handleReload(sender);
                break;
            case "force":
                handleForce(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            default:
                sender.sendMessage(color("&c未知指令"));
                break;
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        plugin.getConfigManager().loadAll();
        sender.sendMessage(plugin.getConfigManager().getMessage("admin.reloaded"));
    }

    private void handleForce(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(color("&c用法: /partyadmin force <队伍ID>"));
            return;
        }

        String partyId = args[1];
        Party targetParty = plugin.getPartyManager().getParty(partyId);

        if (targetParty == null) {
            sender.sendMessage(color("&c队伍不存在: " + partyId));
            return;
        }

        targetParty.disband();
        sender.sendMessage(color("&a队伍 " + partyId + " 已强制解散"));
    }

    private void handleList(CommandSender sender) {
        Collection<Party> parties = plugin.getPartyManager().getParties();

        if (parties.isEmpty()) {
            sender.sendMessage(color("&7没有队伍"));
            return;
        }

        sender.sendMessage(color("&6所有队伍:"));
        sender.sendMessage(color("&7-------------------"));

        for (Party party : parties) {
            String leaderName = Bukkit.getOfflinePlayer(party.getLeader()).getName();
            sender.sendMessage(color("&6队伍ID: " + party.getId()));
            sender.sendMessage(color("&7  队长: " + leaderName));
            sender.sendMessage(color("&7  成员: " + party.getMemberCount() + "/" + plugin.getConfigManager().getInt("party.max-members", 8)));
            sender.sendMessage(color("&7  在线: " + party.getOnlineMemberCount()));
            sender.sendMessage(color("&7-------------------"));
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] subcommands = {"reload", "force", "list"};
            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("force")) {
            for (Party party : plugin.getPartyManager().getParties()) {
                if (party.getId().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(party.getId());
                }
            }
        }

        return completions;
    }

    private String color(String text) {
        return text != null ? text.replace("&", "§") : "";
    }
}