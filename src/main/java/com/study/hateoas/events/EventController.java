package com.study.hateoas.events;

import com.study.hateoas.index.IndexController;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/events", produces = MediaTypes.HAL_JSON_VALUE)
public class EventController {
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;
    private final EventValidator eventValidator;

    public EventController(final EventRepository eventRepository, final ModelMapper modelMapper,
                           final EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.modelMapper = modelMapper;
        this.eventValidator = eventValidator;
    }

    @PostMapping
    public ResponseEntity createEvent(@RequestBody @Valid final EventDto eventDto, final Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index")));
        }

        eventValidator.validate(eventDto, errors);
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().body(EntityModel.of(errors, linkTo(methodOn(IndexController.class).index()).withRel("index")));
        }

        final Event savedEvent = eventRepository.save(modelMapper.map(eventDto, Event.class));
        savedEvent.update();

        final WebMvcLinkBuilder selfLinkBuilder = linkTo(EventController.class).slash(savedEvent.getId());
        final URI createdUri = selfLinkBuilder.toUri();
        final EntityModel<Event> eventResource = EntityModel.of(savedEvent);
        eventResource.add(selfLinkBuilder.slash(savedEvent).withSelfRel());
        eventResource.add(selfLinkBuilder.withRel("query-events"));
        eventResource.add(selfLinkBuilder.withRel("update-event"));
        eventResource.add(Link.of("/docs/index.html#resources-events-create").withRel("profile"));

        return ResponseEntity.created(createdUri).body(eventResource);
    }

    @GetMapping
    public ResponseEntity queryEvents(final Pageable pageable, final PagedResourcesAssembler<Event> assembler) {
        final Page<Event> events = this.eventRepository.findAll(pageable);
        final PagedModel<EntityModel<Event>> model = assembler.toModel(events, e -> EntityModel.of(e, linkTo(EventController.class).slash(e.getId()).withSelfRel()));
        model.add(Link.of("/docs/index.html#resources-events-list").withRel("profile"));
        return ResponseEntity.ok(model);
    }
}
