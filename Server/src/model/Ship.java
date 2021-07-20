package model;

import java.util.LinkedList;

public class Ship {
    public enum Orientation {VERTICAL, HORIZONTAL}

    private final int length;
    private Orientation orientation;
    private int x,y;
    private boolean destroyed;

    public Ship(int length) {
        this.length = length;
        this.destroyed = false;
    }

    public int getLength() {
        return length;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void setDestroyed(boolean destroyed) {
        this.destroyed = destroyed;
    }
}
