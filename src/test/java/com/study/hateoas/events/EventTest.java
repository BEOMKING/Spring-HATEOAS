package com.study.hateoas.events;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

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

	@Test
	void 가격이_있으면_무료가_아니다() {
		// Given
		final Event event = Event.builder()
				.name("Spring REST API")
				.description("REST API development with Spring")
				.basePrice(100)
				.maxPrice(200)
				.location("Think More")
				.build();

		// When
		event.update();

		// Then
		assertThat(event.isFree()).isFalse();
	}

	@Test
	void 가격이_없으면_무료이다() {
		// Given
		final Event event = Event.builder()
				.name("Spring REST API")
				.description("REST API development with Spring")
				.basePrice(0)
				.maxPrice(0)
				.location("Think More")
				.build();

		// When
		event.update();

		// Then
		assertThat(event.isFree()).isTrue();
	}

	@Test
	void 장소가_있으면_온라인이다() {
		// Given
		final Event event = Event.builder()
				.name("Spring REST API")
				.description("REST API development with Spring")
				.location("Think More")
				.build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isTrue();
	}

	@ParameterizedTest
	@ValueSource(strings = {"", " "})
	@NullSource
	void 장소가_없으면_오프라인이다(final String location) {
		// Given
		final Event event = Event.builder()
				.name("Spring REST API")
				.description("REST API development with Spring")
				.location(location)
				.build();

		// When
		event.update();

		// Then
		assertThat(event.isOffline()).isFalse();
	}

}
