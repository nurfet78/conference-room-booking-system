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

@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
@Tag(name = "Комнаты")
public class RoomController {

    private final RoomService roomService;

    @Operation(summary = "Создать переговорную комнату")
    @ApiResponse(responseCode = "201", description = "Комната создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return roomService.createRoom(request);
    }

    @Operation(summary = "Обновить комнату (частично)")
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

    @Operation(summary = "Поиск комнаты с фильтрацией")
    @GetMapping("/search")
    public Page<RoomResponse> searchRoom(
            @Valid @ModelAttribute RoomFilter filter,
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return roomService.searchRoom(filter, pageable);
    }

    @Operation(summary = "Получить список комнат")
    @GetMapping
    public Page<RoomResponse> getRooms(
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        return roomService.getRooms(pageable);
    }

    @Operation(summary = "Деактивировать комнату")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PostMapping("/{id}/deactivate")
    public RoomResponse deactivateRoom(@PathVariable Long id) {
        return roomService.deactivateRoom(id);
    }
}