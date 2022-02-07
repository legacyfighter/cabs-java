package io.legacyfighter.cabs.driverreport;

import io.legacyfighter.cabs.dto.DriverReport;
import org.springframework.stereotype.Service;

import static io.legacyfighter.cabs.config.FeatureFlags.DRIVER_REPORT_CREATION_RECONCILIATION;
import static io.legacyfighter.cabs.config.FeatureFlags.DRIVER_REPORT_SQL;

@Service
public class DriverReportCreator {

    private final SqlBasedDriverReportCreator sqlBasedDriverReportCreator;
    private final OldDriverReportCreator oldDriverReportCreator;
    private final DriverReportReconciliation driverReportReconciliation;

    DriverReportCreator(SqlBasedDriverReportCreator sqlBasedDriverReportCreator, OldDriverReportCreator oldDriverReportCreator, DriverReportReconciliation driverReportReconciliation) {
        this.sqlBasedDriverReportCreator = sqlBasedDriverReportCreator;
        this.oldDriverReportCreator = oldDriverReportCreator;
        this.driverReportReconciliation = driverReportReconciliation;
    }

    public DriverReport create(Long driverId, int days) {
        DriverReport newReport = null;
        DriverReport oldReport = null;
        if (shouldCompare()) {
            newReport = sqlBasedDriverReportCreator.createReport(driverId, days);
            oldReport = oldDriverReportCreator.createReport(driverId, days);
            driverReportReconciliation.compare(oldReport, newReport);
        }
        if (shouldUseNewReport()) {
            if (newReport == null) {
                newReport = sqlBasedDriverReportCreator.createReport(driverId, days);
            }
            return newReport;
        }
        if (oldReport == null) {
            oldReport = oldDriverReportCreator.createReport(driverId, days);
        }
        return oldReport;
    }

    private boolean shouldCompare() {
        return DRIVER_REPORT_CREATION_RECONCILIATION.isActive();
    }

    private boolean shouldUseNewReport() {
        return DRIVER_REPORT_SQL.isActive();
    }
}

interface DriverReportReconciliation {

    void compare(DriverReport oldOne, DriverReport newOne);
}

@Service
class TestDummyReconciliation implements DriverReportReconciliation {

    @Override
    public void compare(DriverReport oldOne, DriverReport newOne) {
        //noop
    }
}
