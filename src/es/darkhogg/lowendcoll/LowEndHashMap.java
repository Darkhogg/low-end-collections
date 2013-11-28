package es.darkhogg.lowendcoll;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.darkhogg.lowendcoll.LowEndArrayBag.LowEndArrayBagIterator;

/**
 * @author Daniel Escoz
 * @version 1.0
 * @param <K> Type of the keys
 * @param <V> Type of the values
 */
public class LowEndHashMap<K, V> extends AbstractMap<K,V> {

    /** The keys of this map */
    /* package */K[] keys;

    /** The values of this map */
    /* package */V[] values;

    /** Maximum load factor of the map */
    private final float loadFactor;

    /** Current size of the map */
    /* package */int size;

    private final LowEndHashMapEntrySet entrySet = new LowEndHashMapEntrySet();

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
        putAll(map);
    }
    
    @Override
    public int size () {
        return size;
    }

    @Override
    public boolean containsKey (Object key) {
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
    public boolean containsValue (Object value) {
        final int al = keys.length;
        for (int i = 0; i < al; i++) {
            V val = values[i];
            if (keys[i] != null && (val == value || (val != null && val.equals(value)))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public V get (Object key) {
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
        if (size > (loadFactor * keys.length)) {
            rehash(keys.length * 2 + 1);
        }

        return putIn(keys, values, key, value);
    }

    @Override
    public V remove (Object key) {
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
    public Set<Map.Entry<K,V>> entrySet () {
        return entrySet;
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
    
    /**
     * The entry map of this map.
     * 
     * @author Daniel Escoz
     * @version 1.0
     */
    /* package */class LowEndHashMapEntrySet extends AbstractSet<Map.Entry<K,V>> {
        private static final int ITERATORS = 3;

        /** Internal iterator */
        @SuppressWarnings("unchecked")
        private final LowEndHashMapEntrySetIterator[] iterators =
            (LowEndHashMapEntrySetIterator[]) new Object[ITERATORS];

        /** Next iterator to retrieve */
        private int iterator;

        @Override
        public Iterator<Map.Entry<K,V>> iterator () {
            LowEndHashMapEntrySetIterator it = iterators[iterator++];
            if (it == null) {
                it = new LowEndHashMapEntrySetIterator();
                iterators[iterator - 1] = it;
            }
            iterator %= ITERATORS;

            it.reset();
            return it;
        }

        @Override
        public int size () {
            return size;
        }
    }

    /* package */class LowEndHashMapEntrySetIterator implements Iterator<Map.Entry<K,V>> {

        /** Entry used by this iterator */
        private final LowEndHashMapEntry entry = new LowEndHashMapEntry();

        /** Current iteration index */
        private int index;

        /**
         * Resets this iterator so it can be reused.
         */
        /* package */void reset () {
            index = 0;
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
            entry.reset(keys[index], values[index]);
            index++;
            return entry;
        }

        @Override
        public void remove () {
            // TODO Implement this the right way
            throw new UnsupportedOperationException();
        }

    }

    /* package */class LowEndHashMapEntry implements Map.Entry<K,V> {

        /** Key of this entry */
        private K key;

        /** Value of this entry */
        private V value;

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
            V oldValue = value;
            value = newValue;
            put(key, value);
            return oldValue;
        }
    }
}
