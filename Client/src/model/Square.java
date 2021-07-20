package model;

public class Square {
    private final int x, y;
    private SquareContent content;

    public Square(int x, int y, SquareContent content) {
        this.x = x;
        this.y = y;
        this.content = content;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public SquareContent getContent() {
        return content;
    }

    public void setContent(SquareContent content) {
        this.content = content;
    }
}
