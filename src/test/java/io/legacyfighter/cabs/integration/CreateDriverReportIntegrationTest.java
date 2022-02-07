package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.dto.CarTypeDTO;
import io.legacyfighter.cabs.dto.DriverAttributeDTO;
import io.legacyfighter.cabs.dto.DriverReport;
import io.legacyfighter.cabs.dto.TransitDTO;
import io.legacyfighter.cabs.entity.*;
import io.legacyfighter.cabs.entity.CarType.CarClass;
import io.legacyfighter.cabs.repository.AddressRepository;
import io.legacyfighter.cabs.service.*;
import io.legacyfighter.cabs.ui.DriverReportController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import static io.legacyfighter.cabs.entity.CarType.CarClass.PREMIUM;
import static io.legacyfighter.cabs.entity.CarType.CarClass.VAN;
import static io.legacyfighter.cabs.entity.Driver.Status.ACTIVE;
import static io.legacyfighter.cabs.entity.DriverAttribute.DriverAttributeName.*;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
class CreateDriverReportIntegrationTest {

    static Instant DAY_BEFORE_YESTERDAY = LocalDateTime.of(1989, 12, 12, 12, 12).toInstant(ZoneOffset.UTC);
    static Instant YESTERDAY = DAY_BEFORE_YESTERDAY.plus(1, ChronoUnit.DAYS);
    static Instant TODAY = YESTERDAY.plus(1, ChronoUnit.DAYS);

    @Autowired
    TransitService transitService;

    @Autowired
    DriverTrackingService driverTrackingService;

    @Autowired
    DriverSessionService driverSessionService;

    @Autowired
    CarTypeService carTypeService;

    @Autowired
    Fixtures fixtures;

    @Autowired
    DriverReportController driverReportController;

    @Autowired
    AddressRepository addressRepository;

    @MockBean
    GeocodingService geocodingService;

    @Autowired
    ClaimService claimService;

    @MockBean
    Clock clock;

    @BeforeEach
    public void setup() {
        anActiveCarCategory(VAN);
        anActiveCarCategory(PREMIUM);
    }

    @Test
    void shouldCreateDriversReport() {
        //given
        Client client= fixtures.aClient();
        //and
        Driver driver = aDriver(ACTIVE, "JAN", "NOWAK", "FARME100165AB5EW");
        //and
        fixtures.driverHasAttribute(driver, COMPANY_NAME, "UBER");
        fixtures.driverHasAttribute(driver, PENALTY_POINTS, "21");
        fixtures.driverHasAttribute(driver, MEDICAL_EXAMINATION_REMARKS, "private info");
        //and
        driverHasDoneSessionAndPicksSomeoneUpInCar(driver, client, VAN, "WU1213", "SCODA FABIA", TODAY);
        driverHasDoneSessionAndPicksSomeoneUpInCar(driver, client, VAN, "WU1213", "SCODA OCTAVIA", YESTERDAY);
        Transit inBmw = driverHasDoneSessionAndPicksSomeoneUpInCar(driver, client, VAN, "WU1213", "BMW M2", DAY_BEFORE_YESTERDAY);
        //and
        fixtures.createClaim(client, inBmw, "za szybko");

        //when
        DriverReport driverReportWithin2days = loadReportIncludingPastDays(driver, 2);
        DriverReport driverReportWithin1day = loadReportIncludingPastDays(driver, 1);
        DriverReport driverReportForJustToday = loadReportIncludingPastDays(driver, 0);

        //then
        assertEquals(3, driverReportWithin2days.getSessions().keySet().size());
        assertEquals(2, driverReportWithin1day.getSessions().keySet().size());
        assertEquals(1, driverReportForJustToday.getSessions().keySet().size());


        assertEquals("FARME100165AB5EW", driverReportWithin2days.getDriverDTO().getDriverLicense());
        assertEquals("JAN", driverReportWithin2days.getDriverDTO().getFirstName());
        assertEquals("NOWAK", driverReportWithin2days.getDriverDTO().getLastName());
        assertEquals(2, driverReportWithin2days.getAttributes().size());
        assertTrue(driverReportWithin2days.getAttributes().contains(new DriverAttributeDTO(COMPANY_NAME, "UBER")));
        assertTrue(driverReportWithin2days.getAttributes().contains(new DriverAttributeDTO(PENALTY_POINTS, "21")));

        assertThat(transitsInSessionIn("SCODA FABIA", driverReportWithin2days))
                .hasSize(1);
        assertThat(transitsInSessionIn("SCODA FABIA", driverReportWithin2days).get(0).getClaimDTO()).isNull();

        assertThat(transitsInSessionIn("SCODA OCTAVIA", driverReportWithin2days))
                .hasSize(1);
        assertThat(transitsInSessionIn("SCODA OCTAVIA", driverReportWithin2days).get(0).getClaimDTO()).isNull();

        assertThat(transitsInSessionIn("BMW M2", driverReportWithin2days))
                .hasSize(1);
        assertThat(transitsInSessionIn("BMW M2", driverReportWithin2days).get(0).getClaimDTO()).isNotNull();
        assertThat(transitsInSessionIn("BMW M2", driverReportWithin2days).get(0).getClaimDTO().getReason()).isEqualTo("za szybko");
    }

    DriverReport loadReportIncludingPastDays(Driver driver, int days) {
        Mockito.when(clock.instant()).thenReturn(TODAY);
        DriverReport driverReport = driverReportController.loadReportForDriver(driver.getId(), days);
        return driverReport;
    }

    List<TransitDTO> transitsInSessionIn(String carBrand, DriverReport driverReport) {
        return driverReport
                .getSessions()
                .entrySet()
                .stream()
                .filter(e -> e.getKey().getCarBrand().equals(carBrand))
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(toList());
    }

    Transit driverHasDoneSessionAndPicksSomeoneUpInCar(Driver driver, Client client, CarClass carClass, String plateNumber, String carBrand, Instant when) {
        when(clock.instant()).thenReturn(when);
        Long driverId = driver.getId();
        driverSessionService.logIn(driverId, plateNumber, carClass, carBrand);
        driverTrackingService.registerPosition(driverId, 10, 20);
        Transit transit = transitService.createTransit(client.getId(), address("PL", "MAZ", "WAW", "STREET", 1, 10, 20), address("PL", "MAZ", "WAW", "STREET", 100, 10.01, 20.01), carClass);
        transitService.publishTransit(transit.getId());
        transitService.acceptTransit(driverId, transit.getId());
        transitService.startTransit(driverId, transit.getId());
        transitService.completeTransit(driverId, transit.getId(), address("PL", "MAZ", "WAW", "STREET", 100, 10.01, 20.01));
        driverSessionService.logOutCurrentSession(driverId);
        return transit;
    }


    Driver aDriver(Driver.Status status, String name, String lastName, String driverLicense) {
        Driver driver = fixtures.aDriver(ACTIVE, name, lastName, driverLicense);
        fixtures.driverHasFee(driver, DriverFee.FeeType.FLAT, 10);
        return driver;
    }

    Address address(String country, String district, String city, String street, Integer buildingNumber, double latitude, double longitude) {
        Address address = new Address();
        address.setCountry(country);
        address.setDistrict(district);
        address.setCity(city);
        address.setStreet(street);
        address.setBuildingNumber(buildingNumber);
        when(geocodingService.geocodeAddress(address)).thenReturn(new double[]{latitude, longitude});
        return addressRepository.save(address);
    }

    CarType anActiveCarCategory(CarClass carClass) {
        CarTypeDTO carTypeDTO = new CarTypeDTO();
        carTypeDTO.setCarClass(carClass);
        carTypeDTO.setDescription("opis");
        CarType carType = carTypeService.create(carTypeDTO);
        IntStream.range(1, carType.getMinNoOfCarsToActivateClass() + 1)
                .forEach(i -> carTypeService.registerCar(carType.getCarClass()));
        carTypeService.activate(carType.getId());
        return carType;
    }
}