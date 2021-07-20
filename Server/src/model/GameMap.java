package model;

import java.util.ArrayList;
import java.util.Random;

public class GameMap {
    private final int mapSize;
    private ArrayList<ArrayList<Square>> squares;
    private ArrayList<Ship> ships;
    private int playerID;

    public GameMap(int mapSize, int playerID) {
        this.mapSize = mapSize;
        this.playerID = playerID;
        this.squares = new ArrayList<>();
        this.ships = new ArrayList<>();
    }

    public int getMapSize() {
        return mapSize;
    }

    public ArrayList<ArrayList<Square>> getSquares() {
        return squares;
    }

    public ArrayList<Ship> getShips() {
        return ships;
    }

    public int getPlayerID() {
        return playerID;
    }

    public String getMapByString(boolean enemy){
        if(squares==null) return null;
        String str = "";
        int kk;

        for(int i=0;i<mapSize;i++){
            for(int j=0;j<mapSize;j++){
                kk = squares.get(i).get(j).getContent().index;
                if(enemy){
                    if((kk==3)||(kk==4)) kk=1;
                }
                str += ","+i+","+j+","+kk;
            }
        }

        return str;
    }
}
