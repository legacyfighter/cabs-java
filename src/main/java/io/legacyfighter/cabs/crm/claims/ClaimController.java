package io.legacyfighter.cabs.crm.claims;

import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClaimController {

    private final ClaimService claimService;

    public ClaimController(ClaimService claimService) {
        this.claimService = claimService;
    }

    @PostMapping("/claims/createDraft")
    public ResponseEntity<ClaimDTO> create(@RequestBody ClaimDTO claimDTO) {
        Claim created = claimService.create(claimDTO);
        return ResponseEntity.ok(toDto(created));
    }

    @PostMapping("/claims/send")
    ResponseEntity<ClaimDTO> sendNew(@RequestBody ClaimDTO claimDTO) {
        claimDTO.setDraft(false);
        Claim claim = claimService.create(claimDTO);
        return ResponseEntity.ok(toDto(claim));
    }

    @PostMapping("/claims/{id}/markInProcess")
    ResponseEntity<ClaimDTO> markAsInProcess(@PathVariable Long id) {
        Claim claim = claimService.setStatus(Status.IN_PROCESS, id);
        return ResponseEntity.ok(toDto(claim));
    }

    @GetMapping("/claims/{id}}")
    @Transactional
    public ResponseEntity<ClaimDTO> find(@PathVariable Long id) {
        Claim claim = claimService.find(id);
        ClaimDTO dto = toDto(claim);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/claims/{id}}")
    ResponseEntity<ClaimDTO> tryToAutomaticallyResolve(@PathVariable Long id) {
        Claim claim = claimService.tryToResolveAutomatically(id);
        return ResponseEntity.ok(toDto(claim));
    }

    private ClaimDTO toDto(Claim claim) {
        return new ClaimDTO(claim);

    }
}
