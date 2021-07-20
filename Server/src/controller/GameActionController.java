package controller;

import model.*;

import java.util.ArrayList;

public class GameActionController {

    public GameActionController() {
    }

    public boolean handlePlayerAction(Game game, int playerID, int x, int y){
        if((x==-1) && (y==-1)){
            return false;
        }

        int enemyID;
        if(game.getPlayerID1()==playerID)
            enemyID = game.getPlayerID2();
        else
            enemyID = game.getPlayerID1();
        GameMap enemyMap = game.getGameMap(enemyID);
        ArrayList<ArrayList<Square>> squares = enemyMap.getSquares();
        SquareContent content = squares.get(x).get(y).getContent();

        if((content == SquareContent.EMPTY_ATTACKED)||(content == SquareContent.FULL_ATTACKED)) {
            return true;
        }
        else if((content == SquareContent.EMPTY)||(content == SquareContent.NEIGHBOR)){
            squares.get(x).get(y).setContent(SquareContent.EMPTY_ATTACKED);
            return false;
        }
        else if(content == SquareContent.FULL){
            squares.get(x).get(y).setContent(SquareContent.FULL_ATTACKED);
            int xs,ys,xt,yt;
            int mapSize = enemyMap.getMapSize();
            boolean newDestroyed;

            for (Ship s: enemyMap.getShips()) {
                if(s.isDestroyed()) continue;

                xs = s.getX();
                ys = s.getY();

                newDestroyed = true;
                for (int i = 0; i < s.getLength(); i++) {
                    switch (s.getOrientation()) {
                        case HORIZONTAL:
                            if(squares.get(xs+i).get(ys).getContent()==SquareContent.FULL)
                                newDestroyed = false;
                            break;
                        case VERTICAL:
                            if(squares.get(xs).get(ys+i).getContent()==SquareContent.FULL)
                                newDestroyed = false;
                            break;
                    }
                }

                if(newDestroyed) {
                    s.setDestroyed(true);
                    for (int i = 0; i < s.getLength() + 2; i++) {
                        switch (s.getOrientation()) {
                            case HORIZONTAL:
                                xt = xs - 1 + i;
                                if ((xt >= 0) && (xt < mapSize)) {
                                    if (ys + 1 < mapSize) {
                                        squares.get(xt).get(ys + 1).setContent(SquareContent.EMPTY_ATTACKED);
                                    }
                                    if (ys - 1 >= 0) {
                                        squares.get(xt).get(ys - 1).setContent(SquareContent.EMPTY_ATTACKED);
                                    }
                                    if ((xt == xs - 1) || (xt == xs + s.getLength()))
                                        squares.get(xt).get(ys).setContent(SquareContent.EMPTY_ATTACKED);
                                }
                                break;
                            case VERTICAL:
                                yt = ys - 1 + i;
                                if ((yt >= 0) && (yt < mapSize)) {
                                    if (xs + 1 < mapSize) {
                                        squares.get(xs + 1).get(yt).setContent(SquareContent.EMPTY_ATTACKED);
                                    }
                                    if (xs - 1 >= 0) {
                                        squares.get(xs - 1).get(yt).setContent(SquareContent.EMPTY_ATTACKED);
                                    }
                                    if ((yt == ys - 1) || (yt == ys + s.getLength()))
                                        squares.get(xs).get(yt).setContent(SquareContent.EMPTY_ATTACKED);
                                }
                                break;
                        }
                    }
                }
            }
            return true;
        }
        else
            return true;
    }

    public boolean isFullDestroyed(Game game, int playerID) {
        int enemyID;
        if(game.getPlayerID1()==playerID)
            enemyID = game.getPlayerID2();
        else
            enemyID = game.getPlayerID1();
        for (Ship s: game.getGameMap(enemyID).getShips()) {
            if(!s.isDestroyed())
                return false;
        }
        return true;
    }
}
