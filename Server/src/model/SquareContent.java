package model;

public enum SquareContent {
    EMPTY(1),
    EMPTY_ATTACKED(2),
    NEIGHBOR(3),
    FULL(4),
    FULL_ATTACKED(5);

    public final int index;

    SquareContent(int index) {
        this.index = index;
    }
}
