package org.nurfet.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "bookings")
@Getter
public class Booking extends BaseEntity {

    private static final Duration MIN_DURATION = Duration.ofMinutes(15);
    private static final Duration MAX_DURATION = Duration.ofHours(8);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
    private BookingStatus status = BookingStatus.PENDING;

    protected Booking() {

    }

    public Booking(Room room, String title, String organizerEmail,
                   Instant startTime, Instant endTime) {

        this.room = room;
        this.title = title;
        this.organizerEmail = Objects.requireNonNull(organizerEmail, "Organizer email cannot be null");
        setTimeInterval(startTime, endTime);
        this.status = BookingStatus.PENDING;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public boolean overlaps(Instant otherStart, Instant otherEnd) {
        return this.startTime.isBefore(otherEnd) && this.endTime.isAfter(otherStart);
    }

    public boolean isActive() {
        return status.isActive();
    }

    public boolean isExpired() {
        return endTime.isBefore(Instant.now());
    }

    public void confirm() {
        if (!status.isConfirmable()) {
            throw new IllegalStateException("Cannot confirm booking with status " + status);
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

    public void markAsExpired() {
        if (status.isActive()) {
            this.status = BookingStatus.EXPIRED;
        }
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
            throw new IllegalArgumentException("Booking duration must be at least " + MIN_DURATION.toMinutes() + " minutes");
        }

        if (duration.compareTo(MAX_DURATION) > 0) {
            throw new IllegalArgumentException("Booking duration cannot exceed " + MAX_DURATION.toHours() + " hours");
        }

        this.startTime = startTime;
        this.endTime = endTime;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", room=" + room +
                ", title='" + title + '\'' +
                ", organizerEmail='" + organizerEmail + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", status=" + status +
                '}';
    }
}
