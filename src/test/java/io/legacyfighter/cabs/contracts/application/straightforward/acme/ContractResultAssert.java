package io.legacyfighter.cabs.contracts.application.straightforward.acme;

import io.legacyfighter.cabs.contracts.application.acme.straigthforward.ContractResult;
import io.legacyfighter.cabs.contracts.model.state.straightforward.BaseState;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ContractResultAssert {
    private ContractResult result;

    public ContractResultAssert(ContractResult result){
        this.result = result;
        assertEquals(ContractResult.Result.SUCCESS, result.getResult());
    }

    public ContractResultAssert state(BaseState state){
        assertEquals(state.getStateDescriptor(), result.getStateDescriptor());
        return this;
    }
}
