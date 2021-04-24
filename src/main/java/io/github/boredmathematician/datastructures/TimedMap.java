package io.github.boredmathematician.datastructures;

import java.util.*;

/**
 * An Implementation a Map with the notion of Time.
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @param <T> the type of time tracked by this map.
 *            T MUST implement {@link Comparable} to have a notion of comparing times.
 * @author Hari Krishnan
 * @see Map
 * @see NavigableMap
 */
public class TimedMap<K, V, T extends Comparable<T>> {

    private final Map<K, NavigableMap<T, V>> map = new HashMap<>();

    /**
     * Associates the specified value with the specified key at the specified time in this map.
     * If the map previously contained a mapping for the key at this time,
     * the old value is replaced by the specified value.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param time  time at which said association should occur
     */
    public void set(K key, V value, T time) {
        map.computeIfAbsent(key, k -> new TreeMap<>()).put(time, value);
    }

    /**
     * Returns the value to which the specified key is mapped at the specified time or earlier,
     * or {@code null} if this map contains no mapping for the key at the specified time or earlier.
     *
     * @param key  the key whose associated value is to be returned
     * @param time the time(or earlier) at which the values associated with the key is to be returned.
     * @return The value to which the specified key is mapped at the specified time or earlier, or
     * {@code null} if this map contains no mapping for the key at the specified time or earlier.
     */
    public V get(K key, T time) {
        Map.Entry<T, V> entry = map.getOrDefault(key, Collections.emptyNavigableMap()).floorEntry(time);
        return entry == null ? null : entry.getValue();
    }

}
