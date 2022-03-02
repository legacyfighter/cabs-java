package io.legacyfighter.cabs.config;


import io.legacyfighter.cabs.common.EventsPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class EventPublisherConfiguration {

    @Bean
    EventsPublisher eventsPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new EventsPublisher(applicationEventPublisher);
    }
}

