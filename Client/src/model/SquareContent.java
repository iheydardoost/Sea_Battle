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

    public static SquareContent getByIndex(int i){
        switch (i){
            case 1:
                return EMPTY;
            case 2:
                return EMPTY_ATTACKED;
            case 3:
                return NEIGHBOR;
            case 4:
                return FULL;
            case 5:
                return FULL_ATTACKED;
            default:
                return null;
        }
    }
}
