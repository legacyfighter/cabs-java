package io.legacyfighter.cabs.contracts.application.acme.straigthforward;

import io.legacyfighter.cabs.contracts.model.DocumentHeader;
import io.legacyfighter.cabs.contracts.model.state.straightforward.BaseState;
import io.legacyfighter.cabs.contracts.model.state.straightforward.acme.DraftState;
import org.springframework.stereotype.Component;

@Component
class AcmeStateFactory {
    public BaseState create(DocumentHeader header){
        //sample impl is based on class names
        //other possibilities: names Dependency Injection Containers, states persisted via ORM Discriminator mechanism, mapper
        String className = header.getStateDescriptor();

        if (className == null) {
            DraftState state = new DraftState();
            state.init(header);
            return state;
        }

        try {
            Class<? extends  BaseState> clazz = (Class<? extends BaseState>) Class.forName(className);
            BaseState state = clazz.getConstructor().newInstance();
            state.init(header);
            return state;
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
