package com.plux.party.api.events;

import com.plux.party.party.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyDisbandEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Party party;
    private boolean cancelled;

    public PartyDisbandEvent(Party party) {
        this.party = party;
        this.cancelled = false;
    }

    public Party getParty() {
        return party;
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
