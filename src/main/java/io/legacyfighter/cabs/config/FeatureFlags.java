package io.legacyfighter.cabs.config;


import org.togglz.core.Feature;
import org.togglz.core.annotation.Label;
import org.togglz.core.context.FeatureContext;

public enum FeatureFlags implements Feature {

    @Label("Driver report created using sql query")
    DRIVER_REPORT_SQL,

    @Label("Should compare reports")
    DRIVER_REPORT_CREATION_RECONCILIATION;

    public boolean isActive() {
        return FeatureContext.getFeatureManager().isActive(this);
    }

}


