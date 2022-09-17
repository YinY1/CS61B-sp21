package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private final Comparator<T> c;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        this.c = c;
    }

    public T max() {
        return getMax(this.c);
    }

    public T max(Comparator<T> c){
        return getMax(c);
    }

    private T getMax(Comparator<T> c) {
        if(this.length == 0){
            return null;
        }
        T m = null;
        for(int i =0;i<this.length;i++){
            T temp = this.get(i);
            if(c.compare(m,temp)<0){
                m = temp;
            }
        }
        return m;
    }
}
