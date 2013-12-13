package es.darkhogg.lowendcoll.impl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import es.darkhogg.lowendcoll.LowEndCollection;
import es.darkhogg.lowendcoll.LowEndList;

/**
 * Representation of an ordered container of arbitrary objects in which duplicates are allowed and order is important,
 * implemented using an array.
 * 
 * @author Daniel Escoz
 * @version 1.0
 * @param <E> Element type
 */
public class LowEndArrayList<E> implements LowEndList<E>, RandomAccess {

    private static final int ITERATORS = 3;

    /** Elements contained in the bag */
    /* package */E[] elements;

    /** Number of elements contained in the bag */
    /* package */int size;

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndArrayListIterator[] iterators =
        (LowEndArrayListIterator[]) new LowEndArrayList<?>.LowEndArrayListIterator[ITERATORS];

    /** Next iterator to retrieve */
    private int iterator;

    /**
     * Creates a new instance with a predefined capacity.
     */
    public LowEndArrayList () {
        this(16);
    }

    /**
     * Creates a new instance with an explicit <tt>capacity</tt>.
     * 
     * @param capacity The initial capacity of this list
     */
    @SuppressWarnings("unchecked")
    public LowEndArrayList (final int capacity) {
        elements = (E[]) new Object[capacity];
    }

    /**
     * Creates a new instance by adding the elements of another <tt>collection</tt>.
     * 
     * @param collection Collection with the initial elements of this list
     */
    public LowEndArrayList (final Collection<? extends E> collection) {
        this(collection.size());
        int i = 0;
        for (E element : collection) {
            elements[i++] = element;
        }
        size = i;
    }

    /**
     * Creates a new instance by adding the elements of another <tt>collection</tt>.
     * 
     * @param collection Collection with the initial elements of this list
     */
    public LowEndArrayList (final LowEndCollection<? extends E> collection) {
        this(collection.size());
        int i = 0;
        for (E element : collection) {
            elements[i++] = element;
        }
        size = i;
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
    public int size () {
        return size;
    }

    @Override
    public void clear () {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public E get (int pos) {
        check(pos);
        return elements[pos];
    }

    @Override
    public E set (int pos, E element) {
        check(pos);
        E old = elements[pos];
        elements[pos] = element;
        return old;
    }

    @Override
    public boolean add (E element) {
        if (isFull()) {
            throw new IllegalStateException("full");
        }
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, Math.min(size * 2 + 1, Integer.MAX_VALUE));
        }
        elements[size++] = element;
        return true;
    }

    @Override
    public void add (int pos, E elem) {
        if (pos < 0 | pos > size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
        if (isFull()) {
            throw new IllegalStateException("full");
        }
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, Math.min(size * 2 + 1, Integer.MAX_VALUE));
        }
        for (int i = elements.length; i > pos; i--) {
            elements[i] = elements[i - 1];
        }

        elements[pos] = elem;
    }

    @Override
    public E remove (int pos) {
        check(pos);
        E old = elements[pos];
        final int lSize = --size;
        for (int i = pos, j; i < lSize; i = j) {
            j = i + 1;
            elements[i] = elements[j];
        }
        elements[size] = null;
        return old;
    }

    @Override
    public Iterator<E> iterator () {
        return listIterator(0);
    }

    @Override
    public Iterator<E> newIterator () {
        return newListIterator(0);
    }

    /**
     * Check array bounds and throw an exception if outside of the array bounds.
     * 
     * @param pos Position to check
     * @throw IndexOutOfBoundsException if outside the array bounds
     */
    private void check (final int pos) {
        if (pos < 0 | pos >= size) {
            throw new IndexOutOfBoundsException(String.valueOf(pos));
        }
    }

    @Override
    public ListIterator<E> newListIterator (int pos) {
        LowEndArrayListIterator it = new LowEndArrayListIterator();
        it.reset(pos);
        return it;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation returns a <i>shared iterator</i> as defined by the <i>Low-end Collections<i> with a maximum
     * nesting level of <i>3</i>.
     * 
     * @return The shared iterator of this instance
     */
    @Override
    public ListIterator<E> listIterator (int pos) {
        LowEndArrayListIterator it = iterators[iterator++];
        if (it == null) {
            it = new LowEndArrayListIterator();
            iterators[iterator - 1] = it;
        }
        iterator %= ITERATORS;

        it.reset(pos);
        return it;
    }

    /**
     * Iterator over this <tt>ArrayList</tt>.
     * 
     * @author Daniel Escoz
     * @version 1.0
     */
    private class LowEndArrayListIterator implements ListIterator<E> {

        private int last;
        private int current;
        private boolean removed;

        /* package */LowEndArrayListIterator () {
        }

        /**
         * Resets this iterator so it can be reused
         * 
         * @param pos Initial position
         */
        /* package */public void reset (int pos) {
            current = pos;
            removed = true;
        }

        @Override
        public boolean hasNext () {
            return current < size;
        }

        @Override
        public E next () {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            removed = false;
            last = current++;
            return elements[current];
        }

        @Override
        public void remove () {
            if (removed) {
                new IllegalStateException();
            }
            removed = true;

            final int lSize = --size;
            for (int i = last, j; i < lSize; i = j) {
                j = i + 1;
                elements[i] = elements[j];
            }
            elements[size] = null;
        }

        @Override
        public void add (E elem) {
            LowEndArrayList.this.add(last, elem);

        }

        @Override
        public boolean hasPrevious () {
            return current > 0;
        }

        @Override
        public int nextIndex () {
            return current;
        }

        @Override
        public E previous () {
            if (!hasPrevious()) {
                throw new NoSuchElementException();
            }
            removed = false;
            last = --current;
            return elements[current];
        }

        @Override
        public int previousIndex () {
            return current - 1;
        }

        @Override
        public void set (E elem) {
            if (removed) {
                new IllegalStateException();
            }
            elements[last] = elem;
        }
    }

}
