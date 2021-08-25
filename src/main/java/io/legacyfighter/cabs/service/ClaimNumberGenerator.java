package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.entity.Claim;
import io.legacyfighter.cabs.repository.ClaimRepository;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class ClaimNumberGenerator {

    private final ClaimRepository claimRepository;

    public ClaimNumberGenerator(ClaimRepository claimRepository) {
        this.claimRepository = claimRepository;
    }

    public String generate(Claim claim) {
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
