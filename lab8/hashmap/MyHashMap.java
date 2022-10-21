package hashmap;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A hash table-backed Map implementation. Provides amortized constant time
 * access to elements via get(), remove(), and put() in the best case.
 * <p>
 * Assumes null keys will never be inserted, and does not resize down upon remove().
 *
 * @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    private final double loadFactor;
    private int size;
    private int length;
    /* Instance Variables */
    private Collection<Node>[] buckets;

    /**
     * Constructors
     */
    public MyHashMap() {
        size = 16;
        loadFactor = 0.75;
        length = 0;
        buckets = createTable(size);
    }

    public MyHashMap(int initialSize) {
        size = initialSize;
        loadFactor = 0.75;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad     maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        size = initialSize;
        loadFactor = maxLoad;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     * <p>
     * The only requirements of a hash table bucket are that we can:
     * 1. Insert items (`add` method)
     * 2. Remove items (`remove` method)
     * 3. Iterate through items (`iterator` method)
     * <p>
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     * <p>
     * Override this method to use different data structures as
     * the underlying bucket type
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new HashSet<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     * <p>
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        return new Collection[tableSize];
    }


    /**
     * Removes all of the mappings from this map.
     */
    @Override
    public void clear() {
        size = 16;
        length = 0;
        buckets = createTable(size);
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     *
     * @param key
     */
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this
     * map contains no mapping for the key.
     *
     * @param key
     */
    @Override
    public V get(K key) {
        int pos = getPosition(key);
        Collection<Node> set = buckets[pos];
        if (set == null) {
            return null;
        }
        for (Node p : set) {
            if (p.key.equals(key)) {
                return p.value;
            }
        }
        return null;
    }

    /**
     * Returns the number of key-value mappings in this map.
     */
    @Override
    public int size() {
        return length;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * If the map previously contained a mapping for the key,
     * the old value is replaced.
     *
     * @param key
     * @param value
     */
    @Override
    public void put(K key, V value) {
        if (isOverload()) {
            reSize();
        }
        Node p = createNode(key, value);
        int pos = getPosition(key);
        if (buckets[pos] == null) {
            buckets[pos] = createBucket();
        }
        for (Node node : buckets[pos]) {
            if (node.key == key) {
                node.value = value;
                return;
            }
        }
        buckets[pos].add(p);
        length++;
    }

    /**
     * Returns a Set view of the keys contained in this map.
     */
    @Override
    public Set<K> keySet() {
        if (length == 0) {
            return null;
        }
        Set<K> ret = new HashSet<>();
        for (K k : this) {
            ret.add(k);
        }
        return ret;
    }

    /**
     * Removes the mapping for the specified key from this map if present.
     * Not required for Lab 8. If you don't implement this, throw an
     * UnsupportedOperationException.
     *
     * @param key
     */
    @Override
    public V remove(K key) {
        int pos = getPosition(key);
        Collection<Node> set = buckets[pos];
        if (set == null) {
            return null;
        }
        for (Node p : set) {
            if (p.key.equals(key)) {
                set.remove(p);
                return p.value;
            }
        }
        return null;
    }

    /**
     * Removes the entry for the specified key only if it is currently mapped to
     * the specified value. Not required for Lab 8. If you don't implement this,
     * throw an UnsupportedOperationException.
     *
     * @param key
     * @param value
     */
    @Override
    public V remove(K key, V value) {
        int pos = getPosition(key);
        Node p = createNode(key, value);
        Collection<Node> set = buckets[pos];
        if (set == null || !set.contains(p)) {
            return null;
        }
        buckets[pos].remove(p);
        return value;
    }
    // You should probably define some more!

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @Override
    public Iterator<K> iterator() {
        return new Iterator<>() {
            private final Collection<Node>[] b = buckets;
            private int pos = findPos(0);

            private Collection<Node> curBuck = b[pos];
            private Iterator<Node> curIter = curBuck.iterator();

            private int findPos(int cur) {
                int pos = cur;
                while (pos < size && b[pos] == null) {
                    pos++;
                }
                return pos;
            }

            @Override
            public boolean hasNext() {
                return curIter.hasNext() || findPos(pos + 1) < size;
            }

            @Override
            public K next() {
                if (curIter.hasNext()) {
                    Node curNode = curIter.next();
                    return curNode.key;
                }
                pos = findPos(pos + 1);
                curBuck = b[pos];
                curIter = curBuck.iterator();
                return curIter.next().key;
            }
        };
    }

    private void reSize() {
        MyHashMap<K, V> temp = new MyHashMap<>(size * 2);
        for (K key : this) {
            temp.put(key, get(key));
        }
        size *= 2;
        buckets = temp.buckets;
    }

    private boolean isOverload() {
        return (double) length / size >= loadFactor;
    }

    private int getPosition(K key) {
        return Math.floorMod(key.hashCode(), size);
    }

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }
}
