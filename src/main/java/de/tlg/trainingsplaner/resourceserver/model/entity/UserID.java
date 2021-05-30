package de.tlg.trainingsplaner.resourceserver.model.entity;

import java.util.UUID;

public class UserID {
    private UUID userId;

    public UserID() {
        this.userId = UUID.randomUUID();
    }

    public UUID getUserId() {
        return userId;
    }

    public UUID update() {
        this.userId = UUID.randomUUID();
        return this.userId;
    }
}
