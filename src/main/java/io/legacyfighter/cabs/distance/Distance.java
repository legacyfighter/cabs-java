package io.legacyfighter.cabs.distance;

import java.util.Locale;
import java.util.Objects;

public final class Distance {

    private static final float MILES_TO_KILOMETERS_RATIO = 1.609344f;

    private final float km;

    public static Distance ofKm(float km) {
        return new Distance(km);
    }

    private Distance(float km) {
        this.km = km;
    }

    public float toKmInFloat() {
        return km;
    }

    public String printIn(String unit) {
        if (unit.equals("km")) {
            if (km == Math.ceil(km)) {
                return String.format(Locale.US, "%d", Math.round(km)) + "km";

            }
            return String.format(Locale.US, "%.3f", km) + "km";
        }
        if (unit.equals("miles")) {
            float km = this.km / MILES_TO_KILOMETERS_RATIO;
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
}


