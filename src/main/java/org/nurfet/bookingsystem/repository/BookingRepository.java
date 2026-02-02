package org.nurfet.bookingsystem.repository;

import jakarta.persistence.LockModeType;
import org.nurfet.bookingsystem.entity.Booking;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
        select (count(b) > 0)
        from Booking b
        where b.room.id = :roomId
        AND (b.status = org.nurfet.bookingsystem.entity.BookingStatus.PENDING OR
             b.status = org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
        and b.startTime < :endTime
        and b.endTime > :startTime
        AND (:excludeBookingId IS NULL OR b.id <> :excludeBookingId)
    """)
    boolean existsOverlappingBooking(
            @Param("roomId") Long roomId,
            @Param("startTime")Instant startTime,
            @Param("endTime") Instant endTime,
            @Param("excludeBookingId") Long excludeBookingId);

    default boolean existsOverlappingBooking(
            Long roomId,
            Instant startTime,
            Instant endTime) {
        return existsOverlappingBooking(roomId, startTime, endTime, null);
    }

    @Query("""
           select b from Booking b
           where b.room.id = :roomId
           AND (b.status = org.nurfet.bookingsystem.entity.BookingStatus.PENDING OR
                       b.status = org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
           and b.startTime < :endTime
           and b.endTime > :startTime
           order by b.startTime
           """)
    List<Booking> findOverlappingBookings(
            @Param("roomId") Long roomId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdWithLock(@Param("id") Long id);

    @Query("""
    select b from Booking b where b.room.id = :roomId
        and b.startTime < :endTime
        and b.endTime > :startTime
    order by b.startTime
    """)
    List<Booking> findByRoomAndTimeRange(
            @Param("roomId") Long roomId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime
    );

    @Query("""
           select b from Booking b
           where b.room.id = :roomId
                      AND (b.status = org.nurfet.bookingsystem.entity.BookingStatus.PENDING OR
                                 b.status = org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
                      and b.endTime > :now
           order by b.startTime
           """)
    List<Booking> findActiveBookingsByRoom(
            @Param("roomId") Long roomId,
            @Param("now") Instant now
    );

    @Query("""
     select b from Booking b
        where b.organizerEmail = :organizerEmail
        order by b.startTime desc
     """)
    List<Booking> findByOrganizerEmail(@Param("organizerEmail")String organizerEmail);

    @Query("""
          select b from Booking b
          where b.status = :status
          order by b.startTime
          """)
    List<Booking> findByStatus(@Param("status") BookingStatus status);

    @Modifying
    @Query("""
    UPDATE Booking b
    SET b.status = org.nurfet.bookingsystem.entity.BookingStatus.EXPIRED,
        b.updatedAt = CURRENT_TIMESTAMP
    WHERE (b.status = org.nurfet.bookingsystem.entity.BookingStatus.PENDING
           OR b.status = org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
      AND b.endTime < :now
    """)
    int markExpiredBookings(@Param("now")Instant now);

    @Query("""
    SELECT COUNT(b) FROM Booking b
    WHERE b.room.id = :roomId
      AND (b.status = org.nurfet.bookingsystem.entity.BookingStatus.PENDING
           OR b.status = org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
      AND b.endTime > :now
    """)
    long countActiveBookingsRoom(
            @Param("roomId")Long roomId,
            @Param("now")Instant now
    );
}
