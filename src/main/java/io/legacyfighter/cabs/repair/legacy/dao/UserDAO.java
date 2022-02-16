package io.legacyfighter.cabs.repair.legacy.dao;

import io.legacyfighter.cabs.repair.legacy.parts.Parts;
import io.legacyfighter.cabs.repair.legacy.user.CommonBaseAbstractUser;
import io.legacyfighter.cabs.repair.legacy.user.EmployeeDriverWithOwnCar;
import io.legacyfighter.cabs.repair.legacy.user.SignedContract;
import org.springframework.stereotype.Repository;

import java.util.Set;

/*
  Fake impl that fakes graph query and determining CommonBaseAbstractUser type
 */
@Repository
public class UserDAO {
    public CommonBaseAbstractUser getOne(Long userId) {
        SignedContract contract = new SignedContract();
        contract.setCoveredParts(Set.of(Parts.values()));
        contract.setCoverageRatio(100.0);

        EmployeeDriverWithOwnCar user = new EmployeeDriverWithOwnCar();
        user.setContract(contract);
        return user;
    }
}
