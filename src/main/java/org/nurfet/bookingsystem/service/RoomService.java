package org.nurfet.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.nurfet.bookingsystem.dto.spec.RoomFilter;
import org.nurfet.bookingsystem.exception.InvalidBookingStateException;
import org.nurfet.bookingsystem.specification.RoomSpecification;
import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.exception.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.mapper.room.RoomMapper;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room", id));
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        log.info("Creating room: {}", request.name());

        Room room = roomMapper.toEntity(request);
        Room saved = roomRepository.save(room);
        log.info("Room with id: {} created", saved.getId());

        return roomMapper.toResponse(saved);
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        log.info("Updating room with id: {}", id);

        Room room = findRoomById(id);

        if (request.name() != null) {
            room.setName(request.name());
        }

        if (request.capacity() != null) {
            room.setCapacity(request.capacity());
        }

        if (request.description() != null) {
            room.setDescription(request.description());
        }

        if (request.active() != null) {
            if (request.active()) {
                room.activate();
            } else {
                room.deactivate();
            }
        }

        log.info("Room with id: {} updated", id);

        return roomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public RoomResponse getRoom(Long id) {
        Room room = findRoomById(id);
        return roomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> searchRoom(RoomFilter filter, Pageable pageable) {
        return roomRepository.findAll(RoomSpecification.fromFilter(filter), pageable)
                .map(roomMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<RoomResponse> getRooms(Pageable pageable) {
        return searchRoom(RoomFilter.empty(), pageable);
    }

    @Transactional
    public RoomResponse deactivateRoom(Long id) {
        log.info("Deactivating room with id: {}", id);

        Room room = findRoomById(id);
        room.deactivate();

        Room saved = roomRepository.save(room);
        log.info("Room with id: {} deactivated", saved.getId());

        return roomMapper.toResponse(saved);
    }
}