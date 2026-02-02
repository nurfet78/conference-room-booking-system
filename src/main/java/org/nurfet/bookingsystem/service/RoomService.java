package org.nurfet.bookingsystem.service;

import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.exception.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.mapper.room.RoomMapper;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RoomService {

    private static final Logger log = LoggerFactory.getLogger(RoomService.class);

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    private Room findRoomById(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Room", id));
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest request) {
        log.info("Creating room: {}", request.name());

        Room room = roomMapper.toEntity(request);
        Room saved = roomRepository.save(room);

        log.info("Room created with id: {}", saved.getId());
        return roomMapper.toResponse(saved);
    }

    @Transactional
    public RoomResponse getRoom(Long id) {
        Room room = findRoomById(id);
        return roomMapper.toResponse(room);
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getAllRooms() {
        return roomMapper.toResponseList(roomRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<RoomResponse> getActiveRooms() {
        return roomMapper.toResponseList(roomRepository.findByActiveTrue());
    }

    @Transactional
    public RoomResponse updateRoom(Long id, UpdateRoomRequest request) {
        log.info("Updating room: {}", id);

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

        Room saved = roomRepository.save(room);
        log.info("Room updated: {}", id);

        return roomMapper.toResponse(saved);
    }

    @Transactional
    public RoomResponse deactivateRoom(Long id) {
        log.info("Deactivating room: {}", id);

        Room room = findRoomById(id);
        room.deactivate();

        Room saving = roomRepository.save(room);
        log.info("Room deactivated: {}", id);

        return roomMapper.toResponse(saving);
    }
}
