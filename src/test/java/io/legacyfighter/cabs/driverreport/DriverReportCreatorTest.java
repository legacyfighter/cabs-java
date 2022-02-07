package io.legacyfighter.cabs.driverreport;

import io.legacyfighter.cabs.config.FeatureFlags;
import io.legacyfighter.cabs.dto.DriverReport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.togglz.junit5.AllDisabled;
import org.togglz.junit5.AllEnabled;
import org.togglz.testing.TestFeatureManager;

import static io.legacyfighter.cabs.config.FeatureFlags.DRIVER_REPORT_CREATION_RECONCILIATION;
import static io.legacyfighter.cabs.config.FeatureFlags.DRIVER_REPORT_SQL;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class DriverReportCreatorTest {

    static final int LAST_DAYS = 3;
    static final long DRIVER_ID = 1L;
    public static final DriverReport SQL_REPORT = new DriverReport();
    public static final DriverReport OLD_REPORT = new DriverReport();

    @InjectMocks
    DriverReportCreator reportCreator;

    @Mock
    OldDriverReportCreator oldDriverReportCreator;

    @Mock
    SqlBasedDriverReportCreator sqlBasedDriverReportCreator;

    @Mock
    DriverReportReconciliation driverReportReconciliation;

    @Test
    @AllEnabled(FeatureFlags.class)
    void callsNewReport(TestFeatureManager testFeatureManager) {
        //given
        newSqlWayReturnsReport();
        testFeatureManager.disable(DRIVER_REPORT_CREATION_RECONCILIATION);
        testFeatureManager.enable(DRIVER_REPORT_SQL);
        //when
        reportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        verify(sqlBasedDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verifyNoInteractions(oldDriverReportCreator);
    }

    @Test
    @AllDisabled(FeatureFlags.class)
    void callsOldReport(TestFeatureManager testFeatureManager) {
        //given
        oldWayReturnsReport();
        testFeatureManager.disable(DRIVER_REPORT_CREATION_RECONCILIATION);
        testFeatureManager.disable(DRIVER_REPORT_SQL);

        //when
        reportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        verify(oldDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verifyNoInteractions(sqlBasedDriverReportCreator);
    }

    @Test
    @AllEnabled(FeatureFlags.class)
    void callsReconciliationAndUsesOldReport(TestFeatureManager testFeatureManager) {
        //given
        bothWaysReturnReport();
        testFeatureManager.enable(DRIVER_REPORT_CREATION_RECONCILIATION);
        testFeatureManager.disable(DRIVER_REPORT_SQL);

        //when
        reportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        verify(oldDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verify(sqlBasedDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verify(driverReportReconciliation).compare(OLD_REPORT, SQL_REPORT);
    }

    @Test
    @AllEnabled(FeatureFlags.class)
    void callsReconciliationAndUsesNewReport(TestFeatureManager testFeatureManager) {
        //given
        bothWaysReturnReport();
        testFeatureManager.enable(DRIVER_REPORT_CREATION_RECONCILIATION);
        testFeatureManager.enable(DRIVER_REPORT_SQL);

        //when
        reportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        verify(sqlBasedDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verify(oldDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
        verify(driverReportReconciliation).compare(OLD_REPORT, SQL_REPORT);
    }

    void bothWaysReturnReport() {
        oldWayReturnsReport();
        newSqlWayReturnsReport();
    }

    void newSqlWayReturnsReport() {
        Mockito.when(sqlBasedDriverReportCreator.createReport(DRIVER_ID, LAST_DAYS)).thenReturn(SQL_REPORT);
    }

    void oldWayReturnsReport() {
        Mockito.when(oldDriverReportCreator.createReport(DRIVER_ID, LAST_DAYS)).thenReturn(OLD_REPORT);
    }

}