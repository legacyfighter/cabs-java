package io.legacyfighter.cabs.agreements;

import org.junit.jupiter.api.Test;

import static io.legacyfighter.cabs.agreements.ContractAttachmentStatus.*;
import static org.junit.jupiter.api.Assertions.*;

class ContractLifecycleTest {

    @Test
    void canCreateContract() {
        //when
        Contract contract = createContract("partnerNameVeryUnique", "umowa o cenę");

        //then
        assertEquals("partnerNameVeryUnique", contract.getPartnerName());
        assertEquals("umowa o cenę", contract.getSubject());
        assertEquals(ContractStatus.NEGOTIATIONS_IN_PROGRESS, contract.getStatus());
        assertNotNull(contract.getCreationDate());
        assertNotNull(contract.getCreationDate());
        assertNull(contract.getChangeDate());
        assertNull(contract.getAcceptedAt());
        assertNull(contract.getRejectedAt());
    }

    @Test
    void canAddAttachmentToContract() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");

        //when
        ContractAttachment contractAttachment = contract.proposeAttachment();

        //then
        assertEquals(1, contract.getAttachmentIds().size());
        assertEquals(PROPOSED, contract.findAttachment(contractAttachment.getContractAttachmentNo()).getStatus());
    }

    @Test
    void canRemoveAttachmentFromContract() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();

        //when
        contract.remove(attachment.getContractAttachmentNo());

        //then
        assertEquals(0, contract.getAttachmentIds().size());
    }

    @Test
    void canAcceptAttachmentByOneSide() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();

        //when
        contract.acceptAttachment(attachment.getContractAttachmentNo());

        //then
        assertEquals(1, contract.getAttachmentIds().size());
        assertEquals(ACCEPTED_BY_ONE_SIDE, contract.findAttachment(attachment.getContractAttachmentNo()).getStatus());
    }

    @Test
    void canAcceptAttachmentByTwoSides() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();

        //when
        contract.acceptAttachment(attachment.getContractAttachmentNo());
        //and
        contract.acceptAttachment(attachment.getContractAttachmentNo());

        //then
        assertEquals(1, contract.getAttachmentIds().size());
        assertEquals(ACCEPTED_BY_BOTH_SIDES, contract.findAttachment(attachment.getContractAttachmentNo()).getStatus());
    }

    @Test
    void canRejectAttachment() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();

        //when
        contract.rejectAttachment(attachment.getContractAttachmentNo());

        //then
        assertEquals(1, contract.getAttachmentIds().size());
        assertEquals(REJECTED, contract.findAttachment(attachment.getContractAttachmentNo()).getStatus());
    }

    @Test
    void canAcceptContractWhenAllAttachmentsAccepted() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();
        //and
        contract.acceptAttachment(attachment.getContractAttachmentNo());
        contract.acceptAttachment(attachment.getContractAttachmentNo());

        //when
        contract.accept();

        //then
        assertEquals(ContractStatus.ACCEPTED, contract.getStatus());
    }

    @Test
    void canRejectContract() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();
        //and
        contract.acceptAttachment(attachment.getContractAttachmentNo());
        contract.acceptAttachment(attachment.getContractAttachmentNo());

        //when
        contract.reject();

        //then
        assertEquals(ContractStatus.REJECTED, contract.getStatus());
    }

    @Test
    void cannotAcceptContractWhenNotAllAttachmentsAccepted() {
        //given
        Contract contract = createContract("partnerName", "umowa o cenę");
        //and
        ContractAttachment attachment = contract.proposeAttachment();
        //and
        contract.acceptAttachment(attachment.getContractAttachmentNo());

        //expect
        assertThrows(IllegalStateException.class, contract::accept);
        assertNotEquals(ContractStatus.ACCEPTED, contract.getStatus());
    }


    Contract createContract(String partnerName, String subject) {
        return new Contract(partnerName, subject, "no");
    }

}