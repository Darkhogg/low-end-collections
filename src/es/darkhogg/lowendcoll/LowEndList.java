package es.darkhogg.lowendcoll;

/**
 * An object that contains elements in a specific order. This interface effectively describes a mathematical
 * <i>sequence</i>.
 * 
 * @author Daniel Escoz
 * @param <E> type of the elements contained in this collection
 */
public interface LowEndList<E> extends LowEndCollection<E> {

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
     * @param elem New elements to be inserted
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

}
