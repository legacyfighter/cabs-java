package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.common.Fixtures;
import io.legacyfighter.cabs.common.TestWithGraphDB;
import io.legacyfighter.cabs.dto.AddressDTO;
import io.legacyfighter.cabs.dto.AnalyzedAddressesDTO;
import io.legacyfighter.cabs.entity.Address;
import io.legacyfighter.cabs.entity.Client;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.service.GeocodingService;
import io.legacyfighter.cabs.ui.TransitAnalyzerController;
import org.awaitility.Awaitility;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Clock;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.legacyfighter.cabs.entity.CarType.CarClass.VAN;
import static java.time.LocalDateTime.of;
import static java.time.ZoneOffset.UTC;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.*;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AnalyzeNearbyTransitsIntegrationTest extends TestWithGraphDB {

    @Autowired
    Fixtures fixtures;

    @Autowired
    TransitAnalyzerController transitAnalyzerController;

    @MockBean
    GeocodingService geocodingService;

    @MockBean
    Clock clock;

    @BeforeEach
    public void setup() {
        fixtures.anActiveCarCategory(VAN);
        when(geocodingService.geocodeAddress(any(Address.class))).thenReturn(new double[]{1, 1});
    }

    @Test
    void canFindLongestTravel() {
        //given
        Client client = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(of(2021, 1, 1, 0, 00).toInstant(UTC));
        Driver driver = fixtures.aNearbyDriver("WA001");
        //and
        Address address1 = new Address("1_1", "1", "1", "1", 1);
        Address address2 = new Address("1_2", "2", "2", "2", 2);
        Address address3 = new Address("1_3", "3", "3", "3", 3);
        Address address4 = new Address("1_4", "4", "4", "4", 3);
        Address address5 = new Address("1_5", "5", "5", "5", 3);
        //and
        // 1-2-3-4
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 00).toInstant(UTC), of(2021, 1, 1, 0, 10).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 15).toInstant(UTC), of(2021, 1, 1, 0, 20).toInstant(UTC), client, driver, address2, address3, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 25).toInstant(UTC), of(2021, 1, 1, 0, 30).toInstant(UTC), client, driver, address3, address4, clock);
        // 1-2-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 00).toInstant(UTC), of(2021, 1, 2, 0, 10).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 15).toInstant(UTC), of(2021, 1, 2, 0, 20).toInstant(UTC), client, driver, address2, address3, clock);
        // 1-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 3, 0, 00).toInstant(UTC), of(2021, 1, 3, 0, 10).toInstant(UTC), client, driver, address1, address3, clock);
        // 3-1-2-5-4-5
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 00).toInstant(UTC), of(2021, 2, 1, 0, 10).toInstant(UTC), client, driver, address3, address1, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 20).toInstant(UTC), of(2021, 2, 1, 0, 25).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 30).toInstant(UTC), of(2021, 2, 1, 0, 35).toInstant(UTC), client, driver, address2, address5, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 40).toInstant(UTC), of(2021, 2, 1, 0, 45).toInstant(UTC), client, driver, address5, address4, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 50).toInstant(UTC), of(2021, 2, 1, 0, 55).toInstant(UTC), client, driver, address4, address5, clock);

        //when
        AnalyzedAddressesDTO analyzedAddressesDTO = transitAnalyzerController.analyze(client.getId(), address1.getId());


        //then
        // 1-2-5-4-5
        addressesContainExactly(analyzedAddressesDTO, address1, address2, address5, address4, address5);
    }

    @Test
    void canFindLongestTravelFromMultipleClients() {
        //given
        Client client1 = fixtures.aClient();
        Client client2 = fixtures.aClient();
        Client client3 = fixtures.aClient();
        Client client4 = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(of(2021, 1, 1, 0, 00).toInstant(UTC));
        Driver driver = fixtures.aNearbyDriver("WA001");
        //and
        Address address1 = new Address("2_1", "1", "1", "1", 1);
        Address address2 = new Address("2_2", "2", "2", "2", 2);
        Address address3 = new Address("2_3", "3", "3", "3", 3);
        Address address4 = new Address("2_4", "4", "4", "4", 3);
        Address address5 = new Address("2_5", "5", "5", "5", 3);
        //and
        // 1-2-3-4
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 00).toInstant(UTC), of(2021, 1, 1, 0, 10).toInstant(UTC), client1, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 15).toInstant(UTC), of(2021, 1, 1, 0, 20).toInstant(UTC), client1, driver, address2, address3, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 25).toInstant(UTC), of(2021, 1, 1, 0, 30).toInstant(UTC), client1, driver, address3, address4, clock);
        // 1-2-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 00).toInstant(UTC), of(2021, 1, 2, 0, 10).toInstant(UTC), client2, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 15).toInstant(UTC), of(2021, 1, 2, 0, 20).toInstant(UTC), client2, driver, address2, address3, clock);
        // 1-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 3, 0, 00).toInstant(UTC), of(2021, 1, 3, 0, 10).toInstant(UTC), client3, driver, address1, address3, clock);
        // 1-3-2-5-4-5
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 00).toInstant(UTC), of(2021, 2, 1, 0, 10).toInstant(UTC), client4, driver, address1, address3, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 20).toInstant(UTC), of(2021, 2, 1, 0, 25).toInstant(UTC), client4, driver, address3, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 30).toInstant(UTC), of(2021, 2, 1, 0, 35).toInstant(UTC), client4, driver, address2, address5, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 40).toInstant(UTC), of(2021, 2, 1, 0, 45).toInstant(UTC), client4, driver, address5, address4, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 2, 1, 0, 50).toInstant(UTC), of(2021, 2, 1, 0, 55).toInstant(UTC), client4, driver, address4, address5, clock);

        //when
        AnalyzedAddressesDTO analyzedAddressesDTO = transitAnalyzerController.analyze(client1.getId(), address1.getId());

        //then
        // 1-2-3-4
        addressesContainExactly(analyzedAddressesDTO, address1, address2, address3, address4);
    }

    @Test
    void canFindLongestTravelWithLongStops() {
        //given
        Client client = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(of(2021, 1, 1, 0, 00).toInstant(UTC));
        Driver driver = fixtures.aNearbyDriver("WA001");
        //and
        Address address1 = new Address("3_1", "1", "1", "1", 1);
        Address address2 = new Address("3_2", "2", "2", "2", 2);
        Address address3 = new Address("3_3", "3", "3", "3", 3);
        Address address4 = new Address("3_4", "4", "4", "4", 3);
        Address address5 = new Address("3_5", "5", "5", "5", 3);
        //and
        // 1-2-3-4-(stop)-5-1
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 00).toInstant(UTC), of(2021, 1, 1, 0, 05).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 10).toInstant(UTC), of(2021, 1, 1, 0, 15).toInstant(UTC), client, driver, address2, address3, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 20).toInstant(UTC), of(2021, 1, 1, 0, 25).toInstant(UTC), client, driver, address3, address4, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 1, 00).toInstant(UTC), of(2021, 1, 1, 1, 10).toInstant(UTC), client, driver, address4, address5, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 1, 10).toInstant(UTC), of(2021, 1, 1, 1, 15).toInstant(UTC), client, driver, address5, address1, clock);

        //when
        AnalyzedAddressesDTO analyzedAddressesDTO = transitAnalyzerController.analyze(client.getId(), address1.getId());

        //then
        // 1-2-3-4
        addressesContainExactly(analyzedAddressesDTO, address1, address2, address3, address4);
    }

    @Test
    void canFindLongestTravelWithLoops() {
        //given
        Client client = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(of(2021, 1, 1, 0, 00).toInstant(UTC));
        Driver driver = fixtures.aNearbyDriver("WA001");
        //and
        Address address1 = new Address("4_1", "1", "1", "1", 1);
        Address address2 = new Address("4_2", "2", "2", "2", 2);
        Address address3 = new Address("4_3", "3", "3", "3", 3);
        Address address4 = new Address("4_4", "4", "4", "4", 3);
        Address address5 = new Address("4_5", "5", "5", "5", 3);
        //and
        // 5-1-2-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 00).toInstant(UTC), of(2021, 1, 1, 0, 5).toInstant(UTC), client, driver, address5, address1, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 6).toInstant(UTC), of(2021, 1, 1, 0, 10).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 15).toInstant(UTC), of(2021, 1, 1, 0, 20).toInstant(UTC), client, driver, address2, address3, clock);
        // 3-2-1
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 00).toInstant(UTC), of(2021, 1, 2, 0, 10).toInstant(UTC), client, driver, address3, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 2, 0, 15).toInstant(UTC), of(2021, 1, 2, 0, 20).toInstant(UTC), client, driver, address2, address1, clock);
        // 1-5
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 3, 0, 00).toInstant(UTC), of(2021, 1, 3, 0, 10).toInstant(UTC), client, driver, address1, address5, clock);
        // 3-1-2-5-4-5
        fixtures.aRequestedAndCompletedTransit(50, of(2000, 2, 1, 0, 00).toInstant(UTC), of(2020, 2, 1, 0, 10).toInstant(UTC), client, driver, address3, address1, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2020, 2, 1, 0, 20).toInstant(UTC), of(2020, 2, 1, 0, 25).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2020, 2, 1, 0, 30).toInstant(UTC), of(2020, 2, 1, 0, 35).toInstant(UTC), client, driver, address2, address5, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2020, 2, 1, 0, 40).toInstant(UTC), of(2020, 2, 1, 0, 45).toInstant(UTC), client, driver, address5, address4, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2020, 2, 1, 0, 50).toInstant(UTC), of(2020, 2, 1, 0, 55).toInstant(UTC), client, driver, address4, address5, clock);

        //when
        AnalyzedAddressesDTO analyzedAddressesDTO = transitAnalyzerController.analyze(client.getId(), address5.getId());

        //then
        // 5-1-2-3
        addressesContainExactly(analyzedAddressesDTO, address5, address1, address2, address3);
    }

    @Test
    void canFindLongTravelBetweenOthers() {
        //given
        Client client = fixtures.aClient();
        //and
        when(clock.instant()).thenReturn(of(2021, 1, 1, 0, 00).toInstant(UTC));
        Driver driver = fixtures.aNearbyDriver("WA001");
        //and
        Address address1 = new Address("5_1", "1", "1", "1", 1);
        Address address2 = new Address("5_2", "2", "2", "2", 2);
        Address address3 = new Address("5_3", "3", "3", "3", 3);
        Address address4 = new Address("5_4", "4", "4", "4", 3);
        Address address5 = new Address("5_5", "4", "4", "4", 3);
        //and
        // 1-2-3
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 00).toInstant(UTC), of(2021, 1, 1, 0, 5).toInstant(UTC), client, driver, address1, address2, clock);
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 10).toInstant(UTC), of(2021, 1, 1, 0, 15).toInstant(UTC), client, driver, address2, address3, clock);
        // 4-5
        fixtures.aRequestedAndCompletedTransit(50, of(2021, 1, 1, 0, 20).toInstant(UTC), of(2021, 1, 1, 0, 25).toInstant(UTC), client, driver, address4, address5, clock);

        //when
        AnalyzedAddressesDTO analyzedAddressesDTO = transitAnalyzerController.analyze(client.getId(), address1.getId());

        //then
        //1-2-3
        addressesContainExactly(analyzedAddressesDTO, address1, address2, address3);
    }

    void addressesContainExactly(AnalyzedAddressesDTO analyzedAddressesDTO, Address... expectedAddresses) {
        List<Integer> expectedHashes = Stream.of(expectedAddresses)
                .map(Address::getHash)
                .collect(Collectors.toList());
        await().ignoreExceptions().until(() -> {
            assertThat(hashesOfAddresses(analyzedAddressesDTO)).containsExactlyElementsOf(expectedHashes);
            return true;
        });
    }

    List<Integer> hashesOfAddresses(AnalyzedAddressesDTO analyzedAddressesDTO) {
        return analyzedAddressesDTO.getAddresses().stream().map(AddressDTO::getHash).collect(Collectors.toList());
    }

}
