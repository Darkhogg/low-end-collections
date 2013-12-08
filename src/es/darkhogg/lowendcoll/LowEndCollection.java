package es.darkhogg.lowendcoll;

import java.util.Iterator;

/**
 * An object that contains elements with no specific ordering or restrictions.
 * <p>
 * A <i>low-end collection</i> differs from regular Java collections in a few ways:
 * <ul>
 * <li>The interface is simpler, keeping it compact but usable.
 * <li>There's an explicit way of telling whether a collection is full.
 * <li>Allocations via <tt>new</tt> are heavily avoided.
 * <li>Iterators returned by the regular {@link #iterator} method are allowed and encouraged to be <i>shared</i> across
 * calls.
 * </ul>
 * 
 * @author Daniel Escoz
 * @param <E> type of the elements contained in this collection
 */
public interface LowEndCollection<E> extends Iterable<E> {

    /**
     * Returns whether this collection is empty.
     * 
     * @return <tt>true</tt> if this collection does not contain any elements, <tt>false</tt> otherwise
     */
    public abstract boolean isEmpty ();

    /**
     * Returns whether this collection is full.
     * 
     * @return <tt>true</tt> if this collection cannot store more elements, <tt>false</tt> otherwise
     */
    public abstract boolean isFull ();

    /**
     * Returns the number of elements that are stored in this collection.
     * 
     * @return The exact number of elements kept in this collection.
     */
    public abstract int size ();

    /**
     * Inserts a new element in this collection.
     * 
     * @param element Element to be inserted
     * @return <tt>true</tt> if the collection changed as a result of this call, <tt>false</tt> otherwise
     * @throws IllegalStateException if the collection if full
     * @throws IllegalArgumentException if the passed <tt>element</tt> cannot be added for some reason
     * @throws NullPointerException if <tt>element</tt> is <tt>null</tt> and this collection does not accept
     *        <tt>null</tt> values
     */
    public abstract boolean add (E element);

    /**
     * Removes all elements from this collection.
     */
    public abstract void clear ();

    /**
     * Returns a <i>shared iterator</i> that will iterate on the elements of this collection.
     * <p>
     * The returned iterator may be cached by the collection, and should be used with care. In particular, nested loops
     * may not work correctly if the collection caches less iterators than loops are being nested. If you need to
     * perform nested loops and make sure each loop receives a new copy of an iterator, use {@link #newIterator}
     * instead.
     * 
     * @return A possibly cached iterator on this collection
     */
    public abstract Iterator<E> iterator ();

    /**
     * Returns a <i>fresh iterator</i> that will iterate on the elements of this collection.
     * <p>
     * The returned iterator is guaranteed to be <tt>new</tt>, and does not have the problems described on
     * {@link #iterator} about shared iterators.
     * 
     * @return A fresh iterator on this collection
     */
    public abstract Iterator<E> newIterator ();

}
