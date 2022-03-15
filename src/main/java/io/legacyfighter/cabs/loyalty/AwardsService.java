package io.legacyfighter.cabs.loyalty;

public interface AwardsService {

    AwardsAccountDTO findBy(Long clientId);

    void registerToProgram(Long clientId);

    void activateAccount(Long clientId);

    void deactivateAccount(Long clientId);

    AwardedMiles registerMiles(Long clientId, Long transitId);

    AwardedMiles registerNonExpiringMiles(Long clientId, Integer miles);

    void removeMiles(Long clientId, Integer miles);

    Integer calculateBalance(Long clientId);

    void transferMiles(Long fromClientId, Long toClientId, Integer miles);
}