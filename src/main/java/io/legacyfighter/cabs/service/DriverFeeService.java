package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.repository.DriverFeeRepository;
import io.legacyfighter.cabs.repository.TransitRepository;
import io.legacyfighter.cabs.entity.DriverFee;
import io.legacyfighter.cabs.entity.Transit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverFeeService {

    @Autowired
    private DriverFeeRepository driverFeeRepository;

    @Autowired
    private TransitRepository transitRepository;

    @Transactional
    public Integer calculateDriverFee(Long transitId) {
        Transit transit = transitRepository.getOne(transitId);
        if (transit == null) {
            throw new IllegalArgumentException("transit does not exist, id = " + transitId);
        }
        if (transit.getDriversFee() != null) {
            return transit.getDriversFee();
        }
        Integer transitPrice = transit.getPrice();
        DriverFee driverFee = driverFeeRepository.findByDriver(transit.getDriver());
        if (driverFee == null) {
            throw new IllegalArgumentException("driver Fees not defined for driver, driver id = " + transit.getDriver().getId());
        }
        Integer finalFee;
        if (driverFee.getFeeType().equals(DriverFee.FeeType.FLAT)) {
            finalFee = transitPrice - driverFee.getAmount();
        } else {
            finalFee = transitPrice * driverFee.getAmount() / 100;

        }

        return Math.max(finalFee, driverFee.getMin() == null ? 0 : driverFee.getMin());
    }
}
