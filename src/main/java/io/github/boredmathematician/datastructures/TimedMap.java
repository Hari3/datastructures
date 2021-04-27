package io.github.boredmathematician.datastructures;


import java.io.Serializable;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * An object which, just like {@link Map}, maps keys to values, but with a "time" component.
 * Each value is associated with a key, <b>at a specified point in time.</b>
 * It will stay associated with that key forever <i>or</i> until it gets associated
 * with a new value at a later point in time. At any one point in time, the value associated
 * with a key is the most recent value associated with this key.
 * <p>
 * When determining when a particular point in time is "later" than another point in time,
 * and when determining the "most recent" point in time, the comparator returned by {@link #comparator()} is used.
 * If this call returns null, the natural ordering on T is used.
 * <p>
 * This interface internally has the following interfaces:
 * <ul>
 *     <li>{@link Entry}: Denotes an entry in this map.
 *     <li>{@link Key}: A wrapper for (key, time).
 * </ul>
 * <p>
 * Note that "association" between and key and a value, and "presence" of a mapping between key
 * and value are two different things.
 * <p>
 * The following definitions are used when referring to "presence" and "absence" of mappings:
 * <ul>
 *     <li> A mapping between a key and value is said to be present at a specified point in time,
 *     iff there exists an explicit mapping between the key and value at that point in time. That is to say,
 *     even if a value {@code v} may be "associated" with a key {@code k} at time {@code t},
 *     This mapping is only said to be "present" if it was explicitly set using {@link #put(K, T, V) put(k, t, v)}.
 *     This relationship is captured in the behaviour of {@link #containsKey(K, T) containsKey(k, t)} and
 *     {@link #get(K, T) get(k, t)}.
 *     <ul>
 *         <li>A mapping between a key {@code k} and value {@code v} is said to be "present"
 *         at a specified point in time {@code t} iff {@link #containsKey(K, T) containsKey(k, t)}
 *         returns {@code true}. Even if this returns {@code false}, {@link #get(K, T) get(k, t)}
 *         may still return a value {@code v}.
 *     </ul>
 *     <li> A mapping for a key {@code k}, at a specified time is said to "absent", if there exists no mapping for key,
 *     at the specified time or earlier. In other words, there exits no time {@code t} for which
 *     both of the following are true:
 *     <ul>
 *         <li> a mapping is "present" for key {@code k}, at time {@code t}
 *         <li> t is before or exactly equal to time.
 *     </ul>
 *     <li> This relation is captured in the behaviour of {@link #containsFloorKey(K, T) containsFloorKey(k, t)},
 *     if this returns {@code false}, there is an "absence" of a mapping for the key, at the specified time.
 * </ul>
 *
 * @param <K> the type of keys maintained by this map
 * @param <V> the type of mapped values
 * @param <T> the type of the time component, for each key-value pair
 * @author Hari Krishnan
 */
public interface TimedMap<K, T extends Comparable<? super T>, V> {

    /**
     * Returns the comparator used to determine the order of time in this map, or
     * {@code null} if this map uses the {@linkplain Comparable
     * natural ordering} of its times.
     *
     * @return the comparator used to order the times in this map,
     * or {@code null} if this map uses the natural ordering
     * of its times
     */
    Comparator<T> comparator();

    /**
     * Returns the number of key-time-value mappings in this map.
     * This includes all the mappings for all keys, at all point in time.
     *
     * @return the number of key-time-value mappings in this map.
     */
    int size();

    /**
     * Returns {@code true} if this map contains no key-time-value mappings.
     *
     * @return {@code true} if this map contains no key-time-value mappings
     */
    boolean isEmpty();

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
    boolean containsKey(K key);

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
    boolean containsKey(K key, T time);

    /**
     * Returns {@code true} if a mapping for this instance of {@link Key} is "present" in this map.
     * Note that even if this returns {@code false}, {@link #get(Key) get(key)} may still
     * return a value. This happens when a values was associated with an earlier instance of {@link Key}.
     *
     * @param key An instance of {@link Key}, whose presence in this map is to be tested
     * @return {@code true} if a mapping for this instance of {@link Key} is "present" in this map.
     * @see #get(Key) get(key)
     */
    default boolean containsKey(Key<K, T> key) {
        return containsKey(key.getKey(), key.getTime());
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
    boolean containsLowerKey(K key, T time);

    /**
     * Returns {@code true} if a mapping for an instance of {@link Key} {@code k} is "present" such that
     * {@link Key#getTime() k.getTime()} is earlier than {@link Key#getTime() key.getTime()}.
     *
     * @param key An instance of {@link Key}, whose presence in this map is to be tested
     * @return {@code true} if a mapping for an instance of {@link Key} {@code k} is "present" such that
     * {@link Key#getTime() k.getTime()} is earlier than {@link Key#getTime() key.getTime()}
     */
    default boolean containsLowerKey(Key<K, T> key) {
        return containsLowerKey(key.getKey(), key.getTime());
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
    boolean containsFloorKey(K key, T time);

    /**
     * Returns {@code true} if a mapping for an instance of {@link Key} {@code k} is "present" such that
     * {@link Key#getTime() k.getTime()} is earlier than or equal to {@link Key#getTime() key.getTime()}.
     *
     * @param key An instance of {@link Key}, whose presence in this map is to be tested
     * @return {@code true} if a mapping for an instance of {@link Key} {@code k} is "present" such that
     * {@link Key#getTime() k.getTime()} is earlier than or equal to {@link Key#getTime() key.getTime()}
     */
    default boolean containsFloorKey(Key<K, T> key) {
        return containsFloorKey(key.getKey(), key.getTime());
    }

    /**
     * Returns {@code true} if one or more mappings to the specified value is present.
     *
     * @param value value whose presence in this map is to be tested
     * @return {@code true} if one or more mappings to the specified value is present.
     */
    boolean containsValue(V value);

    /**
     * Returns a {@link Map} which maps all known values mapped to this key at various points in time
     *
     * @param key the key whose mapping is to returned
     * @return A mapping between all known points in time for this key, and the value associated
     * with the ket at that time
     */
    Map<T, V> get(K key);

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
    V get(K key, T time);

    /**
     * Returns the value to which the instance of {@link Key} is associated with.
     * If no such mapping exists, the call returns {@code null}.
     *
     * <p>If this map permits null values, then a return value of {@code null} does not
     * <i>necessarily</i> indicate that the map contains no mapping for the instance of {@link Key}; it's also possible
     * that the map explicitly maps the key to {@code null} at this point in time.
     * The {@link #containsKey(Key) containsKey} operation may be used to distinguish these two cases.
     *
     * <p>If the function call returns a value, then it does not <i>necessarily</i> indicate
     * that a mapping for the instance of {@link Key} is present; it's also possible that the
     * map contained a mapping for at instance of {@link Key} {@code k} such that {@link
     * Key#getTime() k.getTime()} is earlier than {@link Key#getTime() key.getTime()}.
     * Just like in the case of {@code null} values, the {@link #containsKey(Key) containsKey}
     * operation may be used to distinguish these two cases.
     *
     * @param key the key whose associated value is to be returned
     * @return the value to which the specified key is mapped at the specified
     * point in time, or earlier, or {@code null} if this no such mapping exists.
     */
    default V get(Key<K, T> key) {
        return get(key.getKey(), key.getTime());
    }

    /**
     * Associates the specified value with the specified key, at the specified time in this map.
     * If the map previously contained a mapping for the key at the specified time,
     * the old value is replaced by the specified value.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @param time  the point in time when this association is to be made
     * @return the previous value associated with {@code key}, or
     * {@code null} if there was no mapping for {@code key} at the specified point in time.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@code key} at the specified point in time,
     * if the implementation supports {@code null} values.)
     */
    V put(K key, T time, V value);

    /**
     * Associates the specified value with the instance of {@link Key} in this map.
     * If the map previously contained a mapping for this instance of {@link Key},
     * the old value is replaced by the specified value.
     *
     * @param key   an instance of {@link Key} with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with {@link Key}, or
     * {@code null} if there was no mapping for {@link Key} at the specified point in time.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with {@link Key} at the specified point in time,
     * if the implementation supports {@code null} values.)
     */
    default V put(Key<K, T> key, V value) {
        return put(key.getKey(), key.getTime(), value);
    }

    /**
     * Removes all mappings at all points in time for a key from this map if it is present.
     *
     * <p>Returns the {@code true} if the map previously contained at least one association with the key,
     * or {@code null} if the map contained no mapping for the key.
     *
     * <p>The map will not contain any mapping for the specified key, at any point in time,
     * once the call returns. That is to say, {@link #containsKey(K) containsKey(key)} will return
     * {@code false}
     *
     * @param key key whose mapping is to be removed from the map
     * @return the {@code true} if the map previously contained at least one association with the key,
     * or {@code false} if the map contained no mapping for the key.
     */
    boolean remove(K key);

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
    V remove(K key, T time);

    /**
     * Removes the mapping for the instance of {@link Key} from this map, if it is present.
     *
     * <p>Returns the {@code true} if the map previously contained an association for the
     * instance of {@link Key} or {@code null} if the map contained no mapping for the key.
     *
     * @param key key whose mapping is to be removed from the map
     * @return the previous value associated with the key, at the specified time.
     * or {@code null} if the map contained no such mapping.
     * @see #get(Key) get(key)
     */
    default V remove(Key<K, T> key) {
        return remove(key.getKey(), key.getTime());
    }

    /**
     * Copies all of the mappings from the specified map to this map.
     * The effect of this call is equivalent to that of calling {@link
     * #put(K, T, V) put(k, t, v)} on this map once for each mapping
     * from key {@code k} to value {@code v}, at time {@code t} in the
     * specified map.  The behavior of this operation is undefined if the
     * specified map is modified while the operation is in progress.
     *
     * @param m mappings to be stored in this map
     */
    void putAll(TimedMap<? extends K, ? extends T, ? extends V> m);

    /**
     * Removes all of the mappings from this map.
     * The map will be empty after this call returns.
     */
    void clear();

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
    Set<Key<K, T>> keySet();

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
    Collection<V> values();

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
    Set<Entry<K, T, V>> entrySet();

    /**
     * Compares the specified object with this map for equality.  Returns
     * {@code true} if the given object is also a map and the two maps
     * represent the same mappings.  More formally, two maps {@code m1} and
     * {@code m2} represent the same mappings if
     * {@code m1.entrySet().equals(m2.entrySet())}.  This ensures that the
     * {@code equals} method works properly across different implementations
     * of the {@code Map} interface.
     *
     * @param o object to be compared for equality with this map
     * @return {@code true} if the specified object is equal to this map
     */
    boolean equals(Object o);

    /**
     * Returns the hash code value for this map.  The hash code of a map is
     * defined to be the sum of the hash codes of each entry in the map's
     * {@code entrySet()} view.  This ensures that {@code m1.equals(m2)}
     * implies that {@code m1.hashCode()==m2.hashCode()} for any two maps
     * {@code m1} and {@code m2}, as required by the general contract of
     * {@link Object#hashCode}.
     *
     * @return the hash code value for this map
     * @see Entry#hashCode()
     * @see Object#equals(Object)
     * @see #equals(Object)
     */
    int hashCode();

    /**
     * Returns the value to which the specified key is mapped at the specified time
     * or earlier, or {@code defaultValue} if this map contains no such mapping.
     *
     * @param key          the key whose associated value is to be returned
     * @param time         the point in time of association
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     */
    default V getOrDefault(K key, T time, V defaultValue) {
        return (containsFloorKey(key, time)) ? get(key, time) : defaultValue;
    }

    /**
     * Returns the value to which the instance of {@link Key} is mapped to,
     * or {@code defaultValue} if this map contains no such mapping.
     *
     * @param key          the key whose associated value is to be returned
     * @param defaultValue the default mapping of the key
     * @return the value to which the specified key is mapped, or
     * {@code defaultValue} if this map contains no mapping for the key
     */
    default V getOrDefault(Key<K, T> key, V defaultValue) {
        return getOrDefault(key.getKey(), key.getTime(), defaultValue);
    }

    /**
     * Performs the given action for each entry in this map until all entries
     * have been processed or the action throws an exception. Unless
     * otherwise specified by the implementing class, actions are performed in
     * the order of entry set iteration (if an iteration order is specified.)
     * Exceptions thrown by the action are relayed to the caller.
     *
     * @param action The action to be performed for each entry
     */
    default void forEach(TriConsumer<? super K, ? super T, ? super V> action) {
        Objects.requireNonNull(action);
        for (Entry<K, T, V> entry : entrySet()) {
            K k;
            T t;
            V v;
            try {
                k = entry.getKey();
                t = entry.getTime();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
            action.accept(k, t, v);
        }
    }

    /**
     * Replaces each entry's value with the result of invoking the given
     * function on that entry until all entries have been processed or the
     * function throws an exception.  Exceptions thrown by the function are
     * relayed to the caller.
     *
     * @param function the function to apply to each entry
     */
    default void replaceAll(TriFunction<? super K, ? super T, ? super V, ? extends V> function) {
        Objects.requireNonNull(function);
        for (Entry<K, T, V> entry : entrySet()) {
            K k;
            T t;
            V v;
            try {
                k = entry.getKey();
                t = entry.getTime();
                v = entry.getValue();
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }

            // ise thrown from function is not a cme.
            v = function.apply(k, t, v);

            try {
                entry.setValue(v);
            } catch (IllegalStateException ise) {
                // this usually means the entry is no longer in the map.
                throw new ConcurrentModificationException(ise);
            }
        }
    }

    /**
     * If the specified key is not already associated with a value at the specified
     * time(or is mapped to {@code null}), associates it with the given value and returns
     * {@code null}, else returns the current value.
     * <p>
     * Note that the definition of "absence" as defined in Javadoc of this class is used.
     *
     * @param key   key with which the specified value is to be associated
     * @param time  the point in time when this association should happen
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     */
    default V putIfAbsent(K key, T time, V value) {
        V v = get(key, time);
        if (v == null) {
            v = put(key, time, value);
        }

        return v;
    }

    /**
     * If the specified instance of {@link Key} is not already associated with a value (or is mapped
     * to {@code null}) associates it with the given value and returns
     * {@code null}, else returns the current value.
     * <p>
     * Note that the definition of "absence" as defined in Javadoc of this class is used.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     */
    default V putIfAbsent(Key<K, T> key, V value) {
        return putIfAbsent(key.getKey(), key.getTime(), value);
    }

    /**
     * Removes the entry for the specified key only if it is currently
     * mapped to the specified value at the specified time.
     *
     * @param key   key with which the specified value is associated
     * @param time  the point in time of the association
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     */
    default boolean remove(K key, T time, V value) {
        if (containsKey(key, time)) {
            V curValue = get(key, time);
            boolean changed = curValue.equals(value);
            if (changed) {
                remove(key, time);
            }
            return changed;
        } else return false;
    }

    /**
     * Removes the entry for the specified instance of {@link Key} only if it is currently
     * mapped to the specified value.
     *
     * @param key   key with which the specified value is associated
     * @param value value expected to be associated with the specified key
     * @return {@code true} if the value was removed
     */
    default boolean remove(Key<K, T> key, V value) {
        return remove(key.getKey(), key.getTime(), value);
    }

    /**
     * Replaces the entry for the specified key only if currently
     * mapped to the specified value at the specified time.
     *
     * @param key      key with which the specified value is associated
     * @param time     the point in time for the expected association
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     */
    default boolean replace(K key, T time, V oldValue, V newValue) {
        boolean changed = containsKey(key, time) && get(key, time).equals(oldValue);
        if (changed) {
            put(key, time, newValue);
        }
        return changed;
    }

    /**
     * Replaces the entry for the specified instance of {@link Key} only if currently
     * mapped to the specified value.
     *
     * @param key      key with which the specified value is associated
     * @param oldValue value expected to be associated with the specified key
     * @param newValue value to be associated with the specified key
     * @return {@code true} if the value was replaced
     */
    default boolean replace(Key<K, T> key, V oldValue, V newValue) {
        return replace(key.getKey(), key.getTime(), oldValue, newValue);
    }

    /**
     * Replaces the entry for the specified key only if it is
     * mapped to some value at the specified time.
     *
     * @param key   key with which the specified value is to be associated
     * @param time  the point in time for association
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     */
    default V replace(K key, T time, V value) {
        return (containsKey(key, time)) ? put(key, time, value) : null;
    }

    /**
     * Replaces the entry for the specified instance of {@link Key} only
     * if it is currently mapped to some value.
     *
     * @param key   key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return the previous value associated with the specified key, or
     * {@code null} if there was no mapping for the key.
     * (A {@code null} return can also indicate that the map
     * previously associated {@code null} with the key,
     * if the implementation supports null values.)
     */
    default V replace(Key<K, T> key, V value) {
        return replace(key.getKey(), key.getTime(), value);
    }

    /**
     * If a mapping for specified key, at the specified time is "absent", or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the function returns {@code null} no mapping is recorded. If
     * the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.
     *
     * @param key             key with which the specified value is to be associated
     * @param time            the point in time for this association
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     * the specified key, or null if the computed value is null
     */
    default V computeIfAbsent(K key, T time,
                              BiFunction<? super K, ? super T, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = get(key, time);
        if (v == null) {
            V newValue = mappingFunction.apply(key, time);
            if (newValue != null) {
                put(key, time, newValue);
                return newValue;
            }
        }

        return v;
    }

    /**
     * If a mapping for specified instance of {@link Key} is "absent", or is mapped
     * to {@code null}), attempts to compute its value using the given mapping
     * function and enters it into this map unless {@code null}.
     *
     * <p>If the function returns {@code null} no mapping is recorded. If
     * the function itself throws an (unchecked) exception, the
     * exception is rethrown, and no mapping is recorded.
     *
     * @param key             key with which the specified value is to be associated
     * @param mappingFunction the function to compute a value
     * @return the current (existing or computed) value associated with
     * the specified key, or null if the computed value is null
     */
    default V computeIfAbsent(Key<K, T> key,
                              Function<Key<? super K, ? super T>, ? extends V> mappingFunction) {
        Objects.requireNonNull(mappingFunction);
        V v = get(key);
        if (v == null) {
            V newValue = mappingFunction.apply(key);
            if (newValue != null) {
                put(key, newValue);
                return newValue;
            }
        }

        return v;
    }

    /**
     * If the value for the specified key is present and non-null at
     * the specified time, attempts to compute a new mapping given
     * the key, the point in time and its current mapped value.
     *
     * <p>If the function returns {@code null}, the mapping is removed.  If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    default V computeIfPresent(K key, T time,
                               TriFunction<? super K, ? super T, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key, time);
        if (containsKey(key, time) && oldValue != null) {
            V newValue = remappingFunction.apply(key, time, oldValue);
            if (newValue != null) {
                put(key, time, newValue);
            } else {
                remove(key, time);
            }
            return newValue;
        } else return null;
    }

    /**
     * If the value for the specified instance of {@link Key} is
     * present and non-null, attempts to compute a new mapping given
     * the key and its current mapped value.
     *
     * <p>If the function returns {@code null}, the mapping is removed.  If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    default V computeIfPresent(Key<K, T> key,
                               BiFunction<Key<? super K, ? super T>, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        V oldValue = get(key);
        if (containsKey(key) && oldValue != null) {
            V newValue = remappingFunction.apply(key, oldValue);
            if (newValue != null) {
                put(key, newValue);
            } else {
                remove(key);
            }
            return newValue;
        } else return null;
    }

    /**
     * Attempts to compute a mapping for the specified key and its current
     * mapped value, at the specified time (or {@code null} if there is no current mapping).
     *
     * <p>If the function returns {@code null}, the mapping is removed (or
     * remains "absent" if initially "absent").  If the function itself throws an
     * (unchecked) exception, the exception is rethrown, and the current mapping
     * is left unchanged.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    default V compute(K key, T time,
                      TriFunction<? super K, ? super T, ? super V, ? extends V> remappingFunction) {

        Objects.requireNonNull(remappingFunction);

        V oldValue = get(key, time);
        V newValue = remappingFunction.apply(key, time, oldValue);

        if (newValue != null) {
            put(key, time, newValue);
        } else {
            remove(key, time);
        }

        return newValue;
    }

    /**
     * Attempts to compute a mapping for the specified instance of {@link Key}
     * and its current mapped value (or {@code null} if there is no current mapping).
     *
     * <p>If the function returns {@code null}, the mapping is removed (or
     * remains "absent" if initially "absent").  If the function itself throws an
     * (unchecked) exception, the exception is rethrown, and the current mapping
     * is left unchanged.
     *
     * @param key               key with which the specified value is to be associated
     * @param remappingFunction the function to compute a value
     * @return the new value associated with the specified key, or null if none
     */
    default V compute(Key<K, T> key,
                      BiFunction<Key<? super K, ? super T>, ? super V, ? extends V> remappingFunction) {

        Objects.requireNonNull(remappingFunction);

        V oldValue = get(key);
        V newValue = remappingFunction.apply(key, oldValue);

        if (newValue != null) {
            put(key, newValue);
        } else {
            remove(key);
        }

        return newValue;
    }

    /**
     * If the specified key is not already associated with a value at the
     * specified time or is associated with null, associates it with the
     * given non-null value. Otherwise, replaces the associated value
     * with the results of the given remapping function, or removes if
     * the result is {@code null}. This method may be of use when
     * combining multiple mapped values for a key.
     *
     * <p>If the function returns {@code null} the mapping is removed.  If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * @param key               key with which the resulting value is to be associated
     * @param time              the point in time for the association
     * @param value             the non-null value to be merged with the existing value
     *                          associated with the key or, if no existing value or a null value
     *                          is associated with the key, to be associated with the key
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key, or null if no
     * value is associated with the key
     */
    default V merge(K key, T time, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key, time);
        V newValue = (containsKey(key, time) && oldValue != null)
                ? remappingFunction.apply(oldValue, value)
                : value;
        if (newValue != null) {
            put(key, time, newValue);
        } else {
            remove(key, time);
        }
        return newValue;
    }

    /**
     * If the specified instance of {#link Key} is not already associated
     * with a value or is associated with null, associates it with the
     * given non-null value. Otherwise, replaces the associated value
     * with the results of the given remapping function, or removes if
     * the result is {@code null}. This method may be of use when
     * combining multiple mapped values for a key.
     *
     * <p>If the function returns {@code null} the mapping is removed.  If the
     * function itself throws an (unchecked) exception, the exception is
     * rethrown, and the current mapping is left unchanged.
     *
     * @param key               key with which the resulting value is to be associated
     * @param value             the non-null value to be merged with the existing value
     *                          associated with the key or, if no existing value or a null value
     *                          is associated with the key, to be associated with the key
     * @param remappingFunction the function to recompute a value if present
     * @return the new value associated with the specified key, or null if no
     * value is associated with the key
     */
    default V merge(Key<K, T> key, V value,
                    BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        Objects.requireNonNull(remappingFunction);
        Objects.requireNonNull(value);
        V oldValue = get(key);
        V newValue = (containsKey(key) && oldValue != null) ? remappingFunction.apply(oldValue, value) :
                value;
        if (newValue != null) {
            put(key, newValue);
        } else {
            remove(key);
        }
        return newValue;
    }

    /**
     * A map entry (key-time-value pair).  The {@link #entrySet} method returns
     * a collection-view of the map, whose elements are of this class. The
     * <i>only</i> way to obtain a reference to a map entry is from the
     * iterator of this collection-view. These {@code Entry} objects are
     * valid <i>only</i> for the duration of the iteration; more formally,
     * the behavior of a map entry is undefined if the backing map has been
     * modified after the entry was returned by the iterator, except through
     * the {@link #setValue(V) setValue} operation on the map entry.
     *
     * @see #entrySet()
     */
    interface Entry<K, T extends Comparable<? super T>, V> {
        /**
         * Returns a comparator that compares {@link Entry} in natural order on key.
         *
         * <p>The returned comparator is serializable and throws {@link
         * NullPointerException} when comparing an entry with a null key.
         *
         * @param <K> the {@link Comparable} type of then map keys
         * @param <V> the type of the map values
         * @return a comparator that compares {@link java.util.Map.Entry} in natural order on key.
         * @see Comparable
         * @since 1.8
         */
        static <K extends Comparable<? super K>, T extends Comparable<? super T>, V> Comparator<Entry<K, T, V>> comparingByKey() {
            //noinspection ComparatorCombinators
            return (Comparator<Entry<K, T, V>> & Serializable)
                    (c1, c2) -> c1.getKey().compareTo(c2.getKey());
        }

        /**
         * Returns a comparator that compares {@link Entry} in natural order on value.
         *
         * <p>The returned comparator is serializable and throws {@link
         * NullPointerException} when comparing an entry with null values.
         *
         * @param <K> the type of the map keys
         * @param <V> the {@link Comparable} type of the map values
         * @return a comparator that compares {@link java.util.Map.Entry} in natural order on value.
         * @see Comparable
         * @since 1.8
         */
        static <K, T extends Comparable<? super T>, V extends Comparable<? super V>> Comparator<Entry<K, T, V>> comparingByValue() {
            //noinspection ComparatorCombinators
            return (Comparator<Entry<K, T, V>> & Serializable)
                    (c1, c2) -> c1.getValue().compareTo(c2.getValue());
        }

        /**
         * Returns a comparator that compares {@link java.util.Map.Entry} by key using the given
         * {@link Comparator}.
         *
         * <p>The returned comparator is serializable if the specified comparator
         * is also serializable.
         *
         * @param <K> the type of the map keys
         * @param <V> the type of the map values
         * @param cmp the key {@link Comparator}
         * @return a comparator that compares {@link java.util.Map.Entry} by the key.
         * @since 1.8
         */
        static <K, T extends Comparable<? super T>, V> Comparator<Entry<K, T, V>> comparingByKey(Comparator<? super K> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Entry<K, T, V>> & Serializable)
                    (c1, c2) -> cmp.compare(c1.getKey(), c2.getKey());
        }

        /**
         * Returns a comparator that compares {@link java.util.Map.Entry} by value using the given
         * {@link Comparator}.
         *
         * <p>The returned comparator is serializable if the specified comparator
         * is also serializable.
         *
         * @param <K> the type of the map keys
         * @param <V> the type of the map values
         * @param cmp the value {@link Comparator}
         * @return a comparator that compares {@link java.util.Map.Entry} by the value.
         * @since 1.8
         */
        static <K, T extends Comparable<? super T>, V> Comparator<Entry<K, T, V>> comparingByValue(Comparator<? super V> cmp) {
            Objects.requireNonNull(cmp);
            return (Comparator<Entry<K, T, V>> & Serializable)
                    (c1, c2) -> cmp.compare(c1.getValue(), c2.getValue());
        }

        /**
         * Returns the key corresponding to this entry.
         *
         * @return the key corresponding to this entry
         */
        K getKey();

        /**
         * Returns the point in time corresponding to this entry.
         *
         * @return the point in time corresponding to this entry
         */

        T getTime();

        /**
         * Returns the value corresponding to this entry.
         *
         * @return the value corresponding to this entry
         */
        V getValue();

        /**
         * Replaces the value corresponding to this entry with the specified
         * value (optional operation).  (Writes through to the map.)
         *
         * @param value new value to be stored in this entry
         * @return old value corresponding to the entry
         */
        V setValue(V value);

        /**
         * Compares the specified object with this entry for equality.
         * Returns {@code true} if the given object is also a map entry and
         * the two entries represent the same mapping.  More formally, two
         * entries {@code e1} and {@code e2} represent the same mapping
         * if<pre>
         *     (e1.getKey()==null ?
         *      e2.getKey()==null : e1.getKey().equals(e2.getKey()))  &amp;&amp;
         *     (e1.getValue()==null ?
         *      e2.getValue()==null : e1.getValue().equals(e2.getValue()))
         * </pre>
         * This ensures that the {@code equals} method works properly across
         * different implementations of the {@code Map.Entry} interface.
         *
         * @param o object to be compared for equality with this map entry
         * @return {@code true} if the specified object is equal to this map
         * entry
         */
        boolean equals(Object o);

        /**
         * Returns the hash code value for this map entry.  The hash code
         * of a map entry {@code e} is defined to be: <pre>
         *     (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
         *     (e.getValue()==null ? 0 : e.getValue().hashCode())
         * </pre>
         * This ensures that {@code e1.equals(e2)} implies that
         * {@code e1.hashCode()==e2.hashCode()} for any two Entries
         * {@code e1} and {@code e2}, as required by the general
         * contract of {@code Object.hashCode}.
         *
         * @return the hash code value for this map entry
         * @see Object#hashCode()
         * @see Object#equals(Object)
         * @see #equals(Object)
         */
        int hashCode();
    }

    /**
     * @param <K>
     * @param <T>
     */
    interface Key<K, T extends Comparable<? super T>> {

        /**
         * Returns The key component of this key-time pair
         *
         * @return The key component of this key-time pair
         */
        K getKey();

        /**
         * Returns The time component of this key-time pair
         *
         * @return The time component of this key-time pair
         */
        T getTime();

        /**
         * Compares the specified object with this entry for equality.
         * Returns {@code true} if the given object is also a map entry and
         * the two entries represent the same mapping.  More formally, two
         * entries {@code e1} and {@code e2} represent the same mapping
         * if<pre>
         *     (e1.getKey()==null ?
         *      e2.getKey()==null : e1.getKey().equals(e2.getKey()))  &amp;&amp;
         *     (e1.getValue()==null ?
         *      e2.getValue()==null : e1.getValue().equals(e2.getValue()))
         * </pre>
         * This ensures that the {@code equals} method works properly across
         * different implementations of the {@code Map.Entry} interface.
         *
         * @param o object to be compared for equality with this map entry
         * @return {@code true} if the specified object is equal to this map
         * entry
         */
        boolean equals(Object o);

        /**
         * Returns the hash code value for this map entry.  The hash code
         * of a map entry {@code e} is defined to be: <pre>
         *     (e.getKey()==null   ? 0 : e.getKey().hashCode()) ^
         *     (e.getValue()==null ? 0 : e.getValue().hashCode())
         * </pre>
         * This ensures that {@code e1.equals(e2)} implies that
         * {@code e1.hashCode()==e2.hashCode()} for any two Entries
         * {@code e1} and {@code e2}, as required by the general
         * contract of {@code Object.hashCode}.
         *
         * @return the hash code value for this map entry
         * @see Object#hashCode()
         * @see Object#equals(Object)
         * @see #equals(Object)
         */
        int hashCode();
    }
}
