package org.nurfet.bookingsystem.repository;

import jakarta.persistence.LockModeType;
import org.nurfet.bookingsystem.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Room r WHERE r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id") Long id);

    List<Room> findByActiveTrue();

    Optional<Room> findByName(String name);

    boolean existsByName(String name);

    @Query("SELECT r FROM Room r WHERE r.active = true AND r.capacity >= :minCapacity ORDER BY r.capacity")
    List<Room> findActiveRoomsWithMinCapacity(@Param("minCapacity") Integer minCapacity);
}
