package es.darkhogg.lowendcoll.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import es.darkhogg.lowendcoll.LowEndMap;

/* package */final class BridgeMap<K, V> extends AbstractMap<K,V> {

    /* package */final LowEndMap<K,V> map;

    /* package */BridgeMap (LowEndMap<K,V> map) {
        this.map = map;
    }

    @Override
    public void clear () {
        map.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey (Object key) {
        return map.containsKey((K) key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get (Object key) {
        return map.get((K) key);
    }

    @Override
    public boolean isEmpty () {
        return map.isEmpty();
    }

    @Override
    public V put (K key, V value) {
        return map.put(key, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove (Object key) {
        return map.remove((K) key);
    }

    @Override
    public int size () {
        return map.size();
    }

    @Override
    public Set<java.util.Map.Entry<K,V>> entrySet () {
        return new BridgeMapEntrySet();
    }

    private final class BridgeMapEntrySet extends AbstractSet<Map.Entry<K,V>> {

        /* package */public BridgeMapEntrySet () {
        }

        @Override
        public Iterator<Map.Entry<K,V>> iterator () {
            return new BridgeMapEntryIterator<K,V>(map.iterator());
        }

        @Override
        public int size () {
            return map.size();
        }

    }

    private final static class BridgeMapEntryIterator<K, V> implements Iterator<Entry<K,V>> {

        private final Iterator<LowEndMap.Entry<K,V>> iterator;

        /* package */BridgeMapEntryIterator (Iterator<LowEndMap.Entry<K,V>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext () {
            return iterator.hasNext();
        }

        @Override
        public Map.Entry<K,V> next () {
            return new BridgeMapEntry<K,V>(iterator.next());
        }

        @Override
        public void remove () {
            iterator.remove();
        }

    }

    private static final class BridgeMapEntry<K, V> implements Map.Entry<K,V> {

        private final LowEndMap.Entry<K,V> entry;

        /* package */BridgeMapEntry (LowEndMap.Entry<K,V> entry) {
            this.entry = entry;
        }

        @Override
        public K getKey () {
            return entry.getKey();
        }

        @Override
        public V getValue () {
            return entry.getValue();
        }

        @Override
        public V setValue (V value) {
            return entry.setValue(value);
        }

    }
}
