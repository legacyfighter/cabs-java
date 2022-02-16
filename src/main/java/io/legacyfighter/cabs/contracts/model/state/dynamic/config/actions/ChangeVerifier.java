package io.legacyfighter.cabs.contracts.model.state.dynamic.config.actions;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.dynamic.ChangeCommand;

import java.util.function.BiFunction;

public class ChangeVerifier implements BiFunction<DocumentHeader, ChangeCommand, Void> {

    public static final String PARAM_VERIFIER = "verifier";

    @Override
    public Void apply(DocumentHeader documentHeader, ChangeCommand command) {
        documentHeader.setVerifierId(command.getParam(PARAM_VERIFIER, Long.class));
        return null;
    }
}
