package io.legacyfighter.cabs.contracts.model.state.dynamic;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;

public interface StateConfig {
    State begin(DocumentHeader documentHeader);

    State recreate(DocumentHeader documentHeader);
}
