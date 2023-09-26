package dev.imlukas.ultraspawners.utils.registry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Factory<T, V> {

    protected final Map<T, Supplier<V>> map = new HashMap<>();

    public V supply(T t) {
        return map.get(t).get();
    }

    public Map<T, Supplier<V>> getMap() {
        return map;
    }

    public void register(T t, Supplier<V> v) {
        map.put(t, v);
    }

    public void unregister(T t) {
        map.remove(t);
    }

    public void clear() {
        map.clear();
    }

}
