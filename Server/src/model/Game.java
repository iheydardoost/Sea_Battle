package model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;

public class Game {
    private final int playerID1;
    private final int playerID2;
    private final int gameID;
    private static final SecureRandom secureRandom = new SecureRandom();
    private GameMap gameMap1, gameMap2;
    private boolean player1Ready, player2Ready;
    private String user1Name, user2Name;
    private boolean player1Turn;
    private ArrayList<Integer> watchersIDList;

    public Game(int playerID1, int playerID2) {
        this.playerID1 = playerID1;
        this.playerID2 = playerID2;
        this.gameID = secureRandom.nextInt();
        this.player1Ready = false;
        this.player2Ready = false;
        this.player1Turn = secureRandom.nextBoolean();
        this.watchersIDList = new ArrayList<>();
    }

    public static Game getPlayingGame(int playerID, LinkedList<Game> games){
        for (Game game: games) {
            if(game.playerID1==playerID || game.playerID2==playerID)
                return game;
        }
        return null;
    }

    public static Game getPlayingGameByID(int gameID, LinkedList<Game> games){
        for (Game game: games) {
            if(game.gameID==gameID)
                return game;
        }
        return null;
    }

    public void addWatcher(int clientID){
        watchersIDList.add(clientID);
    }

    public void removeWatcher(int clientID){
        watchersIDList.remove(Integer.valueOf(clientID));
    }

    public ArrayList<Integer> getWatchersIDList() {
        return watchersIDList;
    }

    public boolean isPlayerReady(int playerID) {
        if(playerID==playerID1)
            return player1Ready;
        else if(playerID==playerID2)
            return player2Ready;
        else
            return false;
    }

    public boolean arePlayersReady(){
        return (player1Ready && player2Ready);
    }

    public void setPlayerReady(boolean playerReady,int playerID) {
        if(playerID==playerID1)
            this.player1Ready = playerReady;
        else if(playerID==playerID2)
            this.player2Ready = playerReady;
    }

    public int getPlayerID1() {
        return playerID1;
    }

    public int getPlayerID2() {
        return playerID2;
    }

    public int getGameID() {
        return gameID;
    }

    public GameMap getGameMap(int playerID) {
        if(playerID==playerID1)
            return gameMap1;
        else if(playerID==playerID2)
            return gameMap2;
        else
            return null;
    }

    public void setGameMap(GameMap gameMap, int playerID) {
        if(playerID==playerID1)
            this.gameMap1 = gameMap;
        else if(playerID==playerID2)
            this.gameMap2 = gameMap;
    }

    public String getUserName(int playerID) {
        if(playerID==playerID1)
            return user1Name;
        else if(playerID==playerID2)
            return user2Name;
        else
            return null;
    }

    public void setUserName(String userName, int playerID) {
        if(playerID==playerID1)
            this.user1Name = userName;
        else if(playerID==playerID2)
            this.user2Name = userName;
    }

    public boolean isPlayer1Turn() {
        return player1Turn;
    }

    public void changeTurn(){
        player1Turn = !player1Turn;
    }

    public void setPlayer1Turn(boolean player1Turn) {
        this.player1Turn = player1Turn;
    }
}
