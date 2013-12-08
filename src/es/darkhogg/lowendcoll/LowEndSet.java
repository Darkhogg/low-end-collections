package es.darkhogg.lowendcoll;

/**
 * An object that contains elements a maximum of once, where order is not important. This interface effectively
 * describes a mathematical <i>set</i>.
 * 
 * @author Daniel Escoz
 * @param <E> type of the elements contained in this collection
 */
public interface LowEndSet<E> extends LowEndCollection<E> {

    /**
     * Returns whether an object is contained within this set.
     * 
     * @param item Element to check if is contained
     * @return <tt>true</tt> if <tt>item</tt> is contained in this set, <tt>false</tt> otherwise
     */
    public abstract boolean contains (E item);

    /**
     * Removes an object from this set.
     * 
     * @param item Element to be removed
     * @return <tt>true</tt> if <tt>item</tt> was contained in this set, <tt>false</tt> otherwise
     */
    public abstract boolean remove (E item);

}
