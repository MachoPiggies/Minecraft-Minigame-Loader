package com.machopiggies.gameloaderapi.util;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.PriorityQueue;
import java.util.SortedSet;

public class CircularQueue<E> extends ArrayDeque<E> {

    public CircularQueue() {
        super();
    }

    public CircularQueue(int initialCapacity) {
        super(initialCapacity);
    }

    public CircularQueue(Collection<? extends E> c) {
        super(c);
    }

    public CircularQueue(PriorityQueue<? extends E> c) {
        super(c);
    }

    public CircularQueue(SortedSet<? extends E> c) {
        super(c);
    }

    @Override
    public E poll() {
        E item = super.poll();
        if (item == null) return null;
        add(item);
        return item;
    }
}
