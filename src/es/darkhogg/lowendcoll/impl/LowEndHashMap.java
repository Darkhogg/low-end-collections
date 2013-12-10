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

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndHashMapIterator[] iterators = (LowEndHashMapIterator[]) new Object[ITERATORS];

    /** Next iterator to retrieve */
    private int iterator;

    /**
     * Creates a new closed hash map with default <i>capacity</i> and <i>load factor</i>.
     */
    public LowEndHashMap () {
        this(16, .65f);
    }

    /**
     * Creates a new closed hash map with specified <tt>capacity</tt> and <tt>loadFactor</tt>.
     * 
     * @param capacity Initial capacity of this map
     * @param loadFactor Maximum load factor of this map
     */
    @SuppressWarnings("unchecked")
    public LowEndHashMap (int capacity, float loadFactor) {
        if (loadFactor <= 0 || loadFactor >= 1) {
            throw new IllegalArgumentException("maxFactor: " + loadFactor);
        }

        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];

        this.loadFactor = loadFactor;
        this.size = 0;
    }

    /**
     * Creates a new closed hash map with the same mappings as those from <tt>map</tt>.
     * 
     * @param map Map to copy mappings from
     */
    public LowEndHashMap (Map<? extends K,? extends V> map) {
        this((int) (map.size() / .65f) + 1, .65f);
        for (Map.Entry<? extends K,? extends V> entry : map.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Creates a new closed hash map with the same mappings as those from <tt>map</tt>.
     * 
     * @param map Map to copy mappings from
     */
    public LowEndHashMap (LowEndMap<? extends K,? extends V> map) {
        this((int) (map.size() / .65f) + 1, .65f);
        for (LowEndMap.Entry<? extends K,? extends V> entry : map) {
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
    public boolean containsKey (K key) {
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
    public V get (K key) {
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
    public V put (K key, V value) {
        if (isFull() && !containsKey(key)) {
            throw new IllegalStateException("full");
        }
        if (size > (loadFactor * keys.length)) {
            rehash(Math.min(keys.length * 2 + 1, Integer.MAX_VALUE));
        }

        return putIn(keys, values, key, value);
    }

    @Override
    public V remove (K key) {
        int hash = hash(key, keys.length);

        int t = 0;
        final int al = keys.length;

        while (t < al && keys[hash] != null) {
            if (keys[hash].equals(key)) {
                V oldValue = values[hash];

                keys[hash] = null;
                values[hash] = null;

                int next = nextHash(hash, al);
                while (keys[next] != null) {
                    K pk = keys[next];
                    V pv = values[next];

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

    private final V putIn (K[] keys, V[] values, K key, V value) {
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
                V oldValue = values[hash];
                values[hash] = value;
                return oldValue;
            }

            t++;
            hash = nextHash(hash, keys.length);
        }

        throw new IllegalStateException("[INTERNAL BUG] Not enough space!");
    }

    @SuppressWarnings("unchecked")
    private final void rehash (int capacity) {
        K[] newKeys = (K[]) new Object[capacity];
        V[] newVals = (V[]) new Object[capacity];

        final int al = keys.length;
        for (int i = 0; i < al; i++) {
            if (keys[i] != null) {
                putIn(newKeys, newVals, keys[i], values[i]);
            }
        }

        keys = newKeys;
        values = newVals;
    }

    private static final int hash (Object key, int length) {
        return ((key.hashCode() % length) + length) % length;
    }

    private static final int nextHash (int hash, int length) {
        return (((hash + 1) % length) + length) % length;
    }

    @Override
    public String toString () {
        StringBuilder sb = new StringBuilder();
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
        LowEndHashMapIterator it = new LowEndHashMapIterator(false);
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

        /* package */LowEndHashMapIterator (boolean shareEntries) {
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
        /* package */void reset (K key, V value) {
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
        public V setValue (V newValue) {
            return put(key, newValue);
        }
    }

}
