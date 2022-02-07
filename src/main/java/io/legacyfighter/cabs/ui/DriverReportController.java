package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.dto.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DriverReportController {

    private final SqlBasedDriverReportCreator sqlBasedDriverReportCreator;

    public DriverReportController(SqlBasedDriverReportCreator sqlBasedDriverReportCreator) {
        this.sqlBasedDriverReportCreator = sqlBasedDriverReportCreator;
    }

    @GetMapping("/driverreport/{driverId}")
    @Transactional
    public DriverReport loadReportForDriver(@PathVariable Long driverId, @RequestParam int lastDays) {
        return sqlBasedDriverReportCreator.createReport(driverId, lastDays);
    }

}
