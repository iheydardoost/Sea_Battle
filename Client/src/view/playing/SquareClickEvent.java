package view.playing;

import java.util.EventObject;

public class SquareClickEvent extends EventObject {
    private int x,y;

    public SquareClickEvent(Object source, int x, int y) {
        super(source);
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
