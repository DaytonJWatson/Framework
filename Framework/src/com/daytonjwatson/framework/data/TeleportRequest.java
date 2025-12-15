package com.daytonjwatson.framework.data;

import java.util.UUID;

public class TeleportRequest {
    private final UUID from;
    private final UUID to;
    private final long createdAt;
    private final boolean teleportHere;

    public TeleportRequest(UUID from, UUID to, long createdAt, boolean teleportHere) {
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
        this.teleportHere = teleportHere;
    }

    public UUID getFrom() {
        return from;
    }

    public UUID getTo() {
        return to;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public boolean isTeleportHere() {
        return teleportHere;
    }
}
