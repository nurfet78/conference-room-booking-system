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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from Booking b where b.id = :id")
    Optional<Booking> findByIdWithLock(@Param("id")Long id);

    @Query("""
    select exists (
        select 1
        from Booking  b
        where b.room.id = :roomId
        and b.status in (org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                         org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
        and b.startTime < :endTime
        and b.endTime > :startTime
        and (:excludeId is null or b.id <> :excludeId)
    )
""")
    boolean existsOverlappingBooking(@Param("roomId")Long roomId,
                                     @Param("startTime")Instant startTime,
                                     @Param("endTime")Instant endTime,
                                     @Param("excludeId")Long excludeId);

    default boolean existsOverlappingBooking(Long roomId,
                                             Instant startTime,
                                             Instant endTime) {
        return existsOverlappingBooking(roomId, startTime, endTime, null);
    }

    @Query("""
    select b
    from Booking b
    where b.room.id = :roomId
<<<<<<< HEAD
    and b.status in(org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                    org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
=======
    and b.status in (org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                     org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    and b.startTime < :endTime
    and b.endTime > :startTime
    order by b.startTime
""")
    List<Booking> findOverlappingBooking(@Param("roomId")Long roomId,
                                         @Param("startTime")Instant startTime,
                                         @Param("endTime")Instant endTime);

    @Query("""
    select b
    from Booking b
    where b.room.id = :id
    and b.startTime < :endTime
    and b.endTime > :startTime
    order by b.startTime
    
""")
    List<Booking> findBookingByRoomAndTimeRange(@Param("roomId")Long roomId,
                                                @Param("startTime")Instant startTime,
                                                @Param("endTime")Instant endTime);

    @Query("""
    select b
    from Booking b
    where b.room.id = :roomId
    and b.status in (org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                     org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
    and b.endTime > :now
    order by b.startTime
""")
    List<Booking> findActiveBookingByRoom(@Param("roomId")Long roomId,
                                          @Param("now")Instant now);

    List<Booking> findBookingsByOrganizerEmail(String email);

    List<Booking> findBookingsByStatus(BookingStatus status);

    @Modifying(clearAutomatically = true)
    @Query("""
    update Booking b
    set b.status = org.nurfet.bookingsystem.entity.BookingStatus.EXPIRED,
        b.updatedAt = :now
    where b.status in (org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                       org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
    and b.endTime < :now
""")
    int markExpiredBookings(@Param("now")Instant now);

    @Query("""
    select count(b)
    from Booking b
    where b.room.id = :roomId
    and b.status in (org.nurfet.bookingsystem.entity.BookingStatus.PENDING,
                     org.nurfet.bookingsystem.entity.BookingStatus.CONFIRMED)
    and b.endTime > :now
""")
    long countActiveBookingsByRoom(@Param("roomId")Long roomId,
                                   @Param("now")Instant now);
}