package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import lombok.Setter;
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5

import java.util.Objects;


@Entity
@Table(name = "rooms")
@NoArgsConstructor
@Getter
public class Room extends BaseEntity {

<<<<<<< HEAD
    @Column(name = "name", nullable = false, unique = true)
=======
    @Column(name = "name", nullable = false, unique = true, length = 100)
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    private String name;

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

<<<<<<< HEAD
    @Column(name = "description")
=======
    @Column(name = "description", nullable = false)
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
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
<<<<<<< HEAD
    }

    public boolean isActive() {
        return active;
=======
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
<<<<<<< HEAD
=======
    }

    public boolean isActive() {
        return active;
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
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