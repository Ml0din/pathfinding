package com.mladin.pathfinding.pathfind;

public class PathPoint {
    protected int x;
    protected int z;

    public PathPoint(final int x, final int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
