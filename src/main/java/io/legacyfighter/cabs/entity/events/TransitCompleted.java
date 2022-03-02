package io.legacyfighter.cabs.entity.events;

import io.legacyfighter.cabs.common.Event;

import java.time.Instant;

public class TransitCompleted implements Event {

    public final Long clientId;
    public final Long transitId;
    public final Integer addressFromHash;
    public final Integer addressToHash;
    public final Instant started;
    public final Instant completeAt;
    public final Instant eventTimestamp;

    public TransitCompleted(Long clientId, Long transitId, Integer addressFromHash, Integer addressToHash, Instant started, Instant completeAt, Instant eventTimestamp) {
        this.clientId = clientId;
        this.transitId = transitId;
        this.addressFromHash = addressFromHash;
        this.addressToHash = addressToHash;
        this.started = started;
        this.completeAt = completeAt;
        this.eventTimestamp = eventTimestamp;
    }
}
