package io.legacyfighter.cabs.agreements;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private ContractAttachmentDataRepository contractAttachmentDataRepository;

    @Transactional
    public ContractDTO createContract(ContractDTO contractDTO) {
        int partnerContractsCount = contractRepository.findByPartnerName(contractDTO.getPartnerName()).size() + 1;
        Contract contract = new Contract(contractDTO.getPartnerName(), contractDTO.getSubject(), "C/" + partnerContractsCount + "/" + contractDTO.getPartnerName());
        return findDto(contractRepository.save(contract).getId());
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
        UUID contractAttachmentNo = contractRepository.findContractAttachmentNoById(attachmentId);
        contract.rejectAttachment(contractAttachmentNo);
    }

    @Transactional
    public void acceptAttachment(Long attachmentId) {
        Contract contract = contractRepository.findByAttachmentId(attachmentId);
        UUID contractAttachmentNo = contractRepository.findContractAttachmentNoById(attachmentId);
        contract.acceptAttachment(contractAttachmentNo);
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
        Contract contract = find(id);
        return new ContractDTO(contract, contractAttachmentDataRepository.findByContractAttachmentNoIn(contract.getAttachmentIds()));
    }

    @Transactional
    public ContractAttachmentDTO proposeAttachment(Long contractId, ContractAttachmentDTO contractAttachmentDTO) {
        Contract contract = find(contractId);
        UUID contractAttachmentId = contract.proposeAttachment().getContractAttachmentNo();
        ContractAttachmentData contractAttachmentData = new ContractAttachmentData(contractAttachmentId, contractAttachmentDTO.getData());
        contract = contractRepository.save(contract);
        return new ContractAttachmentDTO(contract.findAttachment(contractAttachmentId), contractAttachmentDataRepository.save(contractAttachmentData));
    }

    @Transactional
    public void removeAttachment(Long contractId, Long attachmentId) {
        //TODO sprawdzenie czy nalezy do kontraktu (JIRA: II-14455)
        Contract contract = find(contractId);
        UUID contractAttachmentNo = contractRepository.findContractAttachmentNoById(attachmentId);
        contract.remove(contractAttachmentNo);
        contractAttachmentDataRepository.deleteByAttachmentId(attachmentId);
    }
}
