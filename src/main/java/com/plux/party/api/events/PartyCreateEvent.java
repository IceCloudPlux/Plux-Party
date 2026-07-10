package com.plux.party.api.events;

import com.plux.party.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyCreateEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Party party;
    private final Player leader;
    private boolean cancelled;

    public PartyCreateEvent(Party party, Player leader) {
        this.party = party;
        this.leader = leader;
        this.cancelled = false;
    }

    public Party getParty() {
        return party;
    }

    public Player getLeader() {
        return leader;
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
