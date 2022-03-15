package io.legacyfighter.cabs.driverfleet;

import io.legacyfighter.cabs.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DriverFeeService {

    @Autowired
    private DriverFeeRepository driverFeeRepository;

    @Transactional
    public Money calculateDriverFee(Money transitPrice, Long driverId) {
        DriverFee driverFee = driverFeeRepository.findByDriverId(driverId);
        if (driverFee == null) {
            throw new IllegalArgumentException("driver Fees not defined for driver, driver id = " + driverId);
        }
        Money finalFee;
        if (driverFee.getFeeType().equals(DriverFee.FeeType.FLAT)) {
            finalFee = transitPrice.subtract(new Money(driverFee.getAmount()));
        } else {
            finalFee = transitPrice.percentage(driverFee.getAmount());
        }

        return new Money(Math.max(finalFee.toInt(), driverFee.getMin() == null ? 0 : driverFee.getMin().toInt()));
    }
}
