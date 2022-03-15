package io.legacyfighter.cabs.crm.claims;

import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
class ClaimNumberGenerator {

    private final ClaimRepository claimRepository;

    ClaimNumberGenerator(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    String generate(Claim claim) {
        Long count = claimRepository.count();
        Long prefix = count;
        if (count == 0) {
            prefix = 1L;
        }
        DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy")
                .withZone(ZoneId.systemDefault());
        return count + "---" + DATE_TIME_FORMATTER.format(claim.getCreationDate());
    }
}
