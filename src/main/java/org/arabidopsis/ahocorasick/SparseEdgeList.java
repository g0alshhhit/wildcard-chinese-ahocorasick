package org.arabidopsis.ahocorasick;

import net.openhft.koloboke.collect.map.hash.HashCharObjMap;
import net.openhft.koloboke.collect.map.hash.HashCharObjMaps;

/**
 * Linked list implementation of the EdgeList should be less memory-intensive.
 */
class SparseEdgeList implements EdgeList {
    private int n = 0;
    private final static int HASH_LIMIT = 6;

    private HashCharObjMap<State> map = null;
    private Cons head;

    public SparseEdgeList() {
        head = null;
    }

    @Override
    public State get(char b) {

        if (map != null) {
            return map.get(b);
        }
        Cons c = head;
        while (c != null) {
            if (c.b == b)
                return c.s;
            c = c.next;
        }

        return null;
    }

    @Override
    public void put(char b, State s) {
        n++;
        if (n < HASH_LIMIT) {
            this.head = new Cons(b, s, head);
            return;
        }
        else if (n == HASH_LIMIT) {
            map = buildMaps();
            head = null;
            map.put(b, s);
            return;
        } else {
            map.put(b, s);
            return;
        }

    }


    /**
     * @author wuyue
     * 
     * @date Feb 10, 2015 5:44:12 PM
     */
    private HashCharObjMap<State> buildMaps() {
        map = HashCharObjMaps.newMutableMap();
        Cons c = head;
        while (c != null) {
            map.put(c.b, c.s);
            c = c.next;
        }
        return map;
    }

    @Override
    public char[] keys() {

        if (map == null) {
            int length = 0;
            Cons c = head;
            while (c != null) {
                length++;
                c = c.next;
            }

            char[] result = new char[length];
            c = head;
            int j = 0;
            while (c != null) {
                result[j] = c.b;
                j++;
                c = c.next;
            }

            return result;
        } else {
            char[] result = new char[map.size()];
            int i = 0;
            for (char c : map.keySet()) {
                result[i] = c;
                i++;
            }
            return result;
        }
    }

    static private class Cons {
        char b;
        State s;
        Cons next;

        public Cons(char b, State s, Cons next) {
            this.b = b;
            this.s = s;
            this.next = next;
        }
    }
}
