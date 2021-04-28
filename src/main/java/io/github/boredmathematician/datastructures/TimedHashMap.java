package io.github.boredmathematician.datastructures;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;

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
public class TimedHashMap<K, T extends Comparable<T>, V> implements TimedMap<K, T, V> {

    private final Map<K, NavigableMap<T, V>> map;
    private final Comparator<T> cmp;

    private Set<Key<K, T>> keySet;
    private Collection<V> values;
    private Set<Entry<K, T, V>> entrySet;
    private int size;


    public TimedHashMap(Comparator<T> cmp) {
        size = 0;
        map = new HashMap<>();
        this.cmp = cmp;
    }

    public TimedHashMap() {
        size = 0;
        map = new HashMap<>();
        cmp = null;
    }

    /**
     * Returns the comparator used to determine the order of time in this map, or
     * {@code null} if this map uses the {@linkplain Comparable
     * natural ordering} of its times.
     *
     * @return the comparator used to order the times in this map,
     * or {@code null} if this map uses the natural ordering
     * of its times
     */
    @Override
    public Comparator<T> comparator() {
        return cmp;
    }

    /**
     * Returns the number of key-time-value mappings in this map.
     * This includes all the mappings for all keys, at all point in time.
     * <p>
     * If the map contains more than Integer.MAX_VALUE elements, returns Integer.MAX_VALUE.
     *
     * @return the number of key-time-value mappings in this map.
     */
    @Override
    public int size() {
        return size;
    }

    /**
     * Returns {@code true} if this map contains no key-time-value mappings.
     *
     * @return {@code true} if this map contains no key-time-value mappings
     */
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns {@code true} if this map contains a mapping for the specified
     * key, at any point in time. Note that when this returns {@code false},
     * {@link #get(K, T) get(key, time)} will <b>ALWAYS</b> return {@code false},
     * no matter the value for {@code time}.
     *
     * @param key key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified
     * key, at any point in time
     */
    @Override
    public boolean containsKey(K key) {
        return map.containsKey(key);
    }

    /**
     * Returns {@code true} if a mapping for the specified key at the specified time is
     * "present". Note that even if this returns {@code false}, {@link #get(K, T)
     * get(key, time)} may still return a value. This happens when the key was associated
     * with a value at an earlier point in time.
     *
     * @param key  key whose presence in this map is to be tested
     * @param time the point in time when this presence is to be tested
     * @return {@code true} if a mapping for the specified key at the specified time
     * is "present"
     * @see #get(K, T) get(key, time)
     */
    @Override
    public boolean containsKey(K key, T time) {
        return map.getOrDefault(key, Collections.emptyNavigableMap()).containsKey(time);
    }

    /**
     * Returns {@code true} if a mapping for the specified key is "present" at some point in time
     * that is earlier than the specified point in time.
     *
     * @param key  key whose presence in this map is to be tested
     * @param time the point in time before which this presence is to be tested
     * @return {@code true} if a mapping for the specified key is "present" at some point in time
     * that is earlier than the specified point in time
     */
    @Override
    public boolean containsLowerKey(K key, T time) {
        return map.getOrDefault(key, Collections.emptyNavigableMap()).lowerKey(time) != null;
    }

    /**
     * Returns {@code true} if a mapping for the specified key is "present" at some point in time
     * that is earlier than or equal to the specified point in time.
     *
     * @param key  key whose presence in this map is to be tested
     * @param time the point in time before which this presence is to be tested
     * @return {@code true} if a mapping for the specified key is "present" at some point in time
     * that is earlier than or equal to the specified point in time
     */
    @Override
    public boolean containsFloorKey(K key, T time) {
        return map.getOrDefault(key, Collections.emptyNavigableMap()).floorKey(time) != null;
    }

    /**
     * Returns {@code true} if one or more mappings to the specified value is present.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if one or more mappings to the specified value is present.
     */
    @Override
    public boolean containsValue(V value) {
        return values().stream().anyMatch(value::equals);
    }

    /**
     * Returns a {@link SortedMap} which maps all known values mapped to this key at various points in time
     *
     * @param key the key whose mapping is to returned
     * @return A mapping between all known points in time for this key, and the value associated
     * with the ket at that time
     */
    @Override
    public NavigableMap<T, V> get(K key) {
        return map.get(key);
    }

    /**
     * Returns the value to which the specified key is associated with at the specified point in time.
     * If no such mapping exists, the call returns {@code null}.
     *
     * <p>If this map permits null values, then a return value of
     * {@code null} does not <i>necessarily</i> indicate that the map
     * contains no mapping for the key at the specified time; it's also possible
     * that the map explicitly maps the key to {@code null} at this point in time.
     * The {@link #containsKey(K, T) containsKey} operation may be used to distinguish these two cases.
     *
     * <p>If the function call returns a value, then it does not <i>necessarily</i> indicate
     * that the mapping between the specified key and the specified value is present
     * <b>at the specified time</b>; it's also possible that the map contained a mapping for
     * this key at a earlier time. Just like in the case of {@code null} values,
     * the {@link #containsKey(K, T) containsKey} operation may be used to distinguish these two cases.
     *
     * @param key  the key whose associated value is to be returned
     * @param time the point in time for the association
     * @return the value to which the specified key is mapped at the specified
     * point in time, or earlier, or {@code null} if this no such mapping exists.
     */
    @Override
    public V get(K key, T time) {
        Map.Entry<T, V> entry = map.getOrDefault(key, Collections.emptyNavigableMap()).floorEntry(time);
        return entry == null ? null : entry.getValue();
    }

    /**
     * Associates the specified value with the specified key, at the specified time in this map.
     * If the map previously contained a mapping for the key at the specified time,
     * the old value is replaced by the specified value.
     *
     * @param key   key with which the specified value is to be associated
     * @param time  the point in time when this association is to be made
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key} at the specified point in time.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key} at the specified point in time,
     * if the implementation supports {@code null} values.)
     */
    @Override
    public V put(K key, T time, V value) {
        V out = map.computeIfAbsent(key, k -> new TreeMap<>(cmp)).put(time, value);
        if (size != Integer.MAX_VALUE)
            size++;
        return out;
    }

    /**
     * Removes all mappings at all points in time for a key from this map if it is present.
     *
     * <p>Returns a mapping of associations at all known times if the map previously
     * contained or {@code null} if the map contained no mapping for the key.
     *
     * <p>The map will not contain any mapping for the specified key, at any point in time,
     * once the call returns. That is to say, {@link #containsKey(K) containsKey(key)} will return
     * {@code false}
     *
     * @param key key whose mapping is to be removed from the map
     * @return a mapping of associations at all known times if the map previously
     * contained or {@code null} if the map contained no mapping for the key.
     */
    @Override
    public NavigableMap<T, V> remove(K key) {
        NavigableMap<T, V> removed = map.remove(key);
        size -= removed.size();
        return removed;
    }

    /**
     * Removes the mapping for the key at the specified point in time from this map, if it is present.
     *
     * <p>Returns the {@code true} if the map previously contained at least one association with the key,
     * or {@code null} if the map contained no mapping for the key.
     *
     * <p>The map will not contain any mapping for the specified key, at any point in time,
     * once the call returns. However, {@link #get(K, T) get(key, time)} may still return a value.
     * This happens when the key was associated with a value at an earlier point in time.
     *
     * @param key  key whose mapping is to be removed from the map
     * @param time the point in time where the association is to be removed
     * @return the previous value associated with the key, at the specified time.
     * or {@code null} if the map contained no such mapping.
     * @see #get(K, T) get(key, time)
     */
    @Override
    public V remove(K key, T time) {
        V removed = map.getOrDefault(key, Collections.emptyNavigableMap()).remove(time);
        if (removed != null)
            size--;
        return removed;
    }

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    @Override
    public void clear() {
        size = 0;
        map.clear();
    }

    /**
     * Returns a {@link Set} view of the {@link Key}s contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterators own {@code remove} operation), the results of
     * the iteration are undefined.  The set supports element removal,
     * which removes the corresponding mapping from the map, via the
     * {@code Iterator.remove}, {@code Set.remove},
     * {@code removeAll}, {@code retainAll}, and {@code clear}
     * operations.  It does not support the {@code add} or {@code addAll}
     * operations.
     *
     * @return a set view of the {@link Key}s contained in this map
     */
    public Set<Key<K, T>> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }

    /**
     * Returns a {@link Collection} view of the values contained in this map.
     * The collection is backed by the map, so changes to the map are
     * reflected in the collection, and vice-versa.  If the map is
     * modified while an iteration over the collection is in progress
     * (except through the iterators own {@code remove} operation),
     * the results of the iteration are undefined.  The collection
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Collection.remove}, {@code removeAll},
     * {@code retainAll} and {@code clear} operations.  It does not
     * support the {@code add} or {@code addAll} operations.
     *
     * @return a collection view of the values contained in this map
     */
    public Collection<V> values() {
        if (values == null) {
            values = new Values();
        }
        return values;
    }

    /**
     * Returns a {@link Set} view of the mappings contained in this map.
     * The set is backed by the map, so changes to the map are
     * reflected in the set, and vice-versa.  If the map is modified
     * while an iteration over the set is in progress (except through
     * the iterators own {@code remove} operation, or through the
     * {@code setValue} operation on a map entry returned by the
     * iterator) the results of the iteration are undefined.  The set
     * supports element removal, which removes the corresponding
     * mapping from the map, via the {@code Iterator.remove},
     * {@code Set.remove}, {@code removeAll}, {@code retainAll} and
     * {@code clear} operations.  It does not support the
     * {@code add} or {@code addAll} operations.
     *
     * @return a set view of the mappings contained in this map
     */
    @Override
    public Set<Entry<K, T, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    @Override
    public String toString() {
        return "{" +
                entrySet.stream().map(Object::toString).reduce((s1, s2) -> s1 + ", " + s2)
                + "}";
    }

    private class EntrySet extends AbstractSet<Entry<K, T, V>> {

        @Override
        public int size() {
            return size;
        }

        @Override
        public final void clear() {
            TimedHashMap.this.clear();
        }

        /**
         * Returns an iterator over the elements contained in this collection.
         *
         * @return an iterator over the elements contained in this collection
         */
        @Override
        public Iterator<Entry<K, T, V>> iterator() {
            return getEntryStream().iterator();
        }

        private Stream<Entry<K, T, V>> getEntryStream() {
            return map
                    .entrySet()
                    .stream()
                    .flatMap(entry -> {
                        K key = entry.getKey();
                        return entry
                                .getValue()
                                .entrySet()
                                .stream()
                                .map(navigableEntry -> new HashEntry(key,
                                        navigableEntry.getKey(), navigableEntry.getValue()));
                    });
        }

        public final boolean contains(Entry<K, T, V> entry) {
            K key = entry.getKey();
            T time = entry.getTime();

            return containsKey(key, time) && (Objects.equals(get(key, time), entry.getValue()));
        }

        public final boolean remove(Entry<K, T, V> entry) {
            K key = entry.getKey();
            T time = entry.getTime();
            V value = entry.getValue();
            return TimedHashMap.this.remove(key, time, value);
        }

        public final void forEach(Consumer<? super Entry<K, T, V>> action) {
            getEntryStream().forEach(action);
        }
    }

    private class KeySet extends AbstractSet<Key<K, T>> {
        @Override
        public Iterator<Key<K, T>> iterator() {
            return new Iterator<Key<K, T>>() {

                private final Iterator<Entry<K, T, V>> i = entrySet().iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public Key<K, T> next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return i.next().getKeyTime();
                }

                @Override
                public void remove() {
                    i.remove();
                }
            };
        }

        @Override
        public int size() {
            return size;
        }

        @Override
        public boolean isEmpty() {
            return TimedHashMap.this.isEmpty();
        }

        @Override
        public void clear() {
            TimedHashMap.this.clear();
        }

        public boolean contains(Key<K, T> k) {
            return containsKey(k);
        }
    }

    private class Values extends AbstractCollection<V> {
        public int size() {
            return size;
        }

        @Override
        public void clear() {
            TimedHashMap.this.clear();
        }

        public Iterator<V> iterator() {
            return new Iterator<V>() {
                private final Iterator<Entry<K, T, V>> i = entrySet().iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public V next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    return i.next().getValue();
                }

                @Override
                public void remove() {
                    i.remove();
                }
            };
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean contains(Object v) {
            return containsValue((V) v);
        }

        @Override
        public final void forEach(Consumer<? super V> action) {
            iterator().forEachRemaining(action);
        }

    }

    private class HashEntry implements Entry<K, T, V> {
        private final K key;
        private final T time;
        private V value;

        public HashEntry(K key, T time, V value) {
            this.key = key;
            this.time = time;
            this.value = value;

        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns the point in time corresponding to this entry.
         *
         * @return the point in time corresponding to this entry
         */
        @Override
        public T getTime() {
            return time;
        }

        @Override
        public Key<K, T> getKeyTime() {
            return new HashKey(key, time);
        }

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        @Override
        public V getValue() {
            return value;
        }

        /**
         * Replaces the value corresponding to this entry with the specified
         * value (optional operation).  (Writes through to the map.)
         *
         * @param value new value to be stored in this entry
         * @return old value corresponding to the entry
         */
        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @SuppressWarnings("unchecked") HashEntry hashEntry = (HashEntry) o;
            return key.equals(hashEntry.key) && time.equals(hashEntry.time) && value.equals(hashEntry.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, time, value);
        }

        @Override
        public String toString() {
            return getKeyTime() + " = " + value;
        }
    }

    private class HashKey implements Key<K, T> {

        private final K key;
        private final T time;

        private HashKey(K key, T time) {
            this.key = key;
            this.time = time;
        }


        /**
         * Returns The key component of this key-time pair
         *
         * @return The key component of this key-time pair
         */
        @Override
        public K getKey() {
            return key;
        }

        /**
         * Returns The time component of this key-time pair
         *
         * @return The time component of this key-time pair
         */
        @Override
        public T getTime() {
            return time;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            @SuppressWarnings("unchecked") HashKey hashKey = (HashKey) o;
            return key.equals(hashKey.key) && time.equals(hashKey.time);
        }

        @Override
        public int hashCode() {
            return Objects.hash(key, time);
        }

        @Override
        public String toString() {
            return key + " @ " + time;
        }
    }
}
