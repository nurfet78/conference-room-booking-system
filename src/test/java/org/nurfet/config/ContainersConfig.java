package org.nurfet.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.testcontainers.postgresql.PostgreSQLContainer;

@TestConfiguration
@Slf4j
public class ContainersConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer postgreSQLContainer() {
        return new PostgreSQLContainer("postgres:18");
    }

    @EventListener(ApplicationReadyEvent.class)
    public void applicationReady() {
        log.warn("╔════════════════════════════════════════════════════════════╗");
        log.warn("║  DEV MODE: Запущен временный PostgreSQL-контейнер          ║");
        log.warn("║  Все данные будут УНИЧТОЖЕНЫ при остановке приложения!     ║");
        log.warn("║  Не используйте этот режим в production!                   ║");
        log.warn("╚════════════════════════════════════════════════════════════╝");
    }
}