package model;

public class PlayerInfo {
    private String userName;
    private int lost, won;
    private boolean connected;

    public PlayerInfo(String userName, int lost, int won, boolean connected) {
        this.userName = userName;
        this.lost = lost;
        this.won = won;
        this.connected = connected;
    }

    public String getUserName() {
        return userName;
    }

    public int getLost() {
        return lost;
    }

    public int getWon() {
        return won;
    }

    public boolean isConnected() {
        return connected;
    }
}
