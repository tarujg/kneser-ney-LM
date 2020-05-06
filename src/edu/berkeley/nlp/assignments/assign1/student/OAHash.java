package edu.berkeley.nlp.assignments.assign1.student;

import java.util.Arrays;
import java.util.Iterator;

public class OAHash {

    private long[] keys;

    private int[] values;

    private final long EMPTY_KEY = -1;

    public void incrementkey(long k) {
        int pos = getInitialPos(k, keys);
        long curr = keys[pos];
        while ((curr != EMPTY_KEY) && (curr!=k)) {
            pos++;
            if (pos == keys.length) pos = 0;
            curr = keys[pos];
        }

        values[pos] += 1;
        if (curr == EMPTY_KEY) {
            keys[pos] = k;
        }
    }

    public OAHash(int initialCapacity_) {
        this(initialCapacity_, 0.7);
    }

    public OAHash(int initialCapacity_, double loadFactor) {
        int cap = Math.max(5, (int) (initialCapacity_ / loadFactor));
        values = new int[cap];
        keys = new long[cap];
        Arrays.fill(values, 0);
        Arrays.fill(keys, EMPTY_KEY);
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
        return values[find(k)];
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

    public class Iter extends MapIterator {
        public Object next() {
            return nextIndex();
        }
    }

    public Iter getIterator(){
        return new Iter();
    }

}
