package es.darkhogg.lowendcoll.impl;

import java.util.Iterator;

import es.darkhogg.lowendcoll.LowEndMap;
import es.darkhogg.lowendcoll.LowEndSet;

public final class LowEndHashSet<E> implements LowEndSet<E> {

    /** Number of iterators */
    private static final int ITERATORS = 3;

    /** Non-null element */
    private static final Object ELEM = new Object();

    /** Internal map as implementation of the set */
    private final LowEndMap<E,Object> map = new LowEndHashMap<E,Object>();

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndHashSetIterator[] iterators = (LowEndHashSetIterator[]) new Object[ITERATORS];

    /** Next iterator to retrieve */
    private int iterator;

    @Override
    public int size () {
        return map.size();
    }

    @Override
    public boolean isEmpty () {
        return map.isEmpty();
    }

    @Override
    public boolean isFull () {
        return map.isFull();
    }

    @Override
    public boolean add (E element) {
        return map.put(element, ELEM) == null;
    }

    @Override
    public boolean contains (E item) {
        return map.containsKey(item);
    }

    @Override
    public boolean remove (E item) {
        return map.remove(item) != null;
    }

    @Override
    public void clear () {
        map.clear();
    }

    @Override
    public Iterator<E> iterator () {
        LowEndHashSetIterator it = iterators[iterator++];
        if (it == null) {
            it = new LowEndHashSetIterator();
            iterators[iterator - 1] = it;
        }
        iterator %= ITERATORS;

        it.reset(map.iterator());
        return it;
    }

    @Override
    public Iterator<E> newIterator () {
        LowEndHashSetIterator it = new LowEndHashSetIterator();
        it.reset(map.newIterator());
        return it;
    }

    private final class LowEndHashSetIterator implements Iterator<E> {

        private Iterator<? extends LowEndMap.Entry<E,?>> iterator;
        
        /* package */LowEndHashSetIterator () {
            
        }

        /* package */void reset (Iterator<? extends LowEndMap.Entry<E,?>> iterator) {
            this.iterator = iterator;
        }
        
        @Override
        public boolean hasNext () {
            return iterator.hasNext();
        }

        @Override
        public E next () {
            return iterator.next().getKey();
        }

        @Override
        public void remove () {
            iterator.remove();
        }
        
    }
}
