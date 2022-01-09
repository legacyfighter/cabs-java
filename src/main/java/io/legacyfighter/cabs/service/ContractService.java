package io.legacyfighter.cabs.service;

import io.legacyfighter.cabs.dto.ContractAttachmentDTO;
import io.legacyfighter.cabs.dto.ContractDTO;
import io.legacyfighter.cabs.entity.Contract;
import io.legacyfighter.cabs.entity.ContractAttachment;
import io.legacyfighter.cabs.repository.ContractAttachmentRepository;
import io.legacyfighter.cabs.repository.ContractRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractAttachmentRepository contractAttachmentRepository;

    @Transactional
    public Contract createContract(ContractDTO contractDTO) {
        int partnerContractsCount = contractRepository.findByPartnerName(contractDTO.getPartnerName()).size() + 1;
        Contract contract = new Contract(contractDTO.getPartnerName(), contractDTO.getSubject(), "C/" + partnerContractsCount + "/" + contractDTO.getPartnerName());
        return contractRepository.save(contract);
    }

    @Transactional
    public void acceptContract(Long id) {
        Contract contract = find(id);
        contract.accept();
    }

    @Transactional
    public void rejectContract(Long id) {
        Contract contract = find(id);
        contract.reject();
    }


    @Transactional
    public void rejectAttachment(Long attachmentId) {
        Contract contract = contractRepository.findByAttachmentId(attachmentId);
        contract.rejectAttachment(attachmentId);
    }

    @Transactional
    public void acceptAttachment(Long attachmentId) {
        Contract contract = contractRepository.findByAttachmentId(attachmentId);
        contract.acceptAttachment(attachmentId);
    }

    @Transactional
    public Contract find(Long id) {
        Contract contract = contractRepository.getOne(id);
        if (contract == null) {
            throw new IllegalStateException("Contract does not exist");
        }
        return contract;
    }

    @Transactional
    public ContractDTO findDto(Long id) {
        return new ContractDTO(find(id), contractAttachmentRepository.findByContractId(id));
    }

    @Transactional
    public ContractAttachmentDTO proposeAttachment(Long contractId, ContractAttachmentDTO contractAttachmentDTO) {
        Contract contract = find(contractId);
        ContractAttachment contractAttachment = contract.proposeAttachment(contractAttachmentDTO.getData());
        return new ContractAttachmentDTO(contractAttachmentRepository.save(contractAttachment));
    }

    @Transactional
    public void removeAttachment(Long contractId, Long attachmentId) {
        //TODO sprawdzenie czy nalezy do kontraktu (JIRA: II-14455)
        contractAttachmentRepository.deleteById(attachmentId);
    }
}
