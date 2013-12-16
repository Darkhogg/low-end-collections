package es.darkhogg.lowendcoll.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;

import es.darkhogg.lowendcoll.LowEndMap;

/**
 * @author Daniel Escoz
 * @version 1.0
 * @param <K> Type of the keys
 * @param <V> Type of the values
 */
public class LowEndHashMap<K, V> implements LowEndMap<K,V> {

    /** Number of iterators */
    private static final int ITERATORS = 3;

    /** The keys of this map */
    /* package */K[] keys;

    /** The values of this map */
    /* package */V[] values;

    /** Maximum load factor of the map */
    private final float loadFactor;

    /** Current size of the map */
    /* package */int size;

    /** Max size of the map */
    private int maxSize;

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndHashMapIterator[] iterators =
        (LowEndHashMapIterator[]) new LowEndHashMap<?,?>.LowEndHashMapIterator[ITERATORS];

    /** Next iterator to retrieve */
    private int iterator;

    /**
     * Creates a new closed hash map with default <i>capacity</i> and <i>load factor</i>.
     */
    public LowEndHashMap () {
        this(64, .65f);
    }

    /**
     * Creates a new closed hash map with specified <tt>capacity</tt> and <tt>loadFactor</tt>.
     * 
     * @param capacity Initial capacity of this map
     * @param loadFactor Maximum load factor of this map
     */
    @SuppressWarnings("unchecked")
    public LowEndHashMap (final int capacity, final float loadFactor) {
        if (loadFactor <= 0 || loadFactor >= 1) {
            throw new IllegalArgumentException("maxFactor: " + loadFactor);
        }

        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];

        this.loadFactor = loadFactor;
        this.size = 0;
        this.maxSize = (int) (loadFactor * keys.length);
    }

    /**
     * Creates a new closed hash map with the same mappings as those from <tt>map</tt>.
     * 
     * @param map Map to copy mappings from
     */
    public LowEndHashMap (final Map<? extends K,? extends V> map) {
        this((int) (map.size() / .65f) + 1, .75f);
        for (final Map.Entry<? extends K,? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Creates a new closed hash map with the same mappings as those from <tt>map</tt>.
     * 
     * @param map Map to copy mappings from
     */
    public LowEndHashMap (final LowEndMap<? extends K,? extends V> map) {
        this((int) (map.size() / .65f) + 1, .65f);
        for (final LowEndMap.Entry<? extends K,? extends V> entry : map) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public int size () {
        return size;
    }

    @Override
    public boolean isEmpty () {
        return size == 0;
    }

    @Override
    public boolean isFull () {
        return size == Integer.MAX_VALUE;
    }

    @Override
    public boolean containsKey (final K key) {
        int hash = hash(key, keys.length);

        int t = 0;
        final int al = keys.length;

        while (t < al && keys[hash] != null) {
            if (keys[hash].equals(key)) {
                return true;
            }
            t++;
            hash = nextHash(hash, keys.length);
        }

        return false;
    }

    @Override
    public void clear () {
        final int al = keys.length;
        for (int i = 0; i < al; i++) {
            keys[i] = null;
            values[i] = null;
        }
        size = 0;
    }

    @Override
    public V get (final K key) {
        int hash = hash(key, keys.length);

        int t = 0;
        final int al = keys.length;

        while (t < al && keys[hash] != null) {
            if (keys[hash].equals(key)) {
                return values[hash];
            }
            t++;
            hash = nextHash(hash, keys.length);
        }

        return null;
    }

    @Override
    public V put (final K key, final V value) {
        if (isFull() && !containsKey(key)) {
            throw new IllegalStateException("full");
        }
        if (size > maxSize) {
            rehash(Math.min(keys.length * 4 + 3, Integer.MAX_VALUE));
        }

        return putIn(keys, values, key, value);
    }

    @Override
    public V remove (final K key) {
        int hash = hash(key, keys.length);

        int t = 0;
        final int al = keys.length;

        while (t < al && keys[hash] != null) {
            if (keys[hash].equals(key)) {
                final V oldValue = values[hash];

                keys[hash] = null;
                values[hash] = null;

                int next = nextHash(hash, al);
                while (keys[next] != null) {
                    final K pk = keys[next];
                    final V pv = values[next];

                    keys[next] = null;
                    values[next] = null;

                    putIn(keys, values, pk, pv);

                    next = nextHash(next, al);
                }

                return oldValue;
            }
            t++;
            hash = nextHash(hash, al);
        }

        return null;
    }

    private final V putIn (final K[] keys, final V[] values, final K key, final V value) {
        int hash = hash(key, keys.length);

        int t = 0;
        final int al = keys.length;

        while (t < al) {
            if (keys[hash] == null) {
                keys[hash] = key;
                values[hash] = value;
                size++;
                return null;
            }
            if (keys[hash].equals(key)) {
                final V oldValue = values[hash];
                values[hash] = value;
                return oldValue;
            }

            t++;
            hash = nextHash(hash, keys.length);
        }

        throw new IllegalStateException("[INTERNAL BUG] Not enough space!");
    }

    @SuppressWarnings("unchecked")
    private final void rehash (final int capacity) {
        final K[] newKeys = (K[]) new Object[capacity];
        final V[] newVals = (V[]) new Object[capacity];

        final int al = keys.length;
        for (int i = 0; i < al; i++) {
            if (keys[i] != null) {
                putIn(newKeys, newVals, keys[i], values[i]);
            }
        }

        keys = newKeys;
        values = newVals;

        maxSize = (int) (loadFactor * keys.length);
    }

    private static final int hash (final Object key, final int length) {
        return (key.hashCode() & 0x7FFFFFFF) % length;
    }

    private static final int nextHash (final int hash, final int length) {
        return ((hash + 1) & 0x7FFFFFFF) % length;
    }

    @Override
    public String toString () {
        final StringBuilder sb = new StringBuilder();
        sb.append('[');
        boolean after = false;

        final int al = keys.length;
        for (int i = 0; i < al; i++) {
            if (keys[i] != null) {
                if (after) {
                    sb.append(',').append(' ');
                }
                after = true;
                sb.append("(" + i + ":" + hash(keys[i], al) + ")");
                sb.append(keys[i]).append('=').append(values[i]);
            }
        }
        return sb.append(']').toString();
    }

    @Override
    public Iterator<LowEndMap.Entry<K,V>> iterator () {
        LowEndHashMapIterator it = iterators[iterator++];
        if (it == null) {
            it = new LowEndHashMapIterator(true);
            iterators[iterator - 1] = it;
        }
        iterator %= ITERATORS;

        it.reset();
        return it;
    }

    @Override
    public Iterator<es.darkhogg.lowendcoll.LowEndMap.Entry<K,V>> newIterator () {
        final LowEndHashMapIterator it = new LowEndHashMapIterator(false);
        it.reset();
        return it;
    }

    private class LowEndHashMapIterator implements Iterator<LowEndMap.Entry<K,V>> {

        /** Entry used by this iterator */
        private final LowEndHashMapEntry entry;

        /** Current iteration index */
        private int index;

        /** Whether the lastitem was removed */
        private boolean removed;

        /* package */LowEndHashMapIterator (final boolean shareEntries) {
            if (shareEntries) {
                entry = new LowEndHashMapEntry();
            } else {
                entry = null;
            }
        }

        /**
         * Resets this iterator so it can be reused.
         */
        /* package */void reset () {
            index = 0;
            removed = true;
        }

        @Override
        public boolean hasNext () {
            final int al = keys.length;
            while (index < al && keys[index] == null) {
                index++;
            }
            return index < al;
        }

        @Override
        public LowEndHashMapEntry next () {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            removed = false;
            final LowEndHashMapEntry entry = this.entry == null ? new LowEndHashMapEntry() : this.entry;

            entry.reset(keys[index], values[index]);
            index++;
            return entry;
        }

        @Override
        public void remove () {
            if (removed) {
                new IllegalStateException();
            }
            removed = true;

            // Look for the last element
            do {
                index--;
            } while (keys[index] == null);

            // Remove directly
            LowEndHashMap.this.remove(keys[index]);
        }

    }

    private class LowEndHashMapEntry implements LowEndMap.Entry<K,V> {

        /** Key of this entry */
        private K key;

        /** Value of this entry */
        private V value;

        /* package */LowEndHashMapEntry () {
        }

        /**
         * Resets the values of this entry.
         * 
         * @param key New key to use
         * @param value New value to use
         */
        /* package */void reset (final K key, final V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey () {
            return key;
        }

        @Override
        public V getValue () {
            return value;
        }

        @Override
        public V setValue (final V newValue) {
            return put(key, newValue);
        }
    }

}
