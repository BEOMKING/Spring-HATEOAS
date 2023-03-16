package com.study.hateoas.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EventTest {
    @Test
    void builder() {
        // Given
        final Event event = Event.builder()
            .name("Inflearn Spring REST API")
            .description("REST API development with Spring")
            .build();

        // When
        final String name = event.getName();

        // Then
        assertThat(name).isEqualTo("Inflearn Spring REST API");
    }

    @Test
    void javaBean() {
        // Given
        final String name = "Event";
        final String description = "Spring";

        // When
        final Event event = new Event();
        event.setName(name);
        event.setDescription(description);

        // Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(description);
    }
}
