package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Table(name = "rooms")
@NoArgsConstructor
@Getter
public class Room extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
    }

    public void setCapacity(Integer capacity) {
        Objects.requireNonNull(capacity, "Capacity cannot be null");

        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
    }

    public void setDescription(String description) {
        this.description = Objects.requireNonNull(description, "Description cannot be null");
    }

    public Room(String name, Integer capacity) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        setCapacity(capacity);
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + getId() +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", active=" + active +
                "} " + super.toString();
    }
}