package es.darkhogg.lowendcoll.util;

import java.util.AbstractCollection;
import java.util.Iterator;

import es.darkhogg.lowendcoll.LowEndCollection;

/* package */class BridgeCollection<T> extends AbstractCollection<T> {

    /** Internal wrapped collection */
    private final LowEndCollection<T> collection;

    /**
     * Creates a JCF collection by wrapping a Low-End Collection.
     * 
     * @param collection Collection to wrap
     */
    /* package */BridgeCollection (LowEndCollection<T> collection) {
        this.collection = collection;
    }

    @Override
    public boolean add (T elem) {
        return collection.add(elem);
    }

    @Override
    public void clear () {
        collection.clear();
    }

    @Override
    public boolean isEmpty () {
        return collection.isEmpty();
    }

    @Override
    public Iterator<T> iterator () {
        return collection.newIterator();
    }

    @Override
    public int size () {
        return collection.size();
    }

}
