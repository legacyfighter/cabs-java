package io.legacyfighter.cabs.loyalty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwardsAccountController {

    @Autowired
    private AwardsService awardsService;

    @PostMapping("/clients/{clientId}/awards")
    ResponseEntity register(@PathVariable Long clientId) {
        awardsService.registerToProgram(clientId);
        return ResponseEntity.ok(awardsService.findBy(clientId));
    }

    @PostMapping("/clients/{clientId}/awards/activate")
    ResponseEntity<AwardsAccountDTO> activate(@PathVariable Long clientId) {
        awardsService.activateAccount(clientId);
        return ResponseEntity.ok(awardsService.findBy(clientId));
    }

    @PostMapping("/clients/{clientId}/awards/deactivate")
    ResponseEntity<AwardsAccountDTO> deactivate(@PathVariable Long clientId) {
        awardsService.deactivateAccount(clientId);
        return ResponseEntity.ok(awardsService.findBy(clientId));
    }

    @GetMapping("/clients/{clientId}/awards/balance")
    ResponseEntity<Integer> calculateBalance(@PathVariable Long clientId) {
        return ResponseEntity.ok(awardsService.calculateBalance(clientId));
    }

    @PostMapping("/clients/{clientId}/awards/transfer/{toClientId}/{howMuch}")
    ResponseEntity<AwardsAccountDTO> transferMiles(@PathVariable Long clientId, @PathVariable Long toClientId, @PathVariable Integer howMuch) {
        awardsService.transferMiles(clientId, toClientId, howMuch);
        return ResponseEntity.ok(awardsService.findBy(clientId));
    }

    @GetMapping("/clients/{clientId}/awards/")
    ResponseEntity<AwardsAccountDTO> findBy(@PathVariable Long clientId) {
        return ResponseEntity.ok(awardsService.findBy(clientId));
    }
}
