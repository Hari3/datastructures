package io.github.boredmathematician.datastructures;

import java.util.ArrayDeque;

public class MinDeque<T extends Comparable<T>> {

    private final ArrayDeque<T> deque = new ArrayDeque<>();
    private final ArrayDeque<T> min = new ArrayDeque<>();
    private int size = 0;
    int size() {
        return size;
    }

    void push(T element) {
        deque.addLast(element);
        size++;
        while (!min.isEmpty() && min.peekLast().compareTo(element) > 0)
            min.removeLast();
        min.addLast(element);
    }

    T pop() {
        if (deque.isEmpty()) {
            assert min.isEmpty();
            return null;
        }

        assert !min.isEmpty();

        if (min.peekFirst().equals(deque.peekFirst()))
            min.removeFirst();

        size--;
        return deque.removeFirst();
    }

    T getMin() {
        return min.peekFirst();
    }
}
