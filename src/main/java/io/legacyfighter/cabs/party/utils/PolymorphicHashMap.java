package io.legacyfighter.cabs.party.utils;

import java.util.HashMap;
import java.util.Optional;

public class PolymorphicHashMap<K extends Class<?>,V> extends HashMap<K,V> {

    @Override
    public boolean containsKey(Object key) {
        return findEntry((K)key).isPresent();
    }

    @Override
    public V get(Object key) {
        Optional<Entry<K,V>> entry = findEntry((K)key);
        return entry.map(Entry::getValue).orElse(null);
    }

    private Optional<Entry<K,V>> findEntry(K key) {
        return entrySet().stream()
                .filter(e -> key.isAssignableFrom(e.getKey()))
                .findFirst();
    }
}
