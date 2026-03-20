package org.nurfet.bookingsystem;

import org.assertj.core.api.AssertionsForInterfaceTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.nurfet.bookingsystem.dto.request.CreateRoomRequest;
import org.nurfet.bookingsystem.dto.request.UpdateRoomRequest;
import org.nurfet.bookingsystem.dto.response.BookingResponse;
import org.nurfet.bookingsystem.dto.response.RoomResponse;
import org.nurfet.bookingsystem.entity.BookingStatus;
import org.nurfet.bookingsystem.entity.Room;
import org.nurfet.bookingsystem.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class RoomControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RoomRepository roomRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("POST /api/v1/rooms")
    public void shouldReturn201createRoom() {
        CreateRoomRequest roomRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        RoomResponse response = webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();

        Room room = roomRepository.findById(response.id()).orElseThrow();

        assertThat(room.getName()).isEqualTo(roomRequest.name());
        assertThat(room.getCapacity()).isEqualTo(roomRequest.capacity());
        assertThat(room.getDescription()).isEqualTo(roomRequest.description());
        assertThat(room.isActive()).isTrue();
    }

    @Test
    @DisplayName("POST /api/v1/rooms")
    public void shouldReturn400WhenCapacityIsInvalid() {
        CreateRoomRequest invalidJson = new CreateRoomRequest(
                "API Test Room",
                0,
                "Room for API tests"
        );

        webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(invalidJson)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("VALIDATION_ERROR");
    }

    @Test
    @DisplayName("POST /api/v1/rooms should return 409 when room name already exists")
    void shouldReturn409ForDuplicateRoomName() {

        CreateRoomRequest roomRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        // создаём комнату первый раз
        webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomRequest)
                .exchange()
                .expectStatus().isCreated();

        // пробуем создать снова с тем же именем
        webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomRequest)
                .exchange()
                .expectStatus().isEqualTo(409)
                .expectBody()
                .jsonPath("$.errorCode").isEqualTo("DUPLICATE_ENTRY")
                .jsonPath("$.detail").isEqualTo("Resource already exists");
    }

    @Test
    @DisplayName("PATCH /api/v1/rooms/{id} should update room")
    public void fullUpdatingRoom() {

        CreateRoomRequest roomRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        RoomResponse response = webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(roomRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();

        UpdateRoomRequest updateRequest = new UpdateRoomRequest(
                "New room",
                20,
                "Update room API tests",
                false
        );

        webTestClient.patch()
                .uri("/api/v1/rooms/{id}", response.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomResponse.class)
                .value(updateResponse -> {
                    assertThat(updateResponse.name()).isEqualTo(updateRequest.name());
                    assertThat(updateResponse.capacity()).isEqualTo(updateRequest.capacity());
                    assertThat(updateResponse.description()).isEqualTo(updateRequest.description());
                    assertThat(updateResponse.active()).isFalse();
                });

        Room updatedRoom = roomRepository.findById(response.id()).orElseThrow();

        assertThat(updatedRoom.getName()).isEqualTo(updateRequest.name());
        assertThat(updatedRoom.getCapacity()).isEqualTo(updateRequest.capacity());
        assertThat(updatedRoom.getDescription()).isEqualTo(updateRequest.description());
        assertThat(updatedRoom.isActive()).isFalse();
    }

    @Test
    @DisplayName("PATCH /api/v1/rooms/{id} should update only provided fields")
    void shouldPartiallyUpdateRoom() {

        CreateRoomRequest createRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        RoomResponse response = webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();

        // обновляем только description
        UpdateRoomRequest updateRequest = new UpdateRoomRequest(
                null,
                null,
                "Updated description",
                null
        );

        webTestClient.patch()
                .uri("/api/v1/rooms/{id}", response.id())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updateRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomResponse.class)
                .value(updateResponse -> {
                    assertThat(updateResponse.name()).isEqualTo("API Test Room");
                    assertThat(updateResponse.capacity()).isEqualTo(10);
                    assertThat(updateResponse.description()).isEqualTo("Updated description");
                    assertThat(updateResponse.active()).isTrue();
                });
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{id}/deactivate should deactivate room")
    void shouldDeactivateRoom() {

        CreateRoomRequest createRequest = new CreateRoomRequest(
                "API Test Room",
                10,
                "Room for API tests"
        );

        RoomResponse response = webTestClient.post()
                .uri("/api/v1/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(createRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(RoomResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(response).isNotNull();

        webTestClient.post()
                .uri("/api/v1/rooms/{id}/deactivate", response.id())
                .exchange()
                .expectStatus().isOk()
                .expectBody(RoomResponse.class)
                .value(deactivated -> {
                    assertThat(deactivated.active()).isFalse();
                });

        Room room = roomRepository.findById(response.id()).orElseThrow();

        assertThat(room.isActive()).isFalse();
    }

    @Test
    @DisplayName("POST /api/v1/rooms/{id}/deactivate should return 404 for non existing room")
    void shouldReturn404WhenRoomNotFound() {

        webTestClient.post()
                .uri("/api/v1/rooms/{id}/deactivate", 999L)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Nested
    @DisplayName("GET /api/v1/rooms/search")
    class SearchRoomsApiTests {

        @BeforeEach
        void setUpRooms() {
            webTestClient.post().uri("/api/v1/rooms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new CreateRoomRequest("Small Room", 5, "Conference"))
                    .exchange().expectStatus().isCreated();

            webTestClient.post().uri("/api/v1/rooms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new CreateRoomRequest("Medium Room", 15, "Conference"))
                    .exchange().expectStatus().isCreated();

            RoomResponse large = webTestClient.post().uri("/api/v1/rooms")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(new CreateRoomRequest("Large Room", 30, "Auditorium"))
                    .exchange().expectStatus().isCreated()
                    .expectBody(RoomResponse.class)
                    .returnResult().getResponseBody();

            assertThat(large).isNotNull();
            webTestClient.post()
                    .uri("/api/v1/rooms/{id}/deactivate", large.id())
                    .exchange().expectStatus().isOk();
        }

        @Test
        @DisplayName("200 OK — без фильтров возвращает все комнаты")
        void shouldReturnAllRoomsWithoutFilters() {
            webTestClient.get()
                    .uri("/api/v1/rooms/search")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(3);
        }

        @Test
        @DisplayName("200 OK — фильтр по имени возвращает совпадающие комнаты")
        void shouldFilterByName() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("name", "Small")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(1)
                    .jsonPath("$.content[0].name").isEqualTo("Small Room");
        }

        @Test
        @DisplayName("200 OK — фильтр по capacity возвращает комнаты с вместимостью >= значения")
        void shouldFilterByCapacity() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("capacity", 15)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(2)  // Medium (15) + Large (30)
                    .jsonPath("$.content[?(@.name == 'Small Room')]").doesNotExist();
        }

        @Test
        @DisplayName("200 OK — фильтр по capacity исключает комнаты ниже порога")
        void shouldExcludeRoomsBelowCapacity() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("capacity", 20)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(1)  // только Large (30)
                    .jsonPath("$.content[0].name").isEqualTo("Large Room");
        }

        @Test
        @DisplayName("200 OK — фильтр по description возвращает совпадающие комнаты")
        void shouldFilterByDescription() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("description", "Conference")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(2)
                    .jsonPath("$.content[?(@.name == 'Large Room')]").doesNotExist();
        }

        @Test
        @DisplayName("200 OK — фильтр active=true не возвращает деактивированные комнаты")
        void shouldFilterByActiveStatus() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("active", true)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(2)
                    .jsonPath("$.content[?(@.name == 'Large Room')]").doesNotExist();
        }

        @Test
        @DisplayName("200 OK — фильтр active=false возвращает только деактивированные комнаты")
        void shouldFilterByInactiveStatus() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("active", false)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(1)
                    .jsonPath("$.content[0].name").isEqualTo("Large Room");
        }

        @Test
        @DisplayName("200 OK — комбинированный фильтр сужает выборку")
        void shouldFilterByNameAndActive() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("name", "Room")
                            .queryParam("active", true)
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(2)
                    .jsonPath("$.content[?(@.name == 'Large Room')]").doesNotExist();
        }

        @Test
        @DisplayName("200 OK — пустая страница если ничего не найдено")
        void shouldReturnEmptyPageWhenNoMatch() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("name", "Несуществующая комната")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.totalElements").isEqualTo(0)
                    .jsonPath("$.content").isEmpty();
        }

        @Test
        @DisplayName("200 OK — результаты отсортированы по имени по умолчанию")
        void shouldReturnRoomsSortedByNameByDefault() {
            webTestClient.get()
                    .uri("/api/v1/rooms/search")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content[0].name").isEqualTo("Large Room")
                    .jsonPath("$.content[1].name").isEqualTo("Medium Room")
                    .jsonPath("$.content[2].name").isEqualTo("Small Room");
        }

        @Test
        @DisplayName("400 Bad Request — невалидное значение capacity")
        void shouldReturn400WhenCapacityIsInvalid() {
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/v1/rooms/search")
                            .queryParam("capacity", 0)  // @Min(1)
                            .build())
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.errorCode").isEqualTo("VALIDATION_ERROR");
        }
    }
}
