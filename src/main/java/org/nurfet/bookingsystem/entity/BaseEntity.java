package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;

@MappedSuperclass
@Getter
@Access(AccessType.FIELD)  // Явно — для документации намерений
public abstract class BaseEntity {

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void prePersist() {  // protected — если подкласс захочет расширить
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
