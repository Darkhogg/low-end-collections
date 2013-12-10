package es.darkhogg.lowendcoll.util;

import java.util.AbstractSet;
import java.util.Iterator;

import es.darkhogg.lowendcoll.LowEndSet;

/* package */final class BridgeSet<T> extends AbstractSet<T> {

    private final LowEndSet<T> set;

    /* package */BridgeSet (LowEndSet<T> set) {
        this.set = set;
    }

    @Override
    public boolean add (T elem) {
        return set.add(elem);
    }

    @Override
    public void clear () {
        set.clear();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains (Object obj) {
        // Cast necessary for JCF
        // As this class is already type-erased, no checks are performed
        // Internally on the LEC Set everything should work normally.
        return set.contains((T) obj);
    }

    @Override
    public boolean isEmpty () {
        return set.isEmpty();
    }

    @Override
    public Iterator<T> iterator () {
        return set.newIterator();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean remove (Object elem) {
        // Cast necessary for JCF.
        // As this class is already type-erased, no checks are performed.
        // Internally on the LEC Set everything should work normally.
        return set.remove((T) elem);
    }

    @Override
    public int size () {
        return set.size();
    }

}
