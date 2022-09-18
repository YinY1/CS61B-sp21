package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private int length;
    private int size;
    private int first;
    private int last;
    private T[] arr;

    public ArrayDeque() {
        this.arr = (T[]) new Object[8];
        this.size = 8;
        this.length = 0;
        this.first = 0;
        this.last = 0;
    }

    @Override
    public void addFirst(T item) {
        if (this.length == this.size - 1) {
            reSize(this.size * 2);
        }
        if (!isEmpty()) {
            this.first = (this.first - 1 + this.size) % this.size;
        }
        this.arr[first] = item;
        this.length++;
    }

    @Override
    public void addLast(T item) {
        if (this.length == this.size - 1) {
            reSize(this.size * 2);
        }
        if (!isEmpty()) {
            this.last = (this.last + 1) % this.size;
        }
        this.arr[this.last] = item;
        this.length++;
    }

    @Override
    public int size() {
        return this.length;
    }

    @Override
    public void printDeque() {
        int i = this.first;
        while (i != this.last) {
            System.out.print(this.arr[i] + " ");
            i = (i + 1) % this.size;
        }
        System.out.println(this.arr[i]);
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        this.length--;
        T ret = this.arr[this.first];
        this.first = (this.first + 1) % this.size;
        if (this.length * 4 < this.size) {
            reSize(this.size / 2);
        }
        return ret;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        this.length--;
        T ret = this.arr[this.last];
        this.last = (this.last - 1 + this.size) % this.size;
        if (this.length * 4 < this.size) {
            reSize(this.size / 2);
        }
        return ret;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        int position = (index + this.first) % this.size;
        return this.arr[position];
    }

    private void reSize(int s) {
        T[] temp = (T[]) new Object[s];
        int i = this.first;
        int j = 0;
        while (j < this.length) {
            temp[j] = this.arr[i];
            i = (i + 1) % this.size;
            j++;
        }
        this.size = s;
        this.first = 0;
        if (isEmpty()) {
            this.last = 0;
        } else {
            this.last = j - 1;
        }
        this.arr = temp;
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
            if (this.get(i) != ((Deque<?>) o).get(i)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new Iterable();
    }

    private class Iterable implements Iterator<T> {
        private int pos;

        Iterable() {
            this.pos = 0;
        }

        public boolean hasNext() {
            return this.pos < length;
        }

        public T next() {
            T ret = get(this.pos);
            this.pos++;
            return ret;
        }
    }
}
