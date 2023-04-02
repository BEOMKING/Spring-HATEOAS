package com.study.hateoas.events;

import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@ExtendWith({ RestDocumentationExtension.class, SpringExtension.class })
class EventControllerTests {
	MockMvc mockMvc;

	@Autowired
	ObjectMapper objectMapper;

	@BeforeEach
	void setUp(
			final WebApplicationContext webApplicationContext,
			final RestDocumentationContextProvider restDocumentation) {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
//				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(documentationConfiguration(restDocumentation)
				.operationPreprocessors()
				.withRequestDefaults(prettyPrint())
				.withResponseDefaults(prettyPrint()))
				.alwaysDo(print()).build();
	}

	@Test
	@DisplayName("정상적인 요청이 들어오면 201을 반환한다")
	void createEvent() throws Exception {
		// Given
		final EventDto eventDto = EventDto.builder()
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 3, 22, 23, 30))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 3, 23, 23, 30))
				.beginEventDateTime(LocalDateTime.of(2023, 3, 24, 23, 30))
				.endEventDateTime(LocalDateTime.of(2023, 3, 25, 23, 30))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Think More")
				.build();

		// When & Then
		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_VALUE)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(eventDto)))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("id").exists())
				.andExpect(header().exists(HttpHeaders.LOCATION))
				.andExpect(header().string(HttpHeaders.CONTENT_TYPE, MediaTypes.HAL_JSON_VALUE))
				.andExpect(jsonPath("free").value(false))
				.andExpect(jsonPath("offline").value(true))
				.andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
				.andExpect(jsonPath("_links.self").exists())
				.andExpect(jsonPath("_links.query-events").exists())
				.andExpect(jsonPath("_links.update-event").exists())
				.andDo(document("create-event",
						links(
								linkWithRel("self").description("link to self"),
								linkWithRel("query-events").description("link to query events"),
								linkWithRel("update-event").description("link to update an existing event"),
								linkWithRel("profile").description("link to profile")
						),
						requestHeaders(
								headerWithName(HttpHeaders.ACCEPT).description("accept header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
						),
						requestFields(
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("Description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("Date time of end of new event"),
								fieldWithPath("location").description("Location of new event"),
								fieldWithPath("basePrice").description("Base price of new event"),
								fieldWithPath("maxPrice").description("Max price of new event"),
								fieldWithPath("limitOfEnrollment").description("Limit of enrollment")
						),
						responseHeaders(
								headerWithName(HttpHeaders.LOCATION).description("Location header"),
								headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type")
						),
						responseFields(
								fieldWithPath("id").description("Identifier of new event"),
								fieldWithPath("name").description("Name of new event"),
								fieldWithPath("description").description("Description of new event"),
								fieldWithPath("beginEnrollmentDateTime").description("Date time of begin of new event"),
								fieldWithPath("closeEnrollmentDateTime").description("Date time of close of new event"),
								fieldWithPath("beginEventDateTime").description("Date time of begin of new event"),
								fieldWithPath("endEventDateTime").description("Date time of end of new event"),
								fieldWithPath("location").description("Location of new event"),
								fieldWithPath("basePrice").description("Base price of new event"),
								fieldWithPath("maxPrice").description("Max price of new event"),
								fieldWithPath("limitOfEnrollment").description("Limit of enrollment"),
								fieldWithPath("free").description("It tells if this event is free or not"),
								fieldWithPath("offline").description("It tells if this event is offline meeting or not"),
								fieldWithPath("eventStatus").description("Event status"),
								fieldWithPath("_links.self.href").ignored(),
								fieldWithPath("_links.query-events.href").ignored(),
								fieldWithPath("_links.update-event.href").ignored(),
								fieldWithPath("_links.profile.href").ignored()
						)
				));
	}

	@Test
	void 불필요한_데이터가_함께_들어오면_400에러를_반환한다() throws Exception {
		// Given
		final Event event = Event.builder()
				.id(100)
				.name("Spring")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 3, 22, 23, 30))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 3, 23, 23, 30))
				.beginEventDateTime(LocalDateTime.of(2023, 3, 24, 23, 30))
				.endEventDateTime(LocalDateTime.of(2023, 3, 25, 23, 30))
				.basePrice(100)
				.maxPrice(200)
				.limitOfEnrollment(100)
				.location("Think More")
				.free(true)
				.offline(false)
				.build();

		// When & Then
		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_VALUE)
						.accept(MediaTypes.HAL_JSON)
						.content(objectMapper.writeValueAsString(event)))
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void 유효하지않은_정보가_들어오면_400에러를_반환한다() throws Exception {
		// Given
		final EventDto eventDto = EventDto.builder()
				.name("Spring Rest API 교육")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 1, 1, 9, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 3, 1, 9, 0))
				.beginEventDateTime(LocalDateTime.of(2023, 3, 13, 16, 0))
				.endEventDateTime(LocalDateTime.of(2023, 4, 27, 17, 0))
				.location("Think More")
				.build();

		// When & Then
		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(eventDto))
						.accept(MediaTypes.HAL_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists());
	}

	@Test
	void 날짜값이_유효하지_않으면_400에러를_반환한다() throws Exception {
		// Given
		final EventDto eventDto1 = EventDto.builder()
				.name("Spring Rest API 교육")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 2, 1, 9, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 1, 1, 9, 0))
				.beginEventDateTime(LocalDateTime.of(2023, 4, 13, 16, 0))
				.endEventDateTime(LocalDateTime.of(2023, 4, 27, 17, 0))
				.location("Think More")
				.build();

		final EventDto eventDto2 = EventDto.builder()
				.name("Spring Rest API 교육")
				.description("REST API Development with Spring")
				.beginEnrollmentDateTime(LocalDateTime.of(2023, 1, 1, 9, 0))
				.closeEnrollmentDateTime(LocalDateTime.of(2023, 2, 1, 9, 0))
				.beginEventDateTime(LocalDateTime.of(2023, 7, 13, 16, 0))
				.endEventDateTime(LocalDateTime.of(2023, 4, 27, 17, 0))
				.location("Think More")
				.build();

		// When & Then
		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(eventDto1))
						.accept(MediaTypes.HAL_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists());

		mockMvc.perform(post("/api/events").contentType(MediaType.APPLICATION_JSON_VALUE)
						.content(objectMapper.writeValueAsString(eventDto2))
						.accept(MediaTypes.HAL_JSON_VALUE))
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$[0].objectName").exists())
				.andExpect(jsonPath("$[0].defaultMessage").exists())
				.andExpect(jsonPath("$[0].code").exists());
	}
}
