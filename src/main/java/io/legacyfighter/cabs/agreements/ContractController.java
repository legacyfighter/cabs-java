package io.legacyfighter.cabs.agreements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping("/contracts/")
    ResponseEntity<ContractDTO> create(@RequestBody ContractDTO contractDTO) {
        ContractDTO created = contractService.createContract(contractDTO);
        return ResponseEntity.ok(created);
    }


    @GetMapping("/contracts/{id}}")
    public ResponseEntity<ContractDTO> find(@PathVariable Long id) {
        ContractDTO contract = contractService.findDto(id);
        return ResponseEntity.ok(contract);
    }

    @PostMapping("/contracts/{id}/attachment")
    public ResponseEntity<ContractAttachmentDTO> proposeAttachment(@PathVariable Long id, @RequestBody ContractAttachmentDTO contractAttachmentDTO) {
        ContractAttachmentDTO dto = contractService.proposeAttachment(id, contractAttachmentDTO);
        return ResponseEntity.ok(dto);
    }

    @PostMapping("/contracts/{contractId}/attachment/{attachmentId}/reject")
    public ResponseEntity rejectAttachment(@PathVariable Long contractId, @PathVariable Long attachmentId) {
        contractService.rejectAttachment(attachmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contracts/{contractId}/attachment/{attachmentId}/accept")
    public ResponseEntity acceptAttachment(@PathVariable Long contractId, @PathVariable Long attachmentId) {
        contractService.acceptAttachment(attachmentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/contracts/{contractId}/attachment/{attachmentId}")
    public ResponseEntity removeAttachment(@PathVariable Long contractId, @PathVariable Long attachmentId) {
        contractService.removeAttachment(contractId, attachmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contracts/{id}/accept")
    public ResponseEntity acceptContract(@PathVariable Long id) {
        contractService.acceptContract(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/contracts/{id}/reject")
    public ResponseEntity rejectContract(@PathVariable Long id) {
        contractService.rejectContract(id);
        return ResponseEntity.ok().build();
    }



}
