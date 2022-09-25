package bstmap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* Using loop as far as possible*/
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

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

        private List<BSTNode> nodesInOrder() {
            List<BSTNode> keys = new ArrayList<>();
            if (left != null) {
                keys.addAll(left.nodesInOrder());
            }
            keys.add(this);
            if (right != null) {
                keys.addAll(right.nodesInOrder());
            }
            return keys;
        }
    }

    private BSTNode root;

    private int size;

    public BSTMap() {
        clear();
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        if (this.root == null) {
            return false;
        }
        Object[] obj = bSearch(this.root, key);
        return obj[2].equals(0);
    }

    @Override
    public V get(K key) {
        if (this.root == null) {
            return null;
        }
        Object[] obj = bSearch(this.root, key);
        BSTNode node = (BSTNode) obj[1];
        if (obj[2].equals(0)) {
            return node.val;
        }
        return null;
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public void put(K key, V value) {
        if (this.root == null) {
            this.root = new BSTNode(key, value);
            this.size++;
            return;
        }
        BSTNode node = this.root;
        Object[] obj = bSearch(node, key);
        node = (BSTNode) obj[1];
        if (obj[2].equals(0)) {
            return;
        }
        if (obj[2].equals(-1)) {
            node.left = new BSTNode(key, value);
        } else {
            node.right = new BSTNode(key, value);
        }
        this.size++;
    }

    @Override
    public Set<K> keySet() {
        if (this.size == 0) {
            return null;
        }
        List<K> keys = new ArrayList<>();
        for (BSTNode node : this.root.nodesInOrder()) {
            keys.add(node.key);
        }
        return Set.copyOf(keys);
    }

    @Override
    public V remove(K key) {
        Object[] obj = bSearch(this.root, key);
        BSTNode prev = (BSTNode) obj[0];
        BSTNode node = (BSTNode) obj[1];
        BSTNode rm = node;
        V ret = rm.val;
        if (node == this.root) {
            removeRoot(node, rm);
        } else if (node.left == null && node.right == null) {
            if (obj[2].equals(-1)) {
                prev.left = null;
            } else {
                prev.right = null;
            }
        } else {
            node = findRightMostLeft(node, rm);
            if (obj[2].equals(-1)) {
                prev.left = node;
            } else {
                prev.right = node;
            }
        }
        this.size--;
        return ret;
    }

    @Override
    public V remove(K key, V value) {
        return remove(key);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }

    public void printInOrder() {
        StringBuilder s = new StringBuilder();
        for (BSTNode node : this.root.nodesInOrder()) {
            s.append(node.val.toString());
        }
        System.out.print(s);
    }

    private Object[] bSearch(BSTNode node, K key) {
        BSTNode prev = null;
        while (true) {
            if (node.key.compareTo(key) > 0) {
                if (node.left != null) {
                    prev = node;
                    node = node.left;
                } else {
                    // next will be left child
                    return new Object[]{prev, node, -1};
                }
            } else if (node.key.compareTo(key) < 0) {
                if (node.right != null) {
                    prev = node;
                    node = node.right;
                } else {
                    // next will be right child
                    return new Object[]{prev, node, 1};
                }
            } else {
                // reach the target
                return new Object[]{prev, node, 0};
            }
        }
    }

    private BSTNode findRightMostLeft(BSTNode node, BSTNode rm) {
        if (node.left == null) {
            node = node.right;
        } else if (node.right == null) {
            node = node.left;
        } else {
            node = node.left;
            if (node.right != null) {
                BSTNode pn = null;
                while (node.right != null) {
                    pn = node;
                    node = node.right;
                }
                pn.right = node.left;
                node.left = rm.left;
            }
            node.right = rm.right;
        }
        return node;
    }

    private void removeRoot(BSTNode node, BSTNode rm) {
        if (node.left != null) {
            node = findRightMostLeft(node, rm);
            node.right = rm.right;
            this.root = rm.left;
        } else {
            this.root = this.root.right;
        }
    }
}
