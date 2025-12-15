package com.daytonjwatson.framework.data;

import java.util.UUID;

public class TeleportRequest {
    private final UUID from;
    private final UUID to;
    private final long createdAt;

    public TeleportRequest(UUID from, UUID to, long createdAt) {
        this.from = from;
        this.to = to;
        this.createdAt = createdAt;
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
}
