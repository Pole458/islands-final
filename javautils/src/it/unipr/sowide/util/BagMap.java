package it.unipr.sowide.util;


import it.unipr.sowide.util.annotations.Mutable;

import java.util.*;

/**
 * A BagMap is a special kind of {@link Map} where for each key there zero, one,
 * or several values.
 * <p></p>
 * Note that in this implementation, {@code BagMap<K, V>} inherits from
 * {@code Map<K, Set<V>>} by delegating to an {@link HashMap} with values in
 * {@link HashSet}s.
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author Giuseppe Petrosino (giuseppe.petrosino@studenti.unipr.it)
 */
@Mutable
public class BagMap<K, V> implements Map<K, Set<V>> {

    private final HashMap<K, Set<V>> map;


    /**
     * Constructs an empty BagMap with the specified initial capacity.
     *
     * @param initialBagCount the initial capacity
     */
    public BagMap(int initialBagCount) {
        this.map = new HashMap<>(initialBagCount);
    }

    /**
     * Constructs a new BagMap and uses the entries in the specified {@code map}
     * as initial values.
     *
     * @param map the map
     */
    public BagMap(Map<K, Set<V>> map) {
        this.map = new HashMap<>(map);
    }

    /**
     * Constructs an empty BagMap with the default initial capacity (16).
     */
    public BagMap() {
        this.map = new HashMap<>();
    }

    /**
     * Returns the number of total values in the <i>bags</i>.
     *
     * @return the number of total values in the <i>bags</i>.
     */
    @Override
    public int size() {
        return this.map.values().stream()
                .mapToInt(Set::size)
                .sum();
    }

    /**
     * Returns the number of <i>bags</i> (i.e. the number of groups of elements,
     * one for each key.
     *
     * @return the number of <i>bags</i> (i.e. the number of groups of elements,
     * one for each key.
     */
    public int bagCount() {
        return map.size();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public boolean containsValue(Object value) {
        return this.map.values().stream()
                .anyMatch(v -> v.equals(value));
    }

    /**
     * Returns a bag with all the elements mapped from the {@code key}.
     *
     * @param key the key
     * @return a bag with all the elements mapped from the {@code key}; null
     * if such bag does not exists.
     */
    @Override
    public Set<V> get(Object key) {
        return this.map.get(key);
    }

    /**
     * Adds all the {@code values} in the bag corresponding to the {@code key}.
     * If there is no such bag, it will be created first.
     *
     * @param key    the key
     * @param values the values
     * @return the bag, after the insertion of the new values.
     */
    @Override
    public Set<V> put(K key, Set<V> values) {
        Set<V> vs = map.computeIfAbsent(key, k -> new HashSet<>());
        vs.addAll(values);
        return vs;
    }

    /**
     * Adds the {@code value} in the bag corresponding to the {@code key}.
     * If there is no such bag, it will be created first.
     *
     * @param key   the key
     * @param value the value
     * @return the bag, after the insertion of the new value.
     */
    public Set<V> putElement(K key, V value) {
        Set<V> vs = map.computeIfAbsent(key, k -> new HashSet<>());
        vs.add(value);
        return vs;
    }

    /**
     * Removes the bag corresponding to the {@code key}.
     *
     * @param key the key of the bag to be removed
     * @return the bag just removed.
     */
    @Override
    public Set<V> remove(Object key) {
        return map.remove(key);
    }

    /**
     * Removes the specified {@code element} from the bag corresponding to the
     * {@code key}, only if is present. If not, this method does nothing.
     *
     * @param key     the key of the bag containing the element to be removed
     * @param element the element to be removed
     */
    public void removeElement(Object key, Object element) {
        //noinspection SuspiciousMethodCalls
        Optional.ofNullable(this.map.get(key))
                .ifPresent(s -> s.remove(element));
    }

    /**
     * Removes the specified {@code element} from all the bags.
     *
     * @param element the element to be removed
     */
    public void removeElement(Object element) {
        for (Set<V> bag : map.values()) {
            //noinspection SuspiciousMethodCalls
            bag.remove(element);
        }
    }

    /**
     * Puts all the entries in {@code m} as key-bag entries in this map.
     *
     * @param m the map
     */
    @Override
    public void putAll(Map<? extends K, ? extends Set<V>> m) {
        this.map.putAll(m);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public void clear() {
        this.map.clear();
    }

    /**
     * Removes all the elements from the bag with the specified key, if the bag
     * is present. Otherwise, this method does nothing.
     *
     * @param key the key of the bag to be cleared.
     */
    public void clearBag(Object key) {
        //noinspection SuspiciousMethodCalls
        Optional.ofNullable(this.map.get(key))
                .ifPresent(Set::clear);
    }

    /**
     * {@inheritDoc}
     **/
    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    /**
     * Returns a collection, backed by this map, of all the bags in the map.
     *
     * @return a collection, backed by this map, of all the bags in the map.
     */
    @Override
    public Collection<Set<V>> values() {
        return this.map.values();
    }

    /**
     * Returns the union of all the bags in the map.
     *
     * @return the union of all the bags in the map.
     */
    public Collection<V> bagUnion() {
        Set<V> vs = new HashSet<>();
        this.values().forEach(vs::addAll);
        return vs;
    }


    @Override
    public Set<Entry<K, Set<V>>> entrySet() {
        return this.map.entrySet();
    }
}
