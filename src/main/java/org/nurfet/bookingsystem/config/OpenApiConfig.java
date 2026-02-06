package org.nurfet.bookingsystem.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI bookingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Room Booking API")
                        .version("1.0.0")
                        .description("""
                                REST API для управления переговорными комнатами и бронированием.
                                
                                ## Возможности
                                - Управление переговорными комнатами (CRUD)
                                - Бронирование временных слотов
                                - Проверка доступности комнат
                                - Подтверждение и отмена бронирований
                                
                                ## Статусы бронирования
                                - **PENDING** — Ожидает подтверждения
                                - **CONFIRMED** — Подтверждено
                                - **CANCELLED** — Отменено
                                - **EXPIRED** — Истекло (время прошло)
                                """
                        )
                        .contact(new Contact()
                                .name("Booking Team")
                                .email("booking-team@example.com")
                                .url("https://example.com"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8888")
                                .description("Локальная разработка"),
                        new Server()
                                .url("https://api.staging.example.com")
                                .description("Staging-окружение"),
                        new Server()
                                .url("https://api.example.com")
                                .description("Production")))
                .tags(List.of(
                        new Tag()
                                .name("Комнаты")
                                .description("Управление переговорными комнатами: создание, просмотр, обновление, деактивация"),
                        new Tag()
                                .name("Бронирования")
                                .description("Управление бронированиями: создание, просмотр, подтверждение, отмена"),
                        new Tag()
                                .name("Доступность")
                                .description("Проверка доступности комнат и поиск конфликтов")
                ));
    }
}
