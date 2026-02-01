package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "rooms")
@Getter
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @Setter
    @Column(name = "description")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    protected Room() {

    }

    public Room(String name, Integer capacity) {
        this.name = Objects.requireNonNull(name, "Room name cannot be null");
        setCapacity(capacity);
    }

    public void setName(String name) {
        this.name = Objects.requireNonNull(name, "Room name cannot be null");
    }

    public void setCapacity(Integer capacity) {
        if (capacity == null || capacity <= 0) {
            throw new IllegalArgumentException("Capacity must by positive");
        }
        this.capacity = capacity;
    }

    public void deactivate() {
        this.active = false;
    }

    public void activate() {
        this.active = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Room room)) return false;
        return id != null && id.equals(room.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Room{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", capacity=" + capacity +
                ", description='" + description + '\'' +
                ", active=" + active +
                '}';
    }
}
