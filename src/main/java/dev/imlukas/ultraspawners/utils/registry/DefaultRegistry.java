package dev.imlukas.ultraspawners.utils.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRegistry<T, V> {

    protected final Map<T, V> map = new HashMap<>();

    public V get(T t) {
        return map.get(t);
    }

    public Map<T, V> getMap() {
        return map;
    }

    public List<V> getValues() {
        return new ArrayList<>(getMap().values());
    }

    public V register(T t, V v) {
        map.put(t, v);
        return v;
    }

    public void unregister(T t) {
        map.remove(t);
    }

    public void clear() {
        map.clear();
    }
}
