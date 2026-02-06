package org.nurfet.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.nurfet.bookingsystem.dto.error.ErrorResponse;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Комнаты")  // Группировка в Swagger UI
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @Operation(
            summary = "Создать переговорную комнату",
            description = """
                    Создаёт новую переговорную комнату с указанным названием, вместимостью и описанием.
                    Комната создаётся в активном состоянии (active = true).
                    """,
            operationId = "createRoom"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Комната успешно создана",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RoomResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка валидации входных данных",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            // ExampleObject — конкретный пример тела ответа для этого статуса.
                            // Помогает frontend-разработчикам понять формат ошибки.
                            examples = @ExampleObject(
                                    name = "validationError",
                                    summary = "Ошибка валидации",
                                    value = """
                                            {
                                              "timeStamp": "2025-06-15T10:30:00Z",
                                              "status": 400,
                                              "error": "Validation Failed",
                                              "errorCode": "VALIDATION_ERROR",
                                              "message": "Validation failed for one or more fields",
                                              "path": "/api/v1/rooms",
                                              "fieldErrors": [
                                                {
                                                  "field": "name",
                                                  "message": "Room name is required",
                                                  "rejectedValue": null
                                                }
                                              ]
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Внутренняя ошибка сервера",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class)
                    )
            )
    })
    @PostMapping
    public ResponseEntity<RoomResponse> createRoom(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Данные для создания комнаты",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CreateRoomRequest.class),
                            examples = @ExampleObject(
                                    name = "createRoomExample",
                                    summary = "Пример создания комнаты",
                                    value = """
                                            {
                                              "name": "Конференц-зал «Эверест»",
                                              "capacity": 12,
                                              "description": "3 этаж, проектор, маркерная доска, видеоконференцсвязь"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody CreateRoomRequest request) {

        RoomResponse response = roomService.createRoom(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "Получить комнату по ID",
            description = "Возвращает полную информацию о переговорной комнате по её уникальному идентификатору",
            operationId = "getRoomById"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Комната найдена",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Комната не найдена",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "timeStamp": "2025-06-15T10:30:00Z",
                                      "status": 404,
                                      "error": "Not Found",
                                      "errorCode": "ROOM_NOT_FOUND",
                                      "message": "Room with id 999 not found",
                                      "path": "/api/v1/rooms/999"
                                    }
                                    """)
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoom(
            @Parameter(
                    description = "Уникальный идентификатор комнаты",
                    required = true,
                    example = "1"
            )
            @PathVariable Long id) {
        RoomResponse response = roomService.getRoom(id);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Получить список комнат",
            description = "Возвращает все комнаты или только активные (при activeOnly=true)",
            operationId = "getAllRooms"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список комнат",
                    // ArraySchema — для описания ответа-массива.
                    // Без него Swagger покажет просто "array" без типа элементов.
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = RoomResponse.class))
                    )
            )
    })
    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms(
            @Parameter(
                    description = "Если true — вернуть только активные комнаты",
                    example = "false"
            )
            @RequestParam(defaultValue = "false") boolean activeOnly) {

        List<RoomResponse> rooms = activeOnly
                ? roomService.getActiveRooms()
                : roomService.getAllRooms();

        return ResponseEntity.ok(rooms);
    }

    @Operation(
            summary = "Обновить комнату (частично)",
            description = "Обновляет только переданные поля комнаты. Поля со значением null игнорируются",
            operationId = "updateRoom"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комната обновлена",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Комната не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<RoomResponse> updateRoom(
            @Parameter(description = "ID комнаты для обновления", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoomRequest request) {

        RoomResponse response = roomService.updateRoom(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Деактивировать комнату",
            description = """
                    Помечает комнату как неактивную (active = false).
                    Деактивированная комната не может быть забронирована.
                    Существующие бронирования **не** отменяются автоматически.
                    """,
            operationId = "deactivateRoom"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Комната деактивирована",
                    content = @Content(schema = @Schema(implementation = RoomResponse.class))),
            @ApiResponse(responseCode = "404", description = "Комната не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{id}/deactivate")
    public ResponseEntity<RoomResponse> deactivateRoom(
            @Parameter(description = "ID комнаты для деактивации", required = true, example = "1")
            @PathVariable Long id) {
        RoomResponse response = roomService.deactivateRoom(id);
        return ResponseEntity.ok(response);
    }
}
