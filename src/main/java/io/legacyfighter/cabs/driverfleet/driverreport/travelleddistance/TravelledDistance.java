package io.legacyfighter.cabs.driverfleet.driverreport.travelleddistance;

import io.legacyfighter.cabs.geolocation.Distance;

import javax.persistence.*;
import java.time.*;
import java.util.Objects;
import java.util.UUID;

import static java.lang.String.format;

@Entity
class TravelledDistance {

    @Id
    private UUID intervalId = UUID.randomUUID();

    @Column(nullable = false)
    private Long driverId;

    @Column(nullable = false)
    private TimeSlot timeSlot;

    @Column(nullable = false)
    private double lastLatitude;

    @Column(nullable = false)
    private double lastLongitude;

    @Embedded
    private Distance distance;

    private TravelledDistance() {
    }

    TravelledDistance(Long driverId, TimeSlot timeSlot, double lastLatitude, double lastLongitude) {
        this.driverId = driverId;
        this.timeSlot = timeSlot;
        this.lastLatitude = lastLatitude;
        this.lastLongitude = lastLongitude;
        this.distance = Distance.ZERO;
    }

    boolean contains(Instant timestamp) {
        return timeSlot.contains(timestamp);
    }

    double getLastLongitude() {
        return lastLongitude;
    }

    double getLastLatitude() {
        return lastLatitude;
    }

    void addDistance(Distance travelled, double latitude, double longitude) {
        this.distance = distance.add(travelled);
        this.lastLatitude = latitude;
        this.lastLongitude = longitude;
    }

    boolean endsAt(Instant instant) {
        return timeSlot.endsAt(instant);
    }

    boolean isBefore(Instant now) {
        return timeSlot.isBefore(now);
    }
}

@Embeddable
class TimeSlot {

    static final int FIVE_MINUTES = 300;

    @Column(nullable = false)
    private Instant beginning;

    @Column(nullable = false)
    private Instant end;

    private TimeSlot(Instant beginning, Instant end) {
        this.beginning = beginning;
        this.end = end;
    }

    static TimeSlot of(Instant beginning, Instant end) {
        if (!end.isAfter(beginning)) {
            throw new IllegalArgumentException(format("From %s is after to %s", beginning, end));
        }
        return new TimeSlot(beginning, end);
    }

    static TimeSlot slotThatContains(Instant seed) {
        LocalDateTime startOfDay = seed.atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();
        LocalDateTime seedDateTime = seed.atZone(ZoneId.systemDefault()).toLocalDateTime();
        long secondsFromStartOfDay = Duration.between(startOfDay, seedDateTime).toSeconds();
        long intervals = (long) Math.floor((secondsFromStartOfDay / (double) FIVE_MINUTES));
        Instant from = startOfDay.atZone(ZoneId.systemDefault()).plusSeconds(intervals * FIVE_MINUTES).toInstant();
        return new TimeSlot(from, from.plusSeconds(FIVE_MINUTES));
    }

    private TimeSlot() {
    }

    boolean contains(Instant timestamp) {
        return timestamp.isBefore(end) && !beginning.isAfter(timestamp);
    }

    boolean endsAt(Instant timestamp) {
        return this.end.equals(timestamp);
    }

    boolean isBefore(Instant timestamp) {
        return end.isBefore(timestamp);
    }

    TimeSlot prev() {
        return new TimeSlot(beginning.minusSeconds(FIVE_MINUTES), end.minusSeconds(FIVE_MINUTES));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeSlot that = (TimeSlot) o;
        return Objects.equals(beginning, that.beginning) && Objects.equals(end, that.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(beginning, end);
    }

    Instant beginning() {
        return beginning;
    }

    Instant end() {
        return end;
    }

    @Override
    public String toString() {
        return "Slot{" +
                "beginning=" + beginning +
                ", end=" + end +
                '}';
    }
}
