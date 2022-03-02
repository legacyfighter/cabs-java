package io.legacyfighter.cabs.common;


import org.springframework.context.ApplicationEventPublisher;

public class EventsPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public EventsPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void publish(Event event) {
        applicationEventPublisher.publishEvent(event);
    }
}

