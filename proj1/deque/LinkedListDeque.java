package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T> {
    private int size;
    private final Node sentinel;

    private class Node {
        public Node prev;
        public Node next;
        public T elem;
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

    public boolean equals(Object o) {
        if (!(o instanceof LinkedListDeque) || this.size != ((LinkedListDeque<?>) o).size) {
            return false;
        }
        Iterator<T> itp = iterator();
        Iterator<T> itq = (Iterator<T>) ((LinkedListDeque<?>) o).iterator();
        while (itp.hasNext()) {
            if (itp.next() != itq.next()) {
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

        public LinkedListIterator() {
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
