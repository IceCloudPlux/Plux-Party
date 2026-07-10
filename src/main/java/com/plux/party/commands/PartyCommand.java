package com.plux.party.commands;

import com.plux.party.PluxParty;
import com.plux.party.party.Party;
import com.plux.party.party.PartyManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class PartyCommand implements CommandExecutor, TabCompleter {

    private final PluxParty plugin;
    private final Map<UUID, Long> warpCooldown;

    public PartyCommand(PluxParty plugin) {
        this.plugin = plugin;
        this.warpCooldown = new ConcurrentHashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(color("&c只有玩家可以使用此命令"));
            return true;
        }

        Player player = (Player) sender;
        PartyManager manager = plugin.getPartyManager();

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "help":
                sendHelp(player);
                break;
            case "create":
                handleCreate(player, args);
                break;
            case "invite":
                handleInvite(player, args);
                break;
            case "accept":
                handleAccept(player, args);
                break;
            case "deny":
                handleDeny(player, args);
                break;
            case "join":
                handleJoin(player, args);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "kick":
                handleKick(player, args);
                break;
            case "transfer":
                handleTransfer(player, args);
                break;
            case "disband":
                handleDisband(player);
                break;
            case "info":
                handleInfo(player);
                break;
            case "list":
                handleList(player);
                break;
            case "chat":
                handleChat(player, args);
                break;
            case "warp":
                handleWarp(player);
                break;
            default:
                player.sendMessage(getMessage("party.not-in-party"));
                break;
        }

        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(color("&6=== PluxParty 帮助 ==="));
        player.sendMessage(color("&7/party create &f- 创建队伍"));
        player.sendMessage(color("&7/party invite <玩家> &f- 邀请玩家"));
        player.sendMessage(color("&7/party accept <玩家> &f- 接受邀请"));
        player.sendMessage(color("&7/party deny <玩家> &f- 拒绝邀请"));
        player.sendMessage(color("&7/party join [队伍ID] &f- 加入队伍"));
        player.sendMessage(color("&7/party leave &f- 离开队伍"));
        player.sendMessage(color("&7/party kick <玩家> &f- 踢出玩家"));
        player.sendMessage(color("&7/party transfer <玩家> &f- 转让队长"));
        player.sendMessage(color("&7/party disband &f- 解散队伍"));
        player.sendMessage(color("&7/party info &f- 队伍信息"));
        player.sendMessage(color("&7/party list &f- 队伍列表"));
        player.sendMessage(color("&7/party chat <消息> &f- 队伍聊天"));
        player.sendMessage(color("&7/party warp &f- 召集队员"));
        player.sendMessage(color("&7/party help &f- 显示帮助"));
    }

    private void handleCreate(Player player, String[] args) {
        if (!player.hasPermission("party.create")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (plugin.getPartyManager().isInParty(player.getUniqueId())) {
            player.sendMessage(getMessage("party.already-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().createParty(player.getUniqueId(), null);

        if (party != null) {
            player.sendMessage(getMessage("party.created").replace("%name%", party.getId()));
        }
    }

    private void handleInvite(Player player, String[] args) {
        if (!player.hasPermission("party.invite")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(getMessage("party.not-leader"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party invite <玩家>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(getMessage("party.player-not-online").replace("%player%", args[1]));
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(getMessage("party.cannot-invite-yourself"));
            return;
        }

        if (plugin.getPartyManager().isInParty(target.getUniqueId())) {
            player.sendMessage(getMessage("party.player-already-in-party").replace("%player%", target.getName()));
            return;
        }

        if (party.isFull()) {
            player.sendMessage(getMessage("party.party-full"));
            return;
        }

        plugin.getPartyManager().sendInvite(player.getUniqueId(), target.getUniqueId());
        player.sendMessage(getMessage("party.invite-sent").replace("%player%", target.getName()));
        target.sendMessage(getMessage("party.invite-received").replace("%player%", player.getName()));
    }

    private void handleAccept(Player player, String[] args) {
        if (!player.hasPermission("party.join")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party accept <玩家>"));
            return;
        }

        Player inviter = Bukkit.getPlayer(args[1]);
        if (inviter == null) {
            player.sendMessage(getMessage("party.player-not-online").replace("%player%", args[1]));
            return;
        }

        boolean success = plugin.getPartyManager().acceptInvite(player.getUniqueId(), inviter.getUniqueId());
        if (success) {
            Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
            player.sendMessage(getMessage("party.joined").replace("%name%", party.getName()));
            party.broadcastWithout(player.getUniqueId(), getMessage("party.member-joined").replace("%player%", player.getName()));
        } else {
            player.sendMessage(getMessage("party.invite-expired"));
        }
    }

    private void handleDeny(Player player, String[] args) {
        if (!player.hasPermission("party.join")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party deny <玩家>"));
            return;
        }

        Player inviter = Bukkit.getPlayer(args[1]);
        if (inviter == null) {
            player.sendMessage(getMessage("party.player-not-online").replace("%player%", args[1]));
            return;
        }

        boolean success = plugin.getPartyManager().denyInvite(player.getUniqueId(), inviter.getUniqueId());
        if (success && inviter.isOnline()) {
            inviter.sendMessage(getMessage("party.invite-denied").replace("%player%", player.getName()));
        }
    }

    private void handleJoin(Player player, String[] args) {
        if (!player.hasPermission("party.join")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (plugin.getPartyManager().isInParty(player.getUniqueId())) {
            player.sendMessage(getMessage("party.already-in-party"));
            return;
        }

        if (args.length > 1) {
            Party party = plugin.getPartyManager().getParty(args[1]);
            if (party == null) {
                player.sendMessage(getMessage("party.no-parties-available"));
                return;
            }
            if (party.isFull()) {
                player.sendMessage(getMessage("party.party-full"));
                return;
            }
            if (party.addMember(player.getUniqueId())) {
                plugin.getPartyManager().quickMatch(player.getUniqueId());
                player.sendMessage(getMessage("party.joined").replace("%name%", party.getName()));
                party.broadcastWithout(player.getUniqueId(), getMessage("party.member-joined").replace("%player%", player.getName()));
            }
        } else {
            Party party = plugin.getPartyManager().quickMatch(player.getUniqueId());
            if (party != null) {
                player.sendMessage(getMessage("party.joined").replace("%name%", party.getName()));
            }
        }
    }

    private void handleLeave(Player player) {
        if (!player.hasPermission("party.leave")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        party.broadcastWithout(player.getUniqueId(), getMessage("party.member-left").replace("%player%", player.getName()));
        plugin.getPartyManager().removePlayerFromParty(player.getUniqueId());
        player.sendMessage(getMessage("party.left"));
    }

    private void handleKick(Player player, String[] args) {
        if (!player.hasPermission("party.kick")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(getMessage("party.not-leader"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party kick <玩家>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(getMessage("party.player-not-online").replace("%player%", args[1]));
            return;
        }

        if (!party.isMember(target.getUniqueId())) {
            player.sendMessage(getMessage("party.player-not-in-party").replace("%player%", target.getName()));
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(color("&c你不能踢出自己"));
            return;
        }

        party.kickMember(target.getUniqueId());
        party.broadcast(getMessage("party.member-kicked").replace("%player%", target.getName()));
        plugin.getPartyManager().removePlayerFromParty(target.getUniqueId());
    }

    private void handleTransfer(Player player, String[] args) {
        if (!player.hasPermission("party.transfer")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(getMessage("party.not-leader"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party transfer <玩家>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            player.sendMessage(getMessage("party.player-not-online").replace("%player%", args[1]));
            return;
        }

        if (!party.isMember(target.getUniqueId())) {
            player.sendMessage(getMessage("party.player-not-in-party").replace("%player%", target.getName()));
            return;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(color("&c你已经是队长了"));
            return;
        }

        party.transferLeadership(target.getUniqueId());
        party.broadcast(getMessage("party.leadership-transferred").replace("%player%", target.getName()));
    }

    private void handleDisband(Player player) {
        if (!player.hasPermission("party.disband")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(getMessage("party.not-leader"));
            return;
        }

        party.broadcast(getMessage("party.disbanded"));
        party.disband();
    }

    private void handleInfo(Player player) {
        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        player.sendMessage(color("&6队伍信息:"));
        player.sendMessage(color("&7队伍ID: &6" + party.getId()));
        
        Player leaderPlayer = Bukkit.getPlayer(party.getLeader());
        String leaderColor = (leaderPlayer != null && leaderPlayer.isOnline()) ? "&a" : "&c";
        player.sendMessage(color("&7队长: " + leaderColor + Bukkit.getOfflinePlayer(party.getLeader()).getName()));
        
        player.sendMessage(color("&7成员: &6" + party.getMemberCount()));
        player.sendMessage(color("&7在线: &6" + party.getOnlineMemberCount()));
        player.sendMessage(color("&7成员列表:"));
        for (UUID memberUuid : party.getMembers()) {
            Player member = Bukkit.getPlayer(memberUuid);
            String name = Bukkit.getOfflinePlayer(memberUuid).getName();
            String color = (member != null && member.isOnline()) ? "&a" : "&c";
            String leader = party.isLeader(memberUuid) ? " &7(队长)" : "";
            player.sendMessage(color("&f- " + color + name + leader));
        }
    }

    private void handleList(Player player) {
        Collection<Party> parties = plugin.getPartyManager().getOpenParties();
        if (parties.isEmpty()) {
            player.sendMessage(getMessage("party.no-parties-available"));
            return;
        }

        player.sendMessage(color("&6可加入队伍:"));
        for (Party party : parties) {
            player.sendMessage(color("&7- &6" + party.getName() + " &7(" + party.getMemberCount() + "/" + plugin.getConfigManager().getInt("party.max-members", 8) + ")"));
        }
    }

    private void handleChat(Player player, String[] args) {
        if (!player.hasPermission("party.chat")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (args.length < 2) {
            player.sendMessage(color("&c用法: /party chat <消息>"));
            return;
        }

        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
        String format = getMessage("party.chat-format");
        party.broadcast(format.replace("%player%", player.getName()).replace("%message%", message));
    }

    private void handleWarp(Player player) {
        if (!player.hasPermission("party.warp")) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        Party party = plugin.getPartyManager().getPartyByPlayer(player.getUniqueId());
        if (party == null) {
            player.sendMessage(getMessage("party.not-in-party"));
            return;
        }

        if (!party.isLeader(player.getUniqueId())) {
            player.sendMessage(getMessage("party.not-leader"));
            return;
        }

        long lastUse = warpCooldown.getOrDefault(player.getUniqueId(), 0L);
        int cooldown = plugin.getConfigManager().getInt("party.warp-cooldown", 5);
        if (System.currentTimeMillis() - lastUse < cooldown * 1000L) {
            player.sendMessage(getMessage("party.warp-cooldown").replace("%seconds%", String.valueOf(cooldown)));
            return;
        }

        warpCooldown.put(player.getUniqueId(), System.currentTimeMillis());

        for (Player member : party.getOnlineMembers()) {
            if (!member.getUniqueId().equals(player.getUniqueId())) {
                member.teleport(player.getLocation());
            }
        }

        party.broadcast(getMessage("party.warping"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            String[] subcommands = {"create", "invite", "accept", "deny", "join", "leave", "kick", "transfer", "disband", "info", "list", "chat", "warp", "help"};
            for (String sub : subcommands) {
                if (sub.startsWith(args[0].toLowerCase())) {
                    completions.add(sub);
                }
            }
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("invite") || sub.equals("kick") || sub.equals("transfer") || sub.equals("accept") || sub.equals("deny")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                        completions.add(player.getName());
                    }
                }
            }
        }

        return completions;
    }

    private String getMessage(String path) {
        return plugin.getConfigManager().getMessage(path);
    }

    private String color(String text) {
        return text != null ? text.replace("&", "§") : "";
    }
}