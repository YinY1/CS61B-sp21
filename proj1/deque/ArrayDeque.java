package deque;

public class ArrayDeque<T> {
    private int length;
    private int size;
    private int next_first;
    private int next_last;
    T[] arr;

    public ArrayDeque() {
        T[] arr = (T[]) new Object[8];
        this.size = 8;
        this.length = 0;
        this.next_first = 0;
        this.next_last = 1;
    }

    public void addFirst(T item) {
        // TODO: treat it as circular
        if (this.length == this.size - 1) {
            ReSize(this.size * 2);
        }
        /*T[] temp = (T[]) new Object[this.size];
        System.arraycopy(this.arr, 0, temp, 1, this.length);
        temp[0] = item;
        this.length += 1;
        System.arraycopy(temp, 0, this.arr, 0, this.length);*/
        this.arr[this.next_first] = item;
        if (this.next_first > 0) {
            this.next_first -= 1;
        } else {
            this.next_first = this.size - 1;
        }
        this.length += 1;
    }

    public void addLast(T item) {
        if (this.length == this.size - 1) {
            ReSize(this.size * 2);
        }
        /*this.arr[this.length] = item;*/
        this.arr[this.next_last] = item;
        if (this.next_last < this.size - 1) {
            this.next_last += 1;
        } else {
            this.next_last = 0;
        }
        this.length += 1;
    }

    public boolean isEmpty() {
        return this.length == 0;
    }

    public int size() {
        return this.size;
    }

    public void printDeque() {
        int cnt = 0;
        int i;
        if (this.next_first == 0 || this.next_first == this.size - 1) {
            i = 0;
        } else {
            i = this.next_first + 1;
        }
        while (cnt < this.length) {
            System.out.print(this.arr[i] + " ");
            if (i == this.size - 1) {
                i = 0;
            } else {
                i += 1;
            }
            cnt += 1;
        }
        System.out.println();
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        this.length -= 1;
        if (this.next_first == 0 || this.next_first == this.size - 1) {
            this.next_first = 0;
            return this.arr[0];
        }
        /*T ret = this.arr[0];
        T[] temp = (T[]) new Object[this.size];
        System.arraycopy(this.arr, 1, temp, 0, this.length);
        this.arr = temp;*/
        this.next_first += 1;
        return this.arr[next_first];
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }
        this.length -= 1;
        this.next_last -= 1;
        return this.arr[next_last + 1];
        /*T ret = this.arr[length - 1];
        this.arr[length - 1] = null;
        this.length -= 1;
        if (this.length * 4 < this.size) {
            ReSize(this.size / 2);
        }*/
    }

    public T get(int index) {
        if (index < 0 || index >= this.length) {
            return null;
        }
        return this.arr[index];
    }

    private void ReSize(int size) {
        T[] temp = (T[]) new Object[size];
        this.size = size;
        System.arraycopy(this.arr, 0, temp, 0, size);
        this.arr = temp;
    }

    // TODO: iterator
}
