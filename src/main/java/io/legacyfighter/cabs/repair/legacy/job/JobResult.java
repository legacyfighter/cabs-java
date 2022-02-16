package io.legacyfighter.cabs.repair.legacy.job;

import java.util.HashMap;
import java.util.Map;

public class JobResult {
    public enum Decision{REDIRECTION, ACCEPTED, ERROR;}

    private Decision decision;

    private Map<String, Object> params = new HashMap<>();

    public JobResult(Decision decision) {
        this.decision = decision;
    }

    public JobResult addParam(String name, Object value){
        params.put(name, value);
        return this;
    }

    public Object getParam(String name) {
        return params.get(name);
    }

    public Decision getDecision() {
        return decision;
    }
}
