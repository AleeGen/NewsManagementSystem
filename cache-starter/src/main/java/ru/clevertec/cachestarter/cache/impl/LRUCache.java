package ru.clevertec.cachestarter.cache.impl;

import ru.clevertec.cachestarter.cache.Cache;

import java.util.HashMap;
import java.util.Map;

/**
 * A cache class that is implemented using the LRU algorithm based on the "least frequently used" principle.
 * Using maps and LinkedSet allows you to achieve a constant execution time for all operations
 *
 * @autor Alexey Leonenko
 * @see Cache
 */
public class LRUCache<K, V> implements Cache<K, V> {

    /**
     * Returns the cache capacity
     */
    private final int capacity;
    /**
     * Returns the cache current size
     */
    private int size;
    /**
     * Returns the head node
     */
    private LinkedNode head;
    /**
     * Returns the tail node
     */
    private LinkedNode tail;
    /**
     * Returns a map with all elements
     */
    private Map<K, LinkedNode> map;

    /**
     * The constructor initializes the cache {@link #capacity}, {@link #map}.
     * Configures the initial pointers of the {@link #tail} and {@link #head} nodes
     *
     * @param capacity capacity cache
     */
    public LRUCache(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        head = new LinkedNode();
        tail = new LinkedNode();
        head.next = tail;
        tail.prev = head;
    }

    public Map<K, LinkedNode> getMap() {
        return map;
    }

    public int getSize() {
        return size;
    }

    public LinkedNode getHead() {
        return head;
    }

    public LinkedNode getTail() {
        return tail;
    }

    /**
     * Returns cache capacity
     *
     * @return cache capacity
     */
    @Override
    public int getCapacity() {
        return capacity;
    }

    /**
     * Method to put an item in the cache. If the cache size is equal to its capacity,
     * then the most recently used element is deleted. In another case, the current
     * element is transferred to the head and its value is updated
     *
     * @param key   the key by which the cache elements are accessed
     * @param value the value that stores the cache by key
     */
    @Override
    public void put(K key, V value) {
        LinkedNode node = map.get(key);
        if (node != null) {
            node.value = value;
            remove(node);
            moveToHead(node);
        } else {
            node = new LinkedNode();
            node.value = value;
            node.key = key;
            map.put(key, node);
            moveToHead(node);
            size++;
            if (size > capacity) {
                map.remove(tail.prev.key);
                remove(tail.prev);
                size--;
            }
        }
    }

    /**
     * Returns the element by key and moves it to the head, if available
     *
     * @param key the key by which the cache elements are accessed
     * @return an element by key or null if no such element is found
     */

    @Override
    public V get(K key) {
        LinkedNode elem = map.get(key);
        if (elem == null) {
            return null;
        }
        remove(elem);
        moveToHead(elem);
        return elem.value;
    }

    /**
     * Deletes an item with the specified key in this cache.
     * Updates the pointers of neighboring elements
     *
     * @param key of the element to be removed
     */
    @Override
    public void delete(K key) {
        remove(map.get(key));
        map.remove(key);
    }

    /**
     * Clean all components
     */
    @Override
    public void clear() {
        map.clear();
        head = new LinkedNode();
        tail = new LinkedNode();
        head.next = tail;
        tail.prev = head;
        size = 0;
    }

    private void moveToHead(LinkedNode node) {
        LinkedNode temp = head.next;
        head.next = node;
        node.prev = head;
        node.next = temp;
        temp.prev = node;
    }

    private void remove(LinkedNode node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /**
     * Supportive nested class in the form of a doubly linked list
     */
    private class LinkedNode {
        K key;
        V value;
        LinkedNode prev;
        LinkedNode next;
    }

}