package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import lombok.Setter;
import org.springframework.data.convert.Jsr310Converters;
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5

import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@NoArgsConstructor
@Getter
public class Booking extends BaseEntity {

    private static final Duration MIN_DURATION = Duration.ofMinutes(15);
    private static final Duration MAX_DURATION = Duration.ofHours(8);

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "organizer_email", nullable = false)
    private String organizerEmail;

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time", nullable = false)
    private Instant endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private BookingStatus status;

    public void changeRoom(Room room) {
        this.room = Objects.requireNonNull(room, "Room cannot be null");
    }

    public void setTitle(String title) {
        this.title = Objects.requireNonNull(title, "Title cannot be null");
    }

    public void setTimeInterval(Instant startTime, Instant endTime) {
        Objects.requireNonNull(startTime, "Start time cannot be null");
        Objects.requireNonNull(endTime, "End time cannot be null");

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Duration duration = Duration.between(startTime, endTime);

        if (duration.compareTo(MIN_DURATION) < 0) {
            throw new IllegalArgumentException("Booking duration must be at least " +
                    MIN_DURATION.toMinutes() + " minutes");
        }

        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Booking duration cannot exceed " +
                    MAX_DURATION.toHours() + " hours");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

<<<<<<< HEAD
=======
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    public Booking(Room room, String title, String organizerEmail,
                   Instant startTime, Instant endTime) {
        this.room = Objects.requireNonNull(room, "Room cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
<<<<<<< HEAD
        this.organizerEmail = Objects.requireNonNull(organizerEmail,"Organizer email cannot be null");
=======
        this.organizerEmail = Objects.requireNonNull(organizerEmail, "Organizer email cannot be null");
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
        setTimeInterval(startTime, endTime);
        this.status = BookingStatus.PENDING;
    }

<<<<<<< HEAD
    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

=======
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    public boolean isActive() {
        return status.isActive();
    }

    public boolean isExpired() {
        return endTime.isBefore(Instant.now());
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return startTime.isBefore(otherEnd) && endTime.isAfter(otherStart);
    }

    public void confirm() {
        if (!status.isConfirmable()) {
<<<<<<< HEAD
            throw new IllegalStateException("Cannot booking confirm with status " + status);
=======
            throw new IllegalStateException("Cannot confirm booking with status " + status);
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
        }

        if (isExpired()) {
            throw new IllegalStateException("Cannot confirm expired booking");
        }

        this.status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (!status.isCancellable()) {
            throw new IllegalStateException("Cannot cancel booking with status " + status);
        }

        this.status = BookingStatus.CANCELLED;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Booking{" +
                "id=" + getId() +
=======
        return "Booking{" + "id=" + getId() +
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
                ", room=" + room +
                ", title='" + title + '\'' +
                ", organizerEmail='" + organizerEmail + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                "} " + super.toString();
    }
}