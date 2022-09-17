package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    protected int length;
    private int size;
    protected int first;
    protected int last;
    private T[] arr;

    public ArrayDeque() {
        this.arr = (T[]) new Object[8];
        this.size = 8;
        this.length = 0;
        this.first = 0;
        this.last = 0;
    }

    public void addFirst(T item) {
        if (this.length == this.size - 1) {
            ReSize(this.size * 2);
        }
        if (!isEmpty()) {
            this.first = (this.first - 1 + this.size) % this.size;
        }
        this.arr[first] = item;
        this.length++;
    }

    public void addLast(T item) {
        if (this.length == this.size - 1) {
            ReSize(this.size * 2);
        }
        if (!isEmpty()) {
            this.last = (this.last + 1) % this.size;
        }
        this.arr[this.last] = item;
        this.length++;
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public int size() {
        return this.size;
    }

    public void printDeque() {
        int i = this.first;
        while (i != this.last) {
            System.out.print(this.arr[i] + " ");
            i = (i + 1) % this.size;
        }
        System.out.println(this.arr[i]);
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        this.length--;
        T ret = this.arr[this.first];
        this.first = (this.first + 1) % this.size;
        return ret;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        this.length--;
        T ret = this.arr[this.last];
        this.last = (this.last - 1 + this.size) % this.size;
        return ret;
    }

    public T get(int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        int position = (index + this.first) % this.size;
        return this.arr[position];
    }

    private void ReSize(int size) {
        T[] temp = (T[]) new Object[size];
        int i = this.first;
        int j = 0;
        while (j < this.length) {
            temp[j] = this.arr[i];
            i = (i + 1) % this.size;
            j++;
        }
        this.size = size;
        this.first = 0;
        this.last = j - 1;
        this.arr = temp;
    }

    public boolean equals(Object o) {
        if (!(o instanceof ArrayDeque) || ((ArrayDeque<?>) o).length != this.length) {
            return false;
        }
        for (int i = 0; i < this.length; i++) {
            if (this.get(i) != ((ArrayDeque<?>) o).get(i)) {
                return false;
            }
        }
        return true;
    }

    public Iterator<T> iterator() {
        return new ArrayListIterator();
    }

    private class ArrayListIterator implements Iterator<T> {
        private int pos;

        public ArrayListIterator() {
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
