/*
 * Copyright (C) 2017 giffgaff All rights reserved
 */
package main;

public class Pair<K, V> {

    private final K x;
    private final V y;

    private Pair(K x, V y) {
        this.x = x;
        this.y = y;
    }

    public static <L, R> Pair of(L x, R y) {
        return new Pair<>(x, y);
    }

    public K getX() {
        return x;
    }

    public V getY() {
        return y;
    }

    public K getKey() {
        return x;
    }

    public V getValue() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pair point = (Pair) o;

        return x == point.x && y == point.y;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }
}
