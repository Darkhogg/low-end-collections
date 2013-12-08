package es.darkhogg.lowendcoll;

import java.util.Iterator;

/**
 * An object that maps keys to values.
 * <p>
 * A <i>low-end map</i> differs from regular Java maps in a few ways:
 * <ul>
 * <li>The interface is simpler, keeping it compact but usable.
 * <li>There's an explicit way of telling whether a map is full.
 * <li>Allocations via <tt>new</tt> are heavily avoided.
 * <li>Maps are {@link java.lang.Iterable}.
 * <li>Iterators returned by the regular {@link #iterator} method are allowed and encouraged to be <i>shared</i> across
 * calls.
 * </ul>
 * 
 * @author Daniel Escoz
 * @param <K> type of the keys contained in this map
 * @param <V> type of the values contained in this map
 */
public interface LowEndMap<K, V> extends Iterable<LowEndMap.Entry<K,V>> {

    /**
     * Returns whether this map is empty.
     * 
     * @return <tt>true</tt> if this map does not contain any key-value pairs, <tt>false</tt> otherwise
     */
    public abstract boolean isEmpty ();

    /**
     * Returns whether this map is full.
     * 
     * @return <tt>true</tt> if this map cannot store more key-value pairs, <tt>false</tt> otherwise
     */
    public abstract boolean isFull ();

    /**
     * Returns the number of key-value pairs that are stored in this map.
     * 
     * @return The exact number of key-value pairs kept in this map.
     */
    public abstract int size ();

    /**
     * Returns the value associated with a given <tt>key</tt>.
     * 
     * @param key Key to look up
     * @return The value associated with <tt>key</tt>, or <tt>null</tt> if there's none
     */
    public abstract V get (K key);

    /**
     * Associates the given <tt>key</tt> with the given <tt>value</tt>.
     * 
     * @param key Key to associate
     * @param value Value to be associated
     * @return The old value associated with <tt>key</tt>, or <tt>null</tt> if there was none
     * @throws IllegalStateException if this map cannot take more elements
     * @throws NullPointerException if <tt>key</tt> or <tt>value</tt> are <tt>null</tt>
     */
    public abstract V put (K key, V value);

    /**
     * Removes the given <tt>key</tt> and its associated value from this map.
     * 
     * @param key Key to be removed
     * @return The old value associated with <tt>key</tt>, or <tt>null</tt> if there was none
     */
    public abstract V remove (K key);

    /**
     * Removes all key-value pairs from this map.
     */
    public abstract void clear ();

    /**
     * Returns whether a key is contained within this map.
     * 
     * @param key Key to check if is contained
     * @return <tt>true</tt> if <tt>key</tt> is contained in this map, <tt>false</tt> otherwise
     */
    public abstract boolean containsKey (K key);

    /**
     * Returns a <i>shared iterator</i> that will iterate on the mappings of this map.
     * <p>
     * The returned iterator may be cached by the map, and should be used with care. In particular, nested loops may not
     * work correctly if the map caches less iterators than loops are being nested. If you need to perform nested loops
     * and make sure each loop receives a new copy of an iterator, use {@link #newIterator} instead.
     * 
     * @return A possibly cached iterator on this map
     */
    public abstract Iterator<LowEndMap.Entry<K,V>> iterator ();

    /**
     * Returns a <i>fresh iterator</i> that will iterate on the mappings of this map.
     * <p>
     * The returned iterator is guaranteed to be <tt>new</tt>, and does not have the problems described on
     * {@link #iterator} about shared iterators.
     * 
     * @return A fresh iterator on this map
     */
    public abstract Iterator<LowEndMap.Entry<K,V>> newIterator ();

    /**
     * A key-value pair.
     * 
     * @author Daniel Escoz
     * @param <K> type of the key contained in this entry
     * @param <V> type of the value contained in this entry
     */
    public interface Entry<K, V> {
        /** @return Key of this entry */
        public abstract K getKey ();

        /** @return Value of this entry */
        public abstract V getValue ();
    }
}
