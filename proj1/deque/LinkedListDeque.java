package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private int size;
    private final Node sentinel;

    private class Node {
        private Node prev;
        private Node next;
        private T elem;
    }

    public LinkedListDeque() {
        this.sentinel = new Node();
        this.sentinel.next = this.sentinel;
        this.sentinel.prev = this.sentinel;
        this.sentinel.elem = null;
        this.size = 0;
    }

    @Override
    public void addFirst(T item) {
        Node p = new Node();
        p.elem = item;
        p.next = sentinel.next;
        p.prev = sentinel;
        p.next.prev = p;
        sentinel.next = p;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node p = new Node();
        p.elem = item;
        p.next = sentinel;
        p.prev = sentinel.prev;
        p.prev.next = p;
        sentinel.prev = p;
        size++;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void printDeque() {
        Node p = sentinel.next;
        while (p.next != sentinel) {
            System.out.print(p.elem + " ");
            p = p.next;
        }
        System.out.println();
    }

    @Override
    public T removeFirst() {
        if (sentinel.next == sentinel) {
            return null;
        }
        Node p = sentinel.next;
        sentinel.next = p.next;
        p.next.prev = sentinel;
        size -= 1;
        return p.elem;
    }

    @Override
    public T removeLast() {
        if (sentinel.prev == sentinel) {
            return null;
        }
        Node p = sentinel.prev;
        sentinel.prev = p.prev;
        p.prev.next = sentinel;
        size -= 1;
        return p.elem;
    }

    @Override
    public T get(int index) {
        Node p = sentinel.next;
        while (index != 0 && p != sentinel) {
            p = p.next;
            index -= 1;
        }
        if (index == 0) {
            return p.elem;
        }
        return null;
    }

    public T getRecursive(int index) {
        Node p = sentinel.next;
        if (index != 0 && p == sentinel) {
            return null;
        }
        if (index == 0) {
            return p.elem;
        }
        LinkedListDeque<T> l = new LinkedListDeque<>();
        l.sentinel.next = p.next;
        return l.getRecursive(index - 1);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Deque) || ((Deque<?>) o).size() != this.size()) {
            return false;
        }
        if (o == this) {
            return true;
        }
        for (int i = 0; i < this.size(); i++) {
            Object item = ((Deque<?>) o).get(i);
            if (!(this.get(i).equals(item))) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private Node node;

        LinkedListIterator() {
            this.node = sentinel.next;
        }

        public boolean hasNext() {
            return this.node != sentinel;
        }

        public T next() {
            T ret = this.node.elem;
            this.node = this.node.next;
            return ret;
        }

    }
}
