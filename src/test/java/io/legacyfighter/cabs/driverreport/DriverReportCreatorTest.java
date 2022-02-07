package io.legacyfighter.cabs.driverreport;

import io.legacyfighter.cabs.config.FeatureFlags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.togglz.junit5.AllDisabled;
import org.togglz.junit5.AllEnabled;

@ExtendWith(MockitoExtension.class)
class DriverReportCreatorTest {

    static final int LAST_DAYS = 3;
    static final long DRIVER_ID = 1L;

    @InjectMocks
    DriverReportCreator driverReportCreator;

    @Mock
    OldDriverReportCreator oldDriverReportCreator;

    @Mock
    SqlBasedDriverReportCreator sqlBasedDriverReportCreator;


    @Test
    @AllEnabled(FeatureFlags.class)
    void callsNewReport() {
        //when
        driverReportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        Mockito.verify(sqlBasedDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
    }

    @Test
    @AllDisabled(FeatureFlags.class)
    void callsOldReport() {
        //when
        driverReportCreator.create(DRIVER_ID, LAST_DAYS);

        //then
        Mockito.verify(oldDriverReportCreator).createReport(DRIVER_ID, LAST_DAYS);
    }

}