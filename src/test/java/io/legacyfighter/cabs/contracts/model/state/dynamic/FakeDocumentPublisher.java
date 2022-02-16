package io.legacyfighter.cabs.contracts.model.state.dynamic;

import io.legacyfighter.cabs.contracts.model.state.dynamic.config.events.DocumentEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FakeDocumentPublisher implements ApplicationEventPublisher {

    private Set<Object> events = new HashSet();

    @Override
    public void publishEvent(Object event) {
        events.add(event);
    }

    public void contains(Class<? extends DocumentEvent> event){
        boolean found = events.stream().anyMatch(e -> e.getClass().equals(event));
        assertTrue(found);
    }

    public void noEvents() {
        assertEquals(0, events.size());
    }

    public void reset() {
        events.clear();
    }


}
