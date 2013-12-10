package es.darkhogg.lowendcoll;

import java.util.ListIterator;

/**
 * An object that contains elements in a specific order. This interface effectively describes a mathematical
 * <i>sequence</i>.
 * 
 * @author Daniel Escoz
 * @param <E> type of the elements contained in this collection
 */
public interface LowEndList<E> extends LowEndCollection<E> {

    /**
     * Inserts an element at <tt>pos</tt>, where <tt>0 <= pos && pos <= size()</tt>.
     * 
     * @param pos Position of the element to be inserted
     * @param elem New element to be inserted
     * @throws IndexOutOfBoundsException if <tt>pos</tt> is not an existing index of this list or the next-to-last index
     */
    public abstract void add (int pos, E elem);

    /**
     * Returns the element stored at position <tt>pos</tt>, where <tt>0 <= pos && pos < size()</tt>.
     * 
     * @param pos Position of the element to be retrieved
     * @return Element at position <tt>pos</tt>
     * @throws IndexOutOfBoundsException if <tt>pos</tt> is not an existing index of this list
     */
    public abstract E get (int pos);

    /**
     * Modifies the element stored at position <tt>pos</tt>, where <tt>0 <= pos && pos < size()</tt>.
     * 
     * @param pos Position of the element to be modified
     * @param elem New element to be placed in <tt>pos</tt>
     * @return Old element at position <tt>pos</tt>
     * @throws IndexOutOfBoundsException if <tt>pos</tt> is not an existing index of this list
     */
    public abstract E set (int pos, E elem);

    /**
     * Removes the element stored at position <tt>pos</tt>, where <tt>0 <= pos && pos < size()</tt>. Subsequent elements
     * are moved and their indices are therefore decremented by one.
     * 
     * @param pos Position of the element to be removed
     * @return Old element at position <tt>pos</tt>
     * @throws IndexOutOfBoundsException if <tt>pos</tt> is not an existing index of this list
     */
    public abstract E remove (int pos);

    /**
     * Returns a <i>shared list iterator</i> that will iterate on the elements of this list.
     * <p>
     * The returned iterator may be cached by the collection, and should be used with care. In particular, nested loops
     * may not work correctly if the list caches less iterators than loops are being nested. If you need to perform
     * nested loops and make sure each loop receives a new copy of an iterator, use {@link #newListIterator} instead.
     * 
     * @param pos Initial position of the iterator
     * @return A possibly cached iterator on this collection
     */
    public abstract ListIterator<E> listIterator (int pos);

    /**
     * Returns a <i>fresh list iterator</i> that will iterate on the elements of this list.
     * <p>
     * The returned iterator is guaranteed to be <tt>new</tt>, and does not have the problems described on
     * {@link #listIterator} about shared iterators.
     * 
     * @param pos Initial position of the iterator
     * @return A fresh iterator on this collection
     */
    public abstract ListIterator<E> newListIterator (int pos);

}
