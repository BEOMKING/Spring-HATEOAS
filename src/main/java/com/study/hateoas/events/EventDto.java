package com.study.hateoas.events;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EventDto {
    private final String name;
    private final String description;
    private final LocalDateTime beginEnrollmentDateTime;
    private final LocalDateTime closeEnrollmentDateTime;
    private final LocalDateTime beginEventDateTime;
    private final LocalDateTime endEventDateTime;
    private final String location; // (optional) 이게 없으면 온라인 모임
    private final int basePrice; // (optional)
    private final int maxPrice; // (optional)
    private final int limitOfEnrollment;

    public EventDto(final String name, final String description,
        final LocalDateTime beginEnrollmentDateTime,
        final LocalDateTime closeEnrollmentDateTime, final LocalDateTime beginEventDateTime,
        final LocalDateTime endEventDateTime, final String location, final int basePrice,
        final int maxPrice,
        final int limitOfEnrollment) {
        this.name = name;
        this.description = description;
        this.beginEnrollmentDateTime = beginEnrollmentDateTime;
        this.closeEnrollmentDateTime = closeEnrollmentDateTime;
        this.beginEventDateTime = beginEventDateTime;
        this.endEventDateTime = endEventDateTime;
        this.location = location;
        this.basePrice = basePrice;
        this.maxPrice = maxPrice;
        this.limitOfEnrollment = limitOfEnrollment;
    }
}
