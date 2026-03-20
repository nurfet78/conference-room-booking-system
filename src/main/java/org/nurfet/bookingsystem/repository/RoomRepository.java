package org.nurfet.bookingsystem.repository;

import jakarta.persistence.LockModeType;
import org.nurfet.bookingsystem.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long>, JpaSpecificationExecutor<Room> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Room r where r.id = :id")
    Optional<Room> findByIdWithLock(@Param("id")Long id);


    @Query("""
    select r
    from Room r
    where r.capacity >= :requiredCapacity
    order by r.capacity
""")
    List<Room> findRoomWithRequiredCapacity(@Param("requiredCapacity")Integer requiredCapacity);
}
