package com.mongodb.jdbc;

public class Triple<L, M, R> {
    private L left;
    private M middle;
    private R right;

    public Triple(L left, M middle, R right) {
        this.left = left;
        this.middle = middle;
        this.right = right;
    }

    public L left() {
        return left;
    }

    public M middle() {
        return middle;
    }

    public R right() {
        return right;
    }
}
