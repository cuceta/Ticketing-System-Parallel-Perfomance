package org.example;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ThreadSafeHashMap {
    private static class Node {

        final int key;
        boolean value;

        Node(int key, boolean value) {
            this.key = key;
            this.value = value;
        }
    }

    private final LinkedList<Node>[] buckets;
    private final ReadWriteLock[] locks;
    private final int capacity;

    public ThreadSafeHashMap(int capacity) {
        this.capacity = capacity;
        this.buckets = new LinkedList[capacity];
        this.locks = new ReentrantReadWriteLock[capacity];
        for (int i = 0; i < capacity; i++) {
            buckets[i] = new LinkedList<>();
            locks[i] = new ReentrantReadWriteLock();
        }
    }

    private int getBucketIndex(int key) {
        return Integer.hashCode(key) % capacity;
    }

    public void put(int key, boolean value) {
        int index = getBucketIndex(key);
        locks[index].writeLock().lock();
        try {
            LinkedList<Node> bucket = buckets[index];
            for (Node node : bucket) {
                if (node.key == key) {
                    node.value = value;  // Update existing key
                    return;
                }
            }
            bucket.add(new Node(key, value));  // Add new key
        } finally {
            locks[index].writeLock().unlock();
        }
    }

    public Boolean get(int key) {
        int index = getBucketIndex(key);
        locks[index].readLock().lock();
        try {
            LinkedList<Node> bucket = buckets[index];
            for (Node node : bucket) {
                if (node.key == key) {
                    return node.value;
                }
            }
            return null;  // Key not found
        } finally {
            locks[index].readLock().unlock();
        }
    }

    public Boolean remove(int key) {
        int index = getBucketIndex(key);
        locks[index].writeLock().lock();
        try {
            LinkedList<Node> bucket = buckets[index];
            for (Node node : bucket) {
                if (node.key == key) {
                    bucket.remove(node);
                    return true;
                }
            }
            return false;  // Key not found
        } finally {
            locks[index].writeLock().unlock();
        }
    }
}
