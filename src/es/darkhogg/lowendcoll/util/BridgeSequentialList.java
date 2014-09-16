package es.darkhogg.lowendcoll.util;

import java.util.AbstractSequentialList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.RandomAccess;

import es.darkhogg.lowendcoll.LowEndList;

/* package */final class BridgeSequentialList<T> extends AbstractSequentialList<T> {

    /** Internal wrapped list */
    private final LowEndList<T> list;

    /**
     * Creates a JCF list by wrapping a Low-End List.
     * 
     * @param list List to wrap
     */
    /* package */BridgeSequentialList (LowEndList<T> list) {
        this.list = list;
    }

    @Override
    public boolean add (T element) {
        return list.add(element);
    }

    @Override
    public void add (int pos, T elem) {
        list.add(pos, elem);
    }

    @Override
    public void clear () {
        list.clear();
    }

    @Override
    public T get (int pos) {
        return list.get(pos);
    }

    @Override
    public boolean isEmpty () {
        return list.isEmpty();
    }

    @Override
    public Iterator<T> iterator () {
        return list.newIterator();
    }

    @Override
    public T remove (int pos) {
        return list.remove(pos);
    }

    @Override
    public T set (int pos, T elem) {
        return list.set(pos, elem);
    }

    @Override
    public int size () {
        return list.size();
    }

    @Override
    public ListIterator<T> listIterator (int pos) {
        return list.newListIterator(pos);
    }

}
