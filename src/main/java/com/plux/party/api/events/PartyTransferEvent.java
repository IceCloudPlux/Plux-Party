package com.plux.party.api.events;

import com.plux.party.party.Party;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyTransferEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private final Party party;
    private final UUID oldLeader;
    private final UUID newLeader;
    private boolean cancelled;

    public PartyTransferEvent(Party party, UUID oldLeader, UUID newLeader) {
        this.party = party;
        this.oldLeader = oldLeader;
        this.newLeader = newLeader;
        this.cancelled = false;
    }

    public Party getParty() {
        return party;
    }

    public UUID getOldLeader() {
        return oldLeader;
    }

    public UUID getNewLeader() {
        return newLeader;
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
