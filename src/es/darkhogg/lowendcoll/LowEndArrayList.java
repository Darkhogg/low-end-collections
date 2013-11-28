package es.darkhogg.lowendcoll;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Representation of an ordered container of arbitrary objects in which duplicates are allowed and order is important, implemented using an array.
 * <p>
 * 
 * 
 * @author Daniel Escoz
 * @version 1.0
 * @param <E> Element type
 */
public class LowEndArrayList<E> extends AbstractList<E> {

    private static final int ITERATORS = 3;

    /** Elements contained in the bag */
    /* package */E[] elements;

    /** Number of elements contained in the bag */
    /* package */int size;

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndArrayListIterator[] iterators = (LowEndArrayListIterator[]) new Object[ITERATORS];

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
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, size * 2 + 1);
        }
        elements[size++] = element;
        return true;
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
    public int size () {
        return size;
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
    public Iterator<E> iterator () {
        LowEndArrayListIterator it = iterators[iterator++];
        if (it == null) {
            it = new LowEndArrayListIterator();
            iterators[iterator - 1] = it;
        }
        iterator %= ITERATORS;

        it.reset();
        return it;
    }

    /**
     * Iterator over this <tt>ArrayList</tt>.
     * 
     * @author Daniel Escoz
     * @version 1.0
     */
    /* package */class LowEndArrayListIterator implements Iterator<E> {

        private int current;

        /** Resets this iterator so it can be reused */
        /* package */public void reset () {
            current = 0;
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
            return elements[current++];
        }

        @Override
        public void remove () {
            final int lSize = --size;
            for (int i = --current, j; i < lSize; i = j) {
                j = i + 1;
                elements[i] = elements[j];
            }
            elements[size] = null;
        }
    }

}
