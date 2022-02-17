package com.runtimeverification.rvmonitor.util;

public class Pair<A, B> {
    private final A left;
    private final B right;

    public Pair(A a, B b) {
        this.left = a;
        this.right = b;
    }

    public A getLeft() {
        return left;
    }

    public B getRight() {
        return right;
    }
}
