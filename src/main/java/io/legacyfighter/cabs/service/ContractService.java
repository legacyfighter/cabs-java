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
        Contract contract = new Contract();
        contract.setPartnerName(contractDTO.getPartnerName());
        int partnerContractsCount = contractRepository.findByPartnerName(contractDTO.getPartnerName()).size() + 1;
        contract.setSubject(contractDTO.getSubject());
        contract.setContractNo("C/" + partnerContractsCount + "/" + contractDTO.getPartnerName());
        return contractRepository.save(contract);
    }

    @Transactional
    public void acceptContract(Long id) {
        Contract contract = find(id);
        List<ContractAttachment> attachments = contractAttachmentRepository.findByContract(contract);
        if(attachments.stream().allMatch(a -> a.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES))) {
            contract.setStatus(Contract.Status.ACCEPTED);
        } else {
            throw new IllegalStateException("Not all attachments accepted by both sides");
        }
    }

    @Transactional
    public void rejectContract(Long id) {
        Contract contract = find(id);
        contract.setStatus(Contract.Status.REJECTED);
    }


    @Transactional
    public void rejectAttachment(Long attachmentId) {
        ContractAttachment contractAttachment = contractAttachmentRepository.getOne(attachmentId);
        contractAttachment.setStatus(ContractAttachment.Status.REJECTED);
    }

    @Transactional
    public void acceptAttachment(Long attachmentId) {
        ContractAttachment contractAttachment = contractAttachmentRepository.getOne(attachmentId);
        if (contractAttachment.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE) || contractAttachment.getStatus().equals(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES)) {
            contractAttachment.setStatus(ContractAttachment.Status.ACCEPTED_BY_BOTH_SIDES);
        } else {
            contractAttachment.setStatus(ContractAttachment.Status.ACCEPTED_BY_ONE_SIDE);
        }
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
        return new ContractDTO(find(id));
    }

    @Transactional
    public ContractAttachmentDTO proposeAttachment(Long contractId, ContractAttachmentDTO contractAttachmentDTO) {
        Contract contract = find(contractId);
        ContractAttachment contractAttachment = new ContractAttachment();
        contractAttachment.setContract(contract);
        contractAttachment.setData(contractAttachmentDTO.getData());
        contractAttachmentRepository.save(contractAttachment);
        contract.getAttachments().add(contractAttachment);
        return new ContractAttachmentDTO(contractAttachment);
    }

    @Transactional
    public void removeAttachment(Long contractId, Long attachmentId) {
        //TODO sprawdzenie czy nalezy do kontraktu (JIRA: II-14455)
        contractAttachmentRepository.deleteById(attachmentId);
    }
}
