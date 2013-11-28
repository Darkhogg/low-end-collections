package es.darkhogg.lowendcoll;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Representation of a container of arbitrary objects in which duplicates are allowed and order is not important,
 * implemented using an array.
 * <p>
 * 
 * 
 * @author Daniel Escoz
 * @version 1.0
 * @param <E> Element type
 */
public class LowEndArrayBag<E> extends AbstractCollection<E> implements Collection<E> {

    private static final int ITERATORS = 3;

    /** Elements contained in the bag */
    /* package */E[] elements;

    /** Number of elements contained in the bag */
    /* package */int size;

    /** Internal iterator */
    @SuppressWarnings("unchecked")
    private final LowEndArrayBagIterator[] iterators = (LowEndArrayBagIterator[]) new Object[ITERATORS];

    /** Next iterator to retrieve */
    private int iterator;

    /**
     * Creates a new instance with a predefined capacity.
     */
    public LowEndArrayBag () {
        this(16);
    }

    /**
     * Creates a new instance with an explicit <tt>capacity</tt>.
     * 
     * @param capacity The initial capacity of this bag
     */
    @SuppressWarnings("unchecked")
    public LowEndArrayBag (final int capacity) {
        elements = (E[]) new Object[capacity];
    }

    /**
     * Creates a new instance by adding the elements of another <tt>collection</tt>.
     * 
     * @param collection Collection with the initial elements of this bag
     */
    public LowEndArrayBag (final Collection<? extends E> collection) {
        this(collection.size());
        int i = 0;
        for (E element : collection) {
            elements[i++] = element;
        }
        size = i;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This implementation returns a <i>shared iterator</i> as defined by the <i>Low-end Collections<i> with a maximum
     * nesting level of <i>3</i>. It is safe (but not recommended) to add elements to this bag while iterating.
     * 
     * @return The shared iterator of this instance
     */
    @Override
    public Iterator<E> iterator () {
        LowEndArrayBagIterator it = iterators[iterator++];
        if (it == null) {
            it = new LowEndArrayBagIterator();
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

    @Override
    public boolean add (E element) {
        if (elements.length == size) {
            elements = Arrays.copyOf(elements, size * 2 + 1);
        }
        elements[size++] = element;
        return true;
    }

    @Override
    public void clear () {
        final int lSize = size;
        for (int i = 0; i < lSize; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    /**
     * Iterator over this <tt>ArrayBag</tt>.
     * 
     * @author Daniel Escoz
     * @version 1.0
     */
    /* package */class LowEndArrayBagIterator implements Iterator<E> {

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
            elements[--current] = elements[--size];
            elements[size] = null;
        }

    }
}
