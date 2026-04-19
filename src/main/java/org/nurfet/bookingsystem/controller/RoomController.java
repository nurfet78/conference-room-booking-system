package org.nurfet.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.dto.spec.RoomFilter;
import org.nurfet.bookingsystem.service.RoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

<<<<<<< HEAD
import java.awt.image.DirectColorModel;

=======
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Комнаты")
public class RoomController {

    private final RoomService roomService;

<<<<<<< HEAD
    @Operation(summary = "Создание комнаты")
=======
    @Operation(summary = "Создать переговорную комнату")
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    @ApiResponse(responseCode = "201", description = "Комната создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

<<<<<<< HEAD
    @Operation(summary = "Обновление комнаты (частично)")
=======
    @Operation(summary = "Обновить комнату (частично)")
>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PatchMapping("/{id}")
    public RoomResponse updateRoom(@PathVariable Long id,
                                   @Valid @RequestBody UpdateRoomRequest request) {
        return roomService.updateRoom(id, request);
    }

    @Operation(summary = "Получить комнату по ID")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping("/{id}")
    public RoomResponse getRoom(@PathVariable Long id) {
        return roomService.getRoom(id);
    }

<<<<<<< HEAD
=======
    @Operation(summary = "Поиск комнаты с фильтрацией")
    @GetMapping("/search")
    public Page<RoomResponse> searchRoom(
            @Valid @ModelAttribute RoomFilter filter,
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return roomService.searchRoom(filter, pageable);
    }

>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    @Operation(summary = "Получить список комнат")
    @GetMapping
    public Page<RoomResponse> getRooms(
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
<<<<<<< HEAD

        return roomService.getRooms(pageable);
    }

    @Operation(summary = "Поиск комнат с фильтрацией")
    @GetMapping("/search")
    public Page<RoomResponse> searchRoom(
            @Valid @ModelAttribute RoomFilter filter,
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {

        return roomService.searchRoom(filter, pageable);
    }

=======
        return roomService.getRooms(pageable);
    }

>>>>>>> ec97005a88fa2d730bbad206fc8e9ec92c3beca5
    @Operation(summary = "Деактивировать комнату")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PostMapping("/{id}/deactivate")
    public RoomResponse deactivateRoom(@PathVariable Long id) {
        return roomService.deactivateRoom(id);
    }
}