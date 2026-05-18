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
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Комнаты")
@RequiredArgsConstructor
@Validated
public class RoomController {

    private final RoomService service;

    @Operation(summary = "Создание переговорной комнаты")
    @ApiResponse(responseCode = "201", description = "Комната создана")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest request) {
        return service.createRoom(request);
    }

    @Operation(summary = "Частичное обновление переговорной комнаты")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    @PatchMapping("/{id}")
    public RoomResponse updateRoom(@PathVariable Long id,
                                   @Valid @RequestBody UpdateRoomRequest request) {

        return service.updateRoom(id, request);
    }

    @Operation(summary = "Получить комнату по ID")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @GetMapping("/{id}")
    public EntityModel<RoomResponse> getRoom(@PathVariable Long id) {
        RoomResponse room = service.getRoom(id);
        return EntityModel.of(room,
                linkTo(methodOn(RoomController.class).getRoom(id)).withSelfRel());
    }

    @Operation(summary = "Получить список комнат")
    @GetMapping
    public Page<RoomResponse> getRooms(Pageable pageable) {
        return service.getRooms(pageable);
    }

    @Operation(summary = "Поиск комнаты с параметрами")
    @GetMapping("/search")
    public PagedModel<EntityModel<RoomResponse>> searchRooms(
            @Valid @ModelAttribute RoomFilter filter,
            @PageableDefault(size = 3, sort = "name", direction = Sort.Direction.ASC) Pageable pageable,
            PagedResourcesAssembler<RoomResponse> assembler) {

        Page<RoomResponse> page = service.searchRoom(filter, pageable);

        return assembler.toModel(page, room ->
                EntityModel.of(room,
                        linkTo(methodOn(RoomController.class)
                                .getRoom(room.id())).withSelfRel()));
    }

    @Operation(summary = "Деактивировать комнату")
    @ApiResponse(responseCode = "404", description = "Комната не найдена")
    @PostMapping("{id}/deactivate")
    public RoomResponse deactivate(@PathVariable Long id) {
        return service.deactivate(id);
    }
}