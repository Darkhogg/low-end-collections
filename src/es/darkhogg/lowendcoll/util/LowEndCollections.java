package es.darkhogg.lowendcoll.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.RandomAccess;
import java.util.Set;

import es.darkhogg.lowendcoll.LowEndCollection;
import es.darkhogg.lowendcoll.LowEndList;
import es.darkhogg.lowendcoll.LowEndMap;
import es.darkhogg.lowendcoll.LowEndSet;

/**
 * Utilities for the <i>Low-End Collections Framework</i>.
 * 
 * @author Daniel Escoz
 */
public final class LowEndCollections {

    /**
     * Creates a regular Java {@link Collection} by wrapping a {@link LowEndCollection}.
     * <p>
     * Note that the collection's <tt>iterator</tt> method calls wrapped collection's <tt>newIterator</tt> to eliminate
     * the problems of shared iterators.
     * 
     * @param collection Collection to wrap
     * @return Wrapped collection
     */
    public static <T> Collection<T> asJavaCollection (LowEndCollection<T> collection) {
        return new BridgeCollection<T>(collection);
    }

    /**
     * Creates a regular Java {@link List} by wrapping a {@link LowEndList}.
     * <p>
     * If the passed <tt>list</tt> implements {@link RandomAccess}, the returned list will also implement it.
     * <p>
     * Note that the list's <tt>iterator</tt> and <tt>listIterator</tt> methods call wrapped list's <tt>newIterator</tt>
     * and <tt>newListIterator</tt> respectively to eliminate the problems of shared iterators.
     * 
     * @param list List to wrap
     * @return Wrapped list
     */
    public static <T> List<T> asJavaList (LowEndList<T> list) {
        if (list instanceof RandomAccess) {
            return new BridgeRandomList<T>(list);
        } else {
            return new BridgeSequentialList<T>(list);
        }
    }

    /**
     * Creates a regular Java {@link Set} by wrapping a {@link LowEndSet}.
     * <p>
     * Note that the set's <tt>iterator</tt> method calls wrapped set's <tt>newIterator</tt> to eliminate the problems
     * of shared iterators.
     * 
     * @param set Set to wrap
     * @return Wrapped set
     */
    public static <T> Set<T> asJavaSet (LowEndSet<T> set) {
        return new BridgeSet<T>(set);
    }

    /**
     * Creates a regular Java {@link Map} by wrapping a {@link LowEndMap}.
     * <p>
     * Note that the map's <tt>entrySet</tt> is implemented using the wrapped map's <tt>newIterator</tt> to eliminate
     * the problems of shared iterators.
     * 
     * @param map Map to wrap
     * @return Wrapped map
     */
    public static <K, V> Map<K,V> asJavaMap (LowEndMap<K,V> map) {
        return new BridgeMap<K,V>(map);
    }

    private LowEndCollections () {
        throw new AssertionError();
    }
}
