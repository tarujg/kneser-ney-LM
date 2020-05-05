package edu.berkeley.nlp.assignments.assign1.student;

import edu.berkeley.nlp.util.StringIndexer;

import java.util.Arrays;
import java.util.Iterator;

public class OpenHash {

    private long[] keys;

    private int[] values;

    private int size = 0;

    private final long EMPTY_KEY = -1;//null;

    private final double MAX_LOAD_FACTOR;

    public boolean put(long k, int v) {
//        if (size / (double) keys.length > MAX_LOAD_FACTOR) {
//            rehash();
//        }
        return putHelp(k, v, keys, values);

    }

    public OpenHash() { this(10); }

    public OpenHash(int initialCapacity_) {
        this(initialCapacity_, 0.7);
    }

    public OpenHash(int initialCapacity_, double loadFactor) {
        int cap = Math.max(5, (int) (initialCapacity_ / loadFactor));
        MAX_LOAD_FACTOR = loadFactor;
        values = new int[cap];
        keys = new long[cap];
        Arrays.fill(values, 0);
        Arrays.fill(keys, EMPTY_KEY);
    }

    public int size() {
        return size;
    }

    /**
     * @param k
     * @param v
     */
    private boolean putHelp(long k, int v, long[] keyArray, int[] valueArray) {
        int pos = getInitialPos(k, keyArray);
        long curr = keyArray[pos];
        while ((curr != EMPTY_KEY) && (curr!=k)) {
            pos++;
            if (pos == keyArray.length) pos = 0;
            curr = keyArray[pos];
        }

        valueArray[pos] += 1; //= v;
        if (curr == EMPTY_KEY) {
            size++;
            keyArray[pos] = k;
            return true;
        }
        return false;
    }

    /**
     * @param k
     * @param keyArray
     * @return
     */
    private int getInitialPos(long k, long[] keyArray) {
        long hash = (k ^ (k >>> 32)) * 3875239;
        int pos = (int) (hash % keyArray.length);
        if (pos < 0) pos += keyArray.length;
        return pos;
    }

    public int get(long k) {
        int pos = find(k);
        return values[pos];
    }

    /**
     * @param k
     * @return
     */
    private int find(long k) {
        int pos = getInitialPos(k, keys);
        long curr = keys[pos];
        while (curr != EMPTY_KEY && curr!=k) {
            pos++;
            if (pos == keys.length) pos = 0;
            curr = keys[pos];
        }
        return pos;
    }

    private abstract class MapIterator implements Iterator
    {
        public MapIterator() {
            end = keys.length;
            next = -1;
        }

        public boolean hasNext() {
            return next < end;
        }

        long nextIndex() {

            do {
                next++;
            } while (next < end && keys[next] == EMPTY_KEY);
            if (next==end) return 0;
            return keys[next];
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

        private int next, end;
    }

    public class MyIterator extends MapIterator{
        public Object next(){
            return nextIndex();
        }

    }

    public MyIterator getIterator(){
        MyIterator it = new MyIterator();
        return it;
    }

}
