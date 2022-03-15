package io.legacyfighter.cabs.integration;

import io.legacyfighter.cabs.agreements.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContractLifecycleIntegrationTest {

    @Autowired
    ContractService contractService;

    @Test
    void canCreateContract() {
        //given
        ContractDTO created = createContract("partnerNameVeryUnique", "umowa o cenę");

        //when
        ContractDTO loaded = loadContract(created.getId());

        //then
        assertEquals("partnerNameVeryUnique", loaded.getPartnerName());
        assertEquals("umowa o cenę", loaded.getSubject());
        assertEquals("C/1/partnerNameVeryUnique", loaded.getContractNo());
        assertEquals(ContractStatus.NEGOTIATIONS_IN_PROGRESS, loaded.getStatus());
        assertNotNull(loaded.getId());
        assertNotNull(loaded.getCreationDate());
        assertNotNull(loaded.getCreationDate());
        assertNull(loaded.getChangeDate());
        assertNull(loaded.getAcceptedAt());
        assertNull(loaded.getRejectedAt());
    }

    @Test
    void secondContractForTheSamePartnerHasCorrectNo() {
        //given
        ContractDTO first = createContract("uniqueName", "umowa o cenę");

        //when
        ContractDTO second = createContract("uniqueName", "umowa o cenę");
        //then
        ContractDTO firstLoaded = loadContract(first.getId());
        ContractDTO secondLoaded = loadContract(second.getId());

        assertEquals("uniqueName", firstLoaded.getPartnerName());
        assertEquals("uniqueName", secondLoaded.getPartnerName());
        assertEquals("C/1/uniqueName", firstLoaded.getContractNo());
        assertEquals("C/2/uniqueName", secondLoaded.getContractNo());
    }

    @Test
    void canAddAttachmentToContract() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //when
        addAttachmentToContract(created, "content");

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(1, loaded.getAttachments().size());
        assertTrue(Arrays.equals("content".getBytes(), loaded.getAttachments().get(0).getData()));
        assertEquals(ContractAttachmentStatus.PROPOSED, loaded.getAttachments().get(0).getStatus());
    }

    @Test
    void canRemoveAttachmentFromContract() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");

        //when
        removeAttachmentFromContract(created, attachment);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(0, loaded.getAttachments().size());
    }

    @Test
    void canAcceptAttachmentByOneSide() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");

        //when
        acceptAttachment(attachment);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(1, loaded.getAttachments().size());
        assertEquals(ContractAttachmentStatus.ACCEPTED_BY_ONE_SIDE, loaded.getAttachments().get(0).getStatus());
    }

    @Test
    void canAcceptAttachmentByTwoSides() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");

        //when
        acceptAttachment(attachment);
        //and
        acceptAttachment(attachment);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(1, loaded.getAttachments().size());
        assertEquals(ContractAttachmentStatus.ACCEPTED_BY_BOTH_SIDES, loaded.getAttachments().get(0).getStatus());
    }

    @Test
    void canRejectAttachment() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");

        //when
        rejectAttachment(attachment);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(1, loaded.getAttachments().size());
        assertEquals(ContractAttachmentStatus.REJECTED, loaded.getAttachments().get(0).getStatus());
    }

    @Test
    void canAcceptContractWhenAllAttachmentsAccepted() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");
        //and
        acceptAttachment(attachment);
        acceptAttachment(attachment);

        //when
        acceptContract(created);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(ContractStatus.ACCEPTED, loaded.getStatus());
    }

    @Test
    void canRejectContract() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");
        //and
        acceptAttachment(attachment);
        acceptAttachment(attachment);

        //when
        rejectContract(created);

        //then
        ContractDTO loaded = loadContract(created.getId());
        assertEquals(ContractStatus.REJECTED, loaded.getStatus());
    }

    @Test
    void cannotAcceptContractWhenNotAllAttachmentsAccepted() {
        //given
        ContractDTO created = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachmentDTO attachment = addAttachmentToContract(created, "content");
        //and
        acceptAttachment(attachment);

        //expect
        assertThrows(IllegalStateException.class, () -> acceptContract(created));
        ContractDTO loaded = loadContract(created.getId());
        assertNotEquals(ContractStatus.ACCEPTED, loaded.getStatus());
    }

    ContractDTO loadContract(Long id) {
        return contractService.findDto(id);
    }

    ContractDTO createContract(String partnerName, String subject) {
        ContractDTO dto = new ContractDTO();
        dto.setPartnerName(partnerName);
        dto.setSubject(subject);
        return contractService.createContract(dto);
    }

    ContractAttachmentDTO addAttachmentToContract(ContractDTO created, String content) {
        ContractAttachmentDTO contractAttachmentDTO = new ContractAttachmentDTO();
        contractAttachmentDTO.setData(content.getBytes());
        return contractService.proposeAttachment(created.getId(), contractAttachmentDTO);
    }

    void removeAttachmentFromContract(ContractDTO contract, ContractAttachmentDTO attachment) {
        contractService.removeAttachment(contract.getId(), attachment.getId());
    }

    void acceptAttachment(ContractAttachmentDTO attachment) {
        contractService.acceptAttachment(attachment.getId());
    }

    void rejectAttachment(ContractAttachmentDTO attachment) {
        contractService.rejectAttachment(attachment.getId());
    }

    void acceptContract(ContractDTO contract) {
        contractService.acceptContract(contract.getId());
    }

    void rejectContract(ContractDTO contract) {
        contractService.rejectContract(contract.getId());
    }

}