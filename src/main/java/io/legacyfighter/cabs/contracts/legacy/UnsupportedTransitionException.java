package io.legacyfighter.cabs.contracts.legacy;

public class UnsupportedTransitionException extends RuntimeException{
    private DocumentStatus current;
    private DocumentStatus desired;

    public UnsupportedTransitionException(DocumentStatus current, DocumentStatus desired) {
        super("can not transit form " + current + " to " + desired);
        this.current = current;
        this.desired = desired;
    }

    public DocumentStatus getCurrent() {
        return current;
    }

    public DocumentStatus getDesired() {
        return desired;
    }
}
