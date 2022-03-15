package io.legacyfighter.cabs.driverfleet;

import javax.persistence.Embeddable;

@Embeddable
class DriverLicense {

    static final String DRIVER_LICENSE_REGEX = "^[A-Z9]{5}\\d{6}[A-Z9]{2}\\d[A-Z]{2}$";

    private String driverLicense;

    DriverLicense() {
    }

    private DriverLicense(String driverLicense) {
        this.driverLicense = driverLicense;
    }

    static DriverLicense withLicense(String driverLicense) {
        if (driverLicense == null || driverLicense.isEmpty() || !driverLicense.matches(DRIVER_LICENSE_REGEX)) {
            throw new IllegalArgumentException("Illegal license no = " + driverLicense);
        }
        return new DriverLicense(driverLicense);
    }

    static DriverLicense withoutValidation(String driverLicense) {
        return new DriverLicense(driverLicense);
    }

    @Override
    public String toString() {
        return "DriverLicense{" +
                "driverLicense='" + driverLicense + '\'' +
                '}';
    }

    String asString() {
        return driverLicense;
    }
}
