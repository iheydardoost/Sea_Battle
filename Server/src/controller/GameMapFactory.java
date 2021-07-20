package controller;

import model.*;

import java.util.ArrayList;
import java.util.Random;

import static model.Ship.Orientation.VERTICAL;

public class GameMapFactory {
    public int mapSize;

    public GameMapFactory(int mapSize) {
        this.mapSize = mapSize;
    }

    public GameMap makeGameMap(int playerID){
        GameMap gameMap = new GameMap(mapSize,playerID);
        addSquares(gameMap);
        addShips(gameMap);
        arrangeShips(gameMap);
        return gameMap;
    }

    public void addSquares(GameMap gm){
        int mapSize = gm.getMapSize();
        ArrayList<ArrayList<Square>> squares = gm.getSquares();

        for(int i=0;i<mapSize;i++){
            squares.add(new ArrayList<>());
            for (int j=0;j<mapSize;j++){
                squares.get(i).add(new Square(i,j, SquareContent.EMPTY));
            }
        }
    }

    public void addShips(GameMap gm){
        ArrayList<Ship> ships = gm.getShips();

        for(int i=0; i<4;i++){
            for(int j=0;j<i+1;j++){
                ships.add(new Ship(4-i));
            }
        }
    }

    public void reArrangeShips(GameMap gm){
        if(gm.getSquares()==null) addSquares(gm);
        if(gm.getShips()==null) addShips(gm);
        ArrayList<ArrayList<Square>> squares = gm.getSquares();

        for(int i=0;i<mapSize;i++){
            for(int j=0;j<mapSize;j++){
                squares.get(i).get(j).setContent(SquareContent.EMPTY);
            }
        }
        arrangeShips(gm);
    }

    public void arrangeShips(GameMap gm){
        ArrayList<Ship> ships = gm.getShips();
        int mapSize = gm.getMapSize();
        ArrayList<ArrayList<Square>> squares = gm.getSquares();
        Random random = new Random();
        int x, y;
        Ship.Orientation orientation;

        for (Ship ship: ships) {
            do {
                x = random.nextInt(mapSize);
                y = random.nextInt(mapSize);
                if(random.nextBoolean()) orientation = VERTICAL;
                else orientation = Ship.Orientation.HORIZONTAL;

            } while (!canArrangeShip(gm, ship, x, y, orientation));
            ship.setX(x);
            ship.setY(y);
            ship.setOrientation(orientation);

            for (int i = 0; i < ship.getLength(); i++)
            {
                switch (orientation) {
                    case HORIZONTAL:
                        squares.get(x+i).get(y).setContent(SquareContent.FULL);
                        break;
                    case VERTICAL:
                        squares.get(x).get(y+i).setContent(SquareContent.FULL);
                        break;
                }
            }

            int xt,yt;
            for (int i = 0; i < ship.getLength()+2; i++)
            {
                switch (orientation) {
                    case HORIZONTAL:
                        xt = x-1+i;
                        if((xt>=0)&&(xt<mapSize)) {
                            if (y + 1 < mapSize) {
                                squares.get(xt).get(y + 1).setContent(SquareContent.NEIGHBOR);
                            }
                            if (y - 1 >= 0) {
                                squares.get(xt).get(y - 1).setContent(SquareContent.NEIGHBOR);
                            }
                            if( (xt == x-1) || (xt == x + ship.getLength()) )
                                squares.get(xt).get(y).setContent(SquareContent.NEIGHBOR);
                        }
                        break;
                    case VERTICAL:
                        yt = y-1+i;
                        if((yt>=0)&&(yt<mapSize)) {
                            if (x + 1 < mapSize) {
                                squares.get(x + 1).get(yt).setContent(SquareContent.NEIGHBOR);
                            }
                            if (x - 1 >= 0) {
                                squares.get(x - 1).get(yt).setContent(SquareContent.NEIGHBOR);
                            }
                            if( (yt == y-1) || (yt == y + ship.getLength()) )
                                squares.get(x).get(yt).setContent(SquareContent.NEIGHBOR);
                        }
                        break;
                }
            }
        }
    }

    public boolean canArrangeShip(GameMap gm, Ship ship, int x, int y, Ship.Orientation orientation){
        int len = ship.getLength();
        ArrayList<ArrayList<Square>> squares = gm.getSquares();
        SquareContent content;

        switch (orientation){
            case HORIZONTAL:
                if(x+len>mapSize) return false;

                for(int i=0;i<len;i++){
                    content = squares.get(x+i).get(y).getContent();
                    if((content == SquareContent.NEIGHBOR) || (content == SquareContent.FULL))
                        return false;
                }
                break;
            case VERTICAL:
                if(y+len>mapSize) return false;

                for(int i=0;i<len;i++){
                    content = squares.get(x).get(y+i).getContent();
                    if((content == SquareContent.NEIGHBOR) || (content == SquareContent.FULL))
                        return false;
                }
                break;
        }
        return true;
    }
}
