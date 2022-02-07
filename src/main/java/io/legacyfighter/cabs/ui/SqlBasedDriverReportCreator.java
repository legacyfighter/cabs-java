package io.legacyfighter.cabs.ui;

import io.legacyfighter.cabs.distance.Distance;
import io.legacyfighter.cabs.dto.*;
import io.legacyfighter.cabs.entity.*;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.legacyfighter.cabs.entity.DriverAttribute.DriverAttributeName.MEDICAL_EXAMINATION_REMARKS;

@Service
public class SqlBasedDriverReportCreator {

    public static final String QUERY_FOR_DRIVER_WITH_ATTRS =
            "SELECT d.id, d.first_name, d.last_name, d.driver_license, " +
                    "d.photo, d.status, d.type, attr.name, attr.value " +
            "FROM Driver d " +
            "LEFT JOIN driver_attribute attr ON d.id = attr.driver_id " +
            "WHERE d.id = :driverId AND attr.name <> :filteredAttr";

    public static final String QUERY_FOR_SESSIONS = "SELECT ds.logged_at, ds.logged_out_at, ds.plates_number, ds.car_class, ds.car_brand, " +
            "t.id as TRANSIT_ID, t.name as TARIFF_NAME, t.status as TRANSIT_STATUS, t.km, t.km_rate, " +
            "t.price, t.drivers_fee, t.estimated_price, t.base_fee, " +
            "t.date_time, t.published, t.accepted_at, t.started, t.complete_at, t.car_type, " +
            "cl.id as CLAIM_ID, cl.owner_id, cl.reason, cl.incident_description, cl.status as CLAIM_STATUS, cl.creation_date, " +
            "cl.completion_date, cl.change_date, cl.completion_mode, cl.claim_no, " +
            "af.country as AF_COUNTRY, af.city as AF_CITY, af.street AS AF_STREET, af.building_number AS AF_NUMBER, " +
            "ato.country as ATO_COUNTRY, ato.city as ATO_CITY, ato.street AS ATO_STREET, ato.building_number AS ATO_NUMBER, " +
            "FROM driver_session ds " +
            "LEFT JOIN transit t ON t.driver_id = ds.driver_id " +
            "LEFT JOIN Address af ON t.from_id = af.id " +
            "LEFT JOIN Address ato ON t.to_id = ato.id " +
            "LEFT JOIN claim cl ON cl.transit_id = t.id " +
            "WHERE ds.driver_id = :driverId AND t.status = :transitStatus " +
            "AND ds.logged_at >= :since " +
            "AND t.complete_at >= ds.logged_at " +
            "AND t.complete_at <= ds.logged_out_at GROUP BY ds.id";

    private final EntityManager em;

    private final Clock clock;

    public SqlBasedDriverReportCreator(EntityManager entityManager, Clock clock) {
        this.em = entityManager;
        this.clock = clock;
    }

    public DriverReport createReport(Long driverId, int lastDays) {
        DriverReport driverReport = new DriverReport();
        List<Tuple> driverInfo = em
                .createNativeQuery(QUERY_FOR_DRIVER_WITH_ATTRS, Tuple.class)
                .setParameter("driverId", driverId)
                .setParameter("filteredAttr", MEDICAL_EXAMINATION_REMARKS.toString())
                .getResultList();

        driverInfo.forEach(tuple -> addAttrToReport(driverReport, tuple));
        driverInfo.stream().findFirst().ifPresent(tuple -> addDriverToReport(driverReport, tuple));

        Stream<Tuple> resultStream = em
                .createNativeQuery(QUERY_FOR_SESSIONS, Tuple.class)
                .setParameter("driverId", driverId)
                .setParameter("transitStatus", Transit.Status.COMPLETED.ordinal())
                .setParameter("since", calculateStartingPoint(lastDays))
                .getResultStream();
        Map<DriverSessionDTO, List<TransitDTO>> sessions = resultStream
                .collect(
                        Collectors.toMap(
                                this::retrieveDrivingSession,
                                tuple -> List.of(
                                        retrieveTransit(tuple)),
                                (existing, newOne) -> {
                                    existing.addAll(newOne);
                                    return existing;
                                }
                        ));

        driverReport.setSessions(sessions);
        return driverReport;
    }

    private TransitDTO retrieveTransit(Tuple tuple) {
        return new TransitDTO(((Number) tuple.get("TRANSIT_ID")).longValue(), (String) tuple.get("TARIFF_NAME"),
                Transit.Status.values()[((Integer) tuple.get("TRANSIT_STATUS"))], null,
                Distance.ofKm(((Number) tuple.get("KM")).floatValue()), ((Number) tuple.get("KM_RATE")).floatValue(),
                new BigDecimal(((Number) tuple.get("PRICE")).intValue()),
                new BigDecimal(((Number) tuple.get("DRIVERS_FEE")).intValue()),
                new BigDecimal(((Number) tuple.get("ESTIMATED_PRICE")).intValue()),
                new BigDecimal(((Number) tuple.get("BASE_FEE")).intValue()),
                ((Timestamp) tuple.get("DATE_TIME")).toInstant(), ((Timestamp) tuple.get("PUBLISHED")).toInstant(),
                ((Timestamp) tuple.get("ACCEPTED_AT")).toInstant(), ((Timestamp) tuple.get("STARTED")).toInstant(),
                ((Timestamp) tuple.get("COMPLETE_AT")).toInstant(), retrieveClaim(tuple), null, retrieveFromAddress(tuple), retrieveToAddress(tuple),
                CarType.CarClass.valueOf((String) tuple.get("CAR_TYPE")), null);
    }

    private DriverSessionDTO retrieveDrivingSession(Tuple tuple) {
        return new DriverSessionDTO(((Timestamp) tuple.get("LOGGED_AT")).toInstant(), ((Timestamp) tuple.get("LOGGED_OUT_AT")).toInstant(), (String) tuple.get("PLATES_NUMBER"), CarType.CarClass.valueOf((String) tuple.get("CAR_CLASS")), (String) tuple.get("CAR_BRAND"));
    }

    private AddressDTO retrieveToAddress(Tuple tuple) {
        return new AddressDTO((String) tuple.get("AF_COUNTRY"), (String) tuple.get("AF_CITY"), (String) tuple.get("AF_STREET"), tuple.get("AF_NUMBER") == null? null : ((Integer) tuple.get("AF_NUMBER")));
    }

    private AddressDTO retrieveFromAddress(Tuple tuple) {
        return new AddressDTO((String) tuple.get("AF_COUNTRY"), (String) tuple.get("AF_CITY"), (String) tuple.get("AF_STREET"), tuple.get("AF_NUMBER") == null? null : ((Integer) tuple.get("AF_NUMBER")));
    }

    private ClaimDTO retrieveClaim(Tuple tuple) {
        Number claim_id = (Number) tuple.get("CLAIM_ID");
        if (claim_id == null) {
            return null;
        }
        return new ClaimDTO(claim_id.longValue(), ((Number) tuple.get("OWNER_ID")).longValue(), ((Number) tuple.get("TRANSIT_ID")).longValue(),
                (String) tuple.get("REASON"), (String) tuple.get("INCIDENT_DESCRIPTION"), ((Timestamp) tuple.get("CREATION_DATE")).toInstant(),
                tuple.get("COMPLETION_DATE") == null ? null : ((Timestamp) tuple.get("COMPLETION_DATE")).toInstant(),
                tuple.get("CHANGE_DATE") == null ? null : ((Timestamp) tuple.get("CHANGE_DATE")).toInstant(), tuple.get("COMPLETION_MODE") == null? null : Claim.CompletionMode.valueOf((String) tuple.get("COMPLETION_MODE")),
                Claim.Status.valueOf((String) tuple.get("CLAIM_STATUS")), (String) tuple.get("CLAIM_NO"));
    }

    private Instant calculateStartingPoint(int lastDays) {
        Instant beggingOfToday = Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant since = beggingOfToday.minus(lastDays, ChronoUnit.DAYS);
        return since;
    }

    private void addDriverToReport(DriverReport driverReport, Tuple tuple) {
        Integer driverType = (Integer) tuple.get("TYPE");
        driverReport.setDriverDTO(new DriverDTO(((Number) tuple.get("ID")).longValue(), (String) tuple.get("FIRST_NAME"), (String) tuple.get("LAST_NAME"), (String) tuple.get("DRIVER_LICENSE"), (String) tuple.get("PHOTO"), Driver.Status.values()[(Integer) tuple.get("STATUS")], driverType == null ? null : Driver.Type.values()[driverType]));
    }

    private void addAttrToReport(DriverReport driverReport, Tuple tuple) {
        driverReport.addAttr(DriverAttribute.DriverAttributeName.valueOf((String) tuple.get("NAME")), (String) tuple.get(8));
    }
}
