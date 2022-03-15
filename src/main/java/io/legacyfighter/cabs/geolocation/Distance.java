package io.legacyfighter.cabs.geolocation;

import javax.persistence.Embeddable;
import java.util.Locale;
import java.util.Objects;

@Embeddable
public final class Distance {

    public static final Distance ZERO = ofKm(0);

    private static final double MILES_TO_KILOMETERS_RATIO = 1.609344f;

    private double km;

    public static Distance ofKm(float km) {
        return new Distance(km);
    }

    public static Distance ofKm(double km) {
        return new Distance(km);
    }

    private Distance(double km) {
        this.km = km;
    }

    public float toKmInFloat() {
        return (float) km;
    }

    private Distance() {

    }

    public String printIn(String unit) {
        if (unit.equals("km")) {
            if (km == Math.ceil(km)) {
                return String.format(Locale.US, "%d", Math.round(km)) + "km";

            }
            return String.format(Locale.US, "%.3f", km) + "km";
        }
        if (unit.equals("miles")) {
            double km = this.km / MILES_TO_KILOMETERS_RATIO;
            if (km == Math.ceil(km)) {
                return String.format(Locale.US, "%d", Math.round(km)) + "miles";
            }
            return String.format(Locale.US, "%.3f", km) + "miles";

        }
        if (unit.equals("m")) {
            return String.format(Locale.US, "%d", Math.round(km * 1000)) + "m";
        }
        throw new IllegalArgumentException("Invalid unit " + unit);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Distance that = (Distance) o;
        return Objects.equals(km, that.km);
    }

    @Override
    public int hashCode() {
        return Objects.hash(km);
    }

    public double toKmInDouble() {
        return km;
    }

    @Override
    public String toString() {
        return "Distance{" +
                "km=" + km +
                '}';
    }

    public Distance add(Distance travelled) {
        return Distance.ofKm(this.km + travelled.km);
    }
}


