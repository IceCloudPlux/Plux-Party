package com.plux.party.api.events;

import com.plux.party.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyMemberLeaveEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Party party;
    private final Player player;
    private final boolean wasLeader;
    private boolean cancelled;

    public PartyMemberLeaveEvent(Party party, Player player, boolean wasLeader) {
        this.party = party;
        this.player = player;
        this.wasLeader = wasLeader;
        this.cancelled = false;
    }

    public Party getParty() {
        return party;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean wasLeader() {
        return wasLeader;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
