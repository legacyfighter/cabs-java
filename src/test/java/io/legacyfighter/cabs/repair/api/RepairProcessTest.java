package io.legacyfighter.cabs.repair.api;

import io.legacyfighter.cabs.party.api.PartyId;
import io.legacyfighter.cabs.repair.legacy.parts.Parts;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

@SpringBootTest
public class RepairProcessTest {

    @Autowired
    private RepairProcess vehicleRepairProcess;

    @Autowired
    private ContractManager contractManager;

    private final PartyId vehicle = new PartyId();
    private final PartyId handlingParty = new PartyId();

    @Test
    public void warrantyByInsuranceCoversAllButPaint(){
        //given
        contractManager.extendedWarrantyContractSigned(handlingParty, vehicle);

        Set<Parts> parts = Set.of(new Parts[] {Parts.ENGINE, Parts.GEARBOX, Parts.PAINT, Parts.SUSPENSION});
        RepairRequest repairRequest = new RepairRequest(vehicle, parts);
        //when
        ResolveResult result = vehicleRepairProcess.resolve(repairRequest);
        //then
        new VehicleRepairAssert(result).by(handlingParty).free().allPartsBut(parts, new Parts[] {Parts.PAINT});
    }

    @Test
    public void manufacturerWarrantyCoversAll(){
        //given
        contractManager.manufacturerWarrantyRegistered(handlingParty, vehicle);

        Set<Parts> parts = Set.of(new Parts[]{Parts.ENGINE, Parts.GEARBOX, Parts.PAINT, Parts.SUSPENSION});
        RepairRequest repairRequest = new RepairRequest(vehicle, parts);
        //when
        ResolveResult result = vehicleRepairProcess.resolve(repairRequest);
        //then
        new VehicleRepairAssert(result).by(handlingParty).free().allParts(parts);
    }
}
