package io.legacyfighter.cabs.contracts.model.state.dynamic;

import java.util.HashMap;
import java.util.Map;

public class ChangeCommand {
    private String desiredState;
    private Map<String, Object> params;

    public ChangeCommand(String desiredState, Map<String, Object> params){
        this.desiredState = desiredState;
        this.params = params;
    }

    public ChangeCommand(String desiredState){
        this(desiredState, new HashMap<>());
    }

    public ChangeCommand withParam(String name, Object value){
        params.put(name, value);
        return this;
    }

    public String getDesiredState() {
        return desiredState;
    }

    public <T> T getParam(String name, Class<T> type){
        return (T) params.get(name);
    }

    @Override
    public String toString() {
        return "ChangeCommand{" +
                "desiredState='" + desiredState + '\'' +
                '}';
    }
}
