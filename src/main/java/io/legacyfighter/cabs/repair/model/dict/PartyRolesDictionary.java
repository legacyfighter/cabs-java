package io.legacyfighter.cabs.repair.model.dict;

import io.legacyfighter.cabs.party.model.role.PartyBasedRole;
import io.legacyfighter.cabs.repair.model.roles.empty.Customer;
import io.legacyfighter.cabs.repair.model.roles.empty.Insured;
import io.legacyfighter.cabs.repair.model.roles.repair.ExtendedInsurance;
import io.legacyfighter.cabs.repair.model.roles.repair.Warranty;

/*
 Enum that emulates database dictionary
 */
public enum PartyRolesDictionary {
    INSURER(ExtendedInsurance.class), INSURED(Insured.class), GUARANTOR(Warranty.class), CUSTOMER(Customer.class);

    private final String name;

    PartyRolesDictionary(Class<? extends PartyBasedRole> clazz){
        name = clazz.getName();
    }

    public String getRoleName(){
        return name;
    }
}
