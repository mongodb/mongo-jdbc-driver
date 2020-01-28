package com.mongodb.jdbc;

public class Pair <L, R> {
    private L left;
	private R right;

	public Pair(L left, R right) {
        this.left = left;
		this.right = right;
	}

	public L left() {
        return left;
	}

	public R right() {
        return right;
	}
}
