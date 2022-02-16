package io.legacyfighter.cabs.contracts.model.state.dynamic.config.actions;

import io.legacyfighter.cabs.contracts.model.ContentId;
import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.content.DocumentNumber;
import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;
import io.legacyfighter.cabs.contracts.model.state.dynamic.config.events.DocumentEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.lang.reflect.Constructor;
import java.util.function.BiFunction;

public class PublishEvent implements BiFunction<DocumentHeader, ChangeCommand, Void> {

    private Class<? extends DocumentEvent> eventClass;

    private ApplicationEventPublisher publisher;

    public PublishEvent(Class<? extends DocumentEvent> eventClass, ApplicationEventPublisher publisher) {
        this.eventClass = eventClass;
        this.publisher = publisher;
    }

    @Override
    public Void apply(DocumentHeader documentHeader, ChangeCommand command) {
        DocumentEvent event;
        try {
            Constructor<? extends DocumentEvent> constructor = eventClass.getDeclaredConstructor(Long.class, String.class, ContentId.class, DocumentNumber.class);
            event = constructor.newInstance(documentHeader.getId(), documentHeader.getStateDescriptor(), documentHeader.getContentId(), documentHeader.getDocumentNumber());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        publisher.publishEvent(event);

        return null;
    }
}
