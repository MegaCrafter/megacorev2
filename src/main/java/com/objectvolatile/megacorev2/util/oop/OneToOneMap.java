package com.objectvolatile.megacorev2.util.oop;

import java.util.HashMap;

public class OneToOneMap<K, V> {

    private final HashMap<K, V> mapRight = new HashMap<>();
    private final HashMap<V, K> mapLeft = new HashMap<>();

    public void put(K key, V val) {
        if (mapRight.containsKey(key)) return;
        if (mapLeft.containsKey(val)) return;

        mapRight.put(key, val);
        mapLeft.put(val, key);
    }

    public V getRight(K key) {
        return mapRight.get(key);
    }

    public V getRightOrDefault(K key, V def) {
        return mapRight.getOrDefault(key, def);
    }

    public K getLeft(V key) {
        return mapLeft.get(key);
    }

    public K getLeftOrDefault(V key, K def) {
        return mapLeft.getOrDefault(key, def);
    }
}