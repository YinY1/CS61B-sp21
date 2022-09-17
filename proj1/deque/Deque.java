package deque;

public interface Deque<T> {
    public void addFirst(T item);

    public void addLast(T item);

    default boolean isEmpty() {
        return this.size() == 0;
    }

    public int size();

    public void printDeque();

    public T removeFirst();

    public T removeLast();

    public T get(int index);
}
