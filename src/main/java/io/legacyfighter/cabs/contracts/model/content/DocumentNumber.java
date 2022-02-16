package io.legacyfighter.cabs.contracts.model.content;

import javax.persistence.Embeddable;

@Embeddable
public class DocumentNumber {
    private String number;

    protected DocumentNumber(){

    }

    public DocumentNumber(String number){
        this.number = number;
    }
}
