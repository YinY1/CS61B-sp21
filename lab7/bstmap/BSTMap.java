package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable, V> implements Map61B {

    private class BSTNode {
        private final K key;
        private final V val;
        private BSTNode left;
        private BSTNode right;

        private BSTNode(K key, V value) {
            this.key = key;
            this.val = value;
            this.left = null;
            this.right = null;
        }

        private void printInOrder(){
            if(left!=null){
                left.printInOrder();
            }
            System.out.print(val);
            if(right!=null){
                right.printInOrder();
            }
        }
    }

    private BSTNode root;

    private int size;

    public BSTMap() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        BSTNode node = this.root;
        while (node != null) {
            if (node.key.compareTo(key) > 0) {
                node = node.left;
            } else if (node.key.compareTo(key) < 0) {
                node = node.right;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        if (this.root == null) {
            return null;
        }
        BSTNode node = this.root;
        while (true) {
            if (node.key.compareTo(key) > 0) {
                if(node.left!=null){
                    node = node.left;
                } else {
                    return null;
                }
            } else if (node.key.compareTo(key) < 0) {
                if(node.right != null ){
                    node = node.right;
                } else {
                    return null;
                }
            } else {
                return node.val;
            }
        }
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(Object key, Object value) {
        if (this.root == null) {
            this.root = new BSTNode((K) key, (V) value);
            this.size++;
            return;
        }
        BSTNode node = this.root;
        while (true) {
            if (node.key.compareTo(key) > 0) {
                if (node.left != null) {
                    node = node.left;
                } else {
                    node.left = new BSTNode((K) key, (V) value);
                    this.size++;
                    return;
                }
            } else if (node.key.compareTo(key) < 0) {
                if (node.right != null) {
                    node = node.right;
                } else {
                    node.right = new BSTNode((K) key, (V) value);
                    this.size++;
                    return;
                }
            } else {
                return;
            }
        }
    }


    @Override
    public Set keySet() throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Object remove(Object key) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Object remove(Object key, Object value) throws UnsupportedOperationException {
        return null;
    }

    @Override
    public Iterator iterator() throws UnsupportedOperationException {
        return null;
    }

    public void printInOrder(){
        this.root.printInOrder();
    }
}
