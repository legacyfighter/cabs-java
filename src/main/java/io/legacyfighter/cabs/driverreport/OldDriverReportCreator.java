package io.legacyfighter.cabs.driverreport;

import io.legacyfighter.cabs.dto.*;
import io.legacyfighter.cabs.entity.Claim;
import io.legacyfighter.cabs.entity.Driver;
import io.legacyfighter.cabs.entity.DriverSession;
import io.legacyfighter.cabs.entity.Transit;
import io.legacyfighter.cabs.repository.ClaimRepository;
import io.legacyfighter.cabs.repository.DriverRepository;
import io.legacyfighter.cabs.repository.DriverSessionRepository;
import io.legacyfighter.cabs.service.DriverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static io.legacyfighter.cabs.entity.DriverAttribute.DriverAttributeName.MEDICAL_EXAMINATION_REMARKS;

@Service
class OldDriverReportCreator {

    @Autowired
    private DriverService driverService;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private DriverSessionRepository driverSessionRepository;

    @Autowired
    private Clock clock;

    public DriverReport createReport(Long driverId, int lastDays) {
        DriverReport driverReport = new DriverReport();
        DriverDTO driverDTO = driverService.loadDriver(driverId);
        driverReport.setDriverDTO(driverDTO);
        Driver driver = driverRepository.getOne(driverId);
        driver
                .getAttributes()
                .stream()
                .filter(attr -> !attr.getName().equals(MEDICAL_EXAMINATION_REMARKS))
                .forEach(attr -> driverReport.getAttributes()
                        .add(new DriverAttributeDTO(attr)));
        Instant beggingOfToday = Instant.now(clock).atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC);
        Instant since = beggingOfToday.minus(lastDays, ChronoUnit.DAYS);
        List<DriverSession> allByDriverAndLoggedAtAfter = driverSessionRepository.findAllByDriverAndLoggedAtAfter(driver, since);
        Map<DriverSessionDTO, List<TransitDTO>> sessionsWithTransits = new HashMap<>();
        for (DriverSession session : allByDriverAndLoggedAtAfter) {
            DriverSessionDTO dto = new DriverSessionDTO(session);
            List<Transit> transitsInSession =
                    driver.getTransits().stream()
                            .filter(t -> t.getStatus().equals(Transit.Status.COMPLETED) && !t.getCompleteAt().isBefore(session.getLoggedAt()) && !t.getCompleteAt().isAfter(session.getLoggedOutAt())).collect(Collectors.toList());

            List<TransitDTO> transitsDtosInSession = new ArrayList<>();
            for (Transit t : transitsInSession) {
                TransitDTO transitDTO = new TransitDTO(t);
                List<Claim> byOwnerAndTransit = claimRepository.findByOwnerAndTransit(t.getClient(), t);
                if (!byOwnerAndTransit.isEmpty()) {
                    ClaimDTO claim = new ClaimDTO(byOwnerAndTransit.get(0));
                    transitDTO.setClaimDTO(claim);
                }
                transitsDtosInSession.add(transitDTO);
            }
            sessionsWithTransits.put(dto, transitsDtosInSession);
        }
        driverReport.setSessions(sessionsWithTransits);
        return driverReport;
    }
}