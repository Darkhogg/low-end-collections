Low-End Collections
===================

The *Low-End Collections Framework* is a re-implementation of most of the Java Collections Framework base classes and interfaces that focuses on high-performance and low memory allocations at the cost of some memory size and usage safety.

> **WARNING**: Do not use this library unless you really need to.
Read this README file carefully to see if you should use it or not.
These classes must only be used for situations that *require* it, as they include some *intentional* safety
vulnerabilities for the sake of speed and low memory allocation.

Its usage should be limited to the internals of higher-level classes or for code that *knows* what this library is and can use it accordingly.
These collections are *intentionally* incompatible with regular Java Collections to avoid misuse.


Motivations
-----------

Although the Java Collections Framework is *great* and should be used in any situation that requires collections, they implement some code that is not well suited for performance-critical applications such as games or memory-critical
environments such as Android or (again) games.

After working on some code where I needed lists and maps that were not as heavy as the regular JCF ones, I decided to implement my own collections mini-framework with a few concerns in mind:

+ `Iterator`s should not be created constantly.
  On games, some collections are iterated multiple times per frame, which results in hundreds of iterations per second.
  If every iteration adds an object to the heap, the GC will have to run more frequently, severely impacting performance.

+ `Iterator`s should not be safe if it impacts performance.
  Most iterations do a single full pass on a collection and are then forgotten.
  Thread safety or fail-fast are of little value in a single-threaded environment and users should take care themselves of not modifying the original collection while iterating.

+ `HashMap`s should be cache-friendly.
  The JCF implementation is *open*, meaning that elements are not stored on the hash table itself, but on linked lists referenced from that table.
  This adds objects to the heap every time a new key is inserted, and increases indirection, making cache misses more frequent.
  By using a closed hash table, we eliminate both problems.
  There's a potential increase of computational complexity for high load factors because addresses on the table are reused for other keys, but that should not be noticeable if the whole table is cached.

+ A direct implementation of `Collection` is necessary for those situations where you just want to keep elements in some kind of structure but order is not important and you *need* fast traversal and removal times.

That's how Low-End Collections was born, and those are the core principles.


Particularities
---------------

As it has been already mentioned, there are a number of particularities that you must know about when using this library.
Most of the usage is equivalent to the JCF so you can feel at home.


### Collection Sizes

In JCF, the `size()` method is defined so that it returns the number of elements in the collection *or `Integer.MAX_VALUE` if there are more*. This is *not* valid on LEC.

Instead, a new method is included, `isFull()`, that tells us if the collection is full and therefore doesn't allow any more elements.
All `add` methods must fail if the collection is full.

This means that, in LEC, `size()` returns the *exact* number of elements in the collection and that adding elements beyond `Integer.MAX_VALUE` is not pemitted under any circumstances, even for structures that technically allow it.

Note however that it would make little sense to have `Integer.MAX_VALUE` elements, as it will use *at least* 2<sup>35</sup> bytes of memory just for the pointers. That's 32 GiB for just a collection of pointers. Add data to the mix and and you have an unrealistic situation where a single collection is using more than 100 GiB of memory.


### Map Simplicity

In JCF, the `Map` interface is *really* complex, mainly because of the introduction of *key sets*, *value collections* and *entry sets*. Although they are really useful in a lot of situations, LEC does not include them.

Instead, LEC makes a `LowEndMap<K, V>` an subinterface of `Iterable<LowEndMap.Entry<K, V>>`, so maps can be iterated as if they were collections of mappings.

Entries are simplified as well, so no `setValue` is available.
*NOTE: This may change in the future, as I think `setValue` is a nice way of modifying a mapping as it's iterated*.


### Shared Iterators

In LEC, there are two ways of getting an iterator from a collection or a map: `iterator()` and `newIterator()`.

The `newIterator` method is the equivalent of the JCF `iterator`: It returns a `new`ly allocated iterator that nobody else will get, and will be GC'd when you stop using it.
In the case of maps, these iterators will also return `new`ly allocated entries.

The `iterator` method, however, returns a *shared* or *cached* iterator.
That is, multiple invocations of `iterator` will keep returning the same iterator(s) again and again, properly resetted so you don't really notice or care about it. There are some implications on this approach:

+ You can't nest loops more times that iterators are cached.
  For example, suppose `coll` is a collection that keeps only one iterator.

      // Calls iterator() on coll to return a new iterator
      for (Object elem1 : coll) {
          // Calls iterator() on coll and returns *THE SAME* iterator
          for (Object elem2 : coll) {
              // Some code
          }
      }

  This code is supposed to iterate the collection by getting every two-element combination possible.
  And in fact, it would if it either was a normal JCF collection or the collection had kept more than one iterator.

  Instead, that code works as follows:

  - The first loop is entered. `coll.iterator()` is called.
  - The first iteration of the loop sets `elem1` to the first element of the collection.
  - The second loop is entered. `coll.iterator()` is called, so the same iterator is returned and resetted.
  - The loop completes normally, `elem2` has been set to every item in the collection.
  - The first loop completes immediately: The iterator `hasNext()` method returns `false` because the second loop had already exhausted the iterator.

  To avoid this, use `newIterator()` if you need to nest loops beyond the number of cached iterators.
  All implementations of LEC interfaces should state clearly how many loops can be nested.
  Standard implementations are implemented so that as much as 3 loops can be nested, so the above code would be correct *if performed over a standard collection*.

+ `LowEndMap` shared iterators also return shared entries.
  When you call `next` on a shared map iterator, the same object is always returned for that iterator.
  Hence, these entries should not be stored for future use, as they would always be the same and reference the last iterated mapping.

  Note that this behaviour is *not* the same for non-shared iterators.
  Iterators returned by `LowEndMap#newIterator()` return new entries, although they are *not* views of the mapping as in JCF maps.

+ Iterating with shared iterators, however, is not as bad as it looks.
  You get one advantage you *need* on memory-critical applications:
  *There are no memory allocations derived from collection iteration*, except for the first few ones.
  This can save you a few GC's *per second*.


Interfacing with JCF
--------------------

Although Low-End Collections classes and interfaces do not directly implement or extend the JCF ones, there's still hope if you need to pass a LEC as a regular Java collection: the `LowEndCollections` utility class.

The `LowEndCollections` class offers a static method for every LEC interface that returns a regular JCF of the same kind. The wrapped collections have the following properties:

+ The `iterator` method of the collections returned by `toJavaCollection`, `toJavaList` and `toJavaSet` call the wrapped collection's `newIterator` method, hiding away the concept of shared iterators and letting clients use the collections as actual regular JCF collections.

+ Similarly, the `listIterator` method of the lists returned by `toJavaList` calls the wrapped list's `newListIterator` method.

+ If a `LowEndList` implements `RandomAccess`, the JCF `List` returned by `toJavaList` also implements `RandomAccess`.

+ Maps returned by `toJavaMap` implement their `entrySet`s by creating an `AbstractSet` that returns as their `iterator` a wrapped version of the original map's `newIterator`.
