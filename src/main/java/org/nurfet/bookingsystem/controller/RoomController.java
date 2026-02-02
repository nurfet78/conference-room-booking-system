package org.nurfet.bookingsystem.controller;

import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerMapping;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @Valid @RequestBody CreateRoomRequest request) {

        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(@PathVariable Long id) {
        RoomResponse response = roomService.getRoom(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<RoomResponse> rooms = activeOnly
                ? roomService.getActiveRooms()
                : roomService.getAllRooms();

        return ResponseEntity.ok(rooms);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {

        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<RoomResponse> deactivateRoom(@PathVariable Long id) {
        RoomResponse response = roomService.deactivateRoom(id);
        return ResponseEntity.ok(response);
    }
}
