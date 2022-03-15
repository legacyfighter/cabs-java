package io.legacyfighter.cabs.driverfleet;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverFeeRepository extends JpaRepository<DriverFee, Long> {

    DriverFee findByDriverId(Long driverId);

}

