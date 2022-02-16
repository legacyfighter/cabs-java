package io.legacyfighter.cabs.contracts.legacy;

public interface Versionable {
    void recreateTo(long version);
    long getLastVersion();
}
