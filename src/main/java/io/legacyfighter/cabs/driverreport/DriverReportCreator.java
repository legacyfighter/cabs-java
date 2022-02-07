package io.legacyfighter.cabs.driverreport;

import io.legacyfighter.cabs.dto.DriverReport;
import org.springframework.stereotype.Service;

import static io.legacyfighter.cabs.config.FeatureFlags.DRIVER_REPORT_SQL;

@Service
public class DriverReportCreator {

    private final SqlBasedDriverReportCreator sqlBasedDriverReportCreator;
    private final OldDriverReportCreator oldDriverReportCreator;

    DriverReportCreator(SqlBasedDriverReportCreator sqlBasedDriverReportCreator, OldDriverReportCreator oldDriverReportCreator) {
        this.sqlBasedDriverReportCreator = sqlBasedDriverReportCreator;
        this.oldDriverReportCreator = oldDriverReportCreator;
    }

    public DriverReport create(Long driverId, int days) {
        if (shouldUseNewReport()) {
            return sqlBasedDriverReportCreator.createReport(driverId, days);
        }
        return oldDriverReportCreator.createReport(driverId, days);
    }

    private boolean shouldUseNewReport() {
        return DRIVER_REPORT_SQL.isActive();
    }
}


