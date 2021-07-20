package controller;

import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import model.*;

import java.security.SecureRandom;
import java.util.Comparator;
import java.util.LinkedList;

public class MainController {
    private SocketController socketController;
    private static Context context = new Context();
    private static SecureRandom secureRandom = new SecureRandom();
    private final LinkedList<ClientHandler> waitingToPlay;
    private LinkedList<Game> games;
    private GameMapFactory gameMapFactory;
    private GameActionController gameActionController;

    public MainController() {
        waitingToPlay = new LinkedList<>();
        games = new LinkedList<>();
        gameMapFactory = new GameMapFactory(10);
        gameActionController = new GameActionController();
        socketController = new SocketController();
    }

    public void authenticationReq(Packet rp){
        String[] bodyArgs = rp.getBody().split(",");

        if(bodyArgs[0].equals("SIGNUP")) {
            LinkedList<User> users = context.userDB.getAll();
            for (User u : users) {
                if (u.getUserName().equals(bodyArgs[1])) {
                    socketController.getClient(rp.getClientID()).addResponse(
                            new Packet(PacketType.AUTHENTICATION_ERROR,
                                    "userName Already exists.",
                                    0,
                                    false,
                                    rp.getClientID(),
                                    rp.getRequestID()));
                    return;
                }
            }
            User newUser = new User(bodyArgs[1],bodyArgs[2]);
            context.userDB.add(newUser);
            int newAuthToken = secureRandom.nextInt();
            socketController.getClient(rp.getClientID()).setAuthToken(newAuthToken);
            socketController.getClient(rp.getClientID()).addResponse(
                    new Packet(PacketType.AUTHENTICATION_SUCCESS,
                            "signed up.",
                            newAuthToken,
                            true,
                            rp.getClientID(),
                            rp.getRequestID()));
            socketController.getClient(rp.getClientID()).setUserID(newUser.getUserID());
            return;
        }
        if(bodyArgs[0].equals("LOGIN")) {
            LinkedList<User> users = context.userDB.getAll();
            for (User u : users) {
                if (u.getUserName().equals(bodyArgs[1])) {
                    if(socketController.isUserOnline(u.getUserID())){
                        socketController.getClient(rp.getClientID()).addResponse(
                                new Packet(PacketType.AUTHENTICATION_ERROR,
                                        "user is online now.",
                                        0,
                                        false,
                                        rp.getClientID(),
                                        rp.getRequestID()));
                        return;
                    }
                    if(u.getPass().equals(bodyArgs[2])){
                        int newAuthToken = secureRandom.nextInt();
                        socketController.getClient(rp.getClientID()).setAuthToken(newAuthToken);
                        socketController.getClient(rp.getClientID()).addResponse(
                                new Packet(PacketType.AUTHENTICATION_SUCCESS,
                                        "logged in.",
                                        newAuthToken,
                                        true,
                                        rp.getClientID(),
                                        rp.getRequestID()));
                        socketController.getClient(rp.getClientID()).setUserID(u.getUserID());
                        return;
                    }
                    socketController.getClient(rp.getClientID()).addResponse(
                            new Packet(PacketType.AUTHENTICATION_ERROR,
                                    "pass is wrong.",
                                    0,
                                    false,
                                    rp.getClientID(),
                                    rp.getRequestID()));
                    return;
                }
            }
            socketController.getClient(rp.getClientID()).addResponse(
                    new Packet(PacketType.AUTHENTICATION_ERROR,
                            "user does not exist.",
                            0,
                            false,
                            rp.getClientID(),
                            rp.getRequestID()));
        }
    }

    public void playerInfoReq(Packet rp){
        ClientHandler clt = socketController.getClient(rp.getClientID());
        int userID = clt.getUserID();
        User user = context.userDB.get(userID);

        clt.addResponse(
                new Packet(PacketType.INFO_RES,
                        user.getUserName()+","+user.getLostNo()+","+user.getWonNo()+",true",
                        clt.getAuthToken(),
                        true,
                        rp.getClientID(),
                        rp.getRequestID()));
    }

    public SocketController getSocketController() {
        return socketController;
    }

    public void handlePlayingReq(Packet rp){
        String[] args = rp.getBody().split(",");
        if(args[0].equals("find-player")){
            synchronized (waitingToPlay) {
                if (waitingToPlay.size() > 0) {
                    int playerID1 = socketController.getClientByAuth(rp.getAuthToken()).getUserID();
                    int playerID2 = waitingToPlay.removeFirst().getUserID();
                    Game game = new Game(playerID1, playerID2);
                    games.add(game);
                    ClientHandler clt1 = socketController.getClientByID(playerID1);
                    ClientHandler clt2 = socketController.getClientByID(playerID2);
                    clt1.addResponse(
                            new Packet(PacketType.PLAYING_RES,
                                    "found-enemy",
                                    clt1.getAuthToken(),
                                    true,
                                    clt1.getClientID(),
                                    rp.getRequestID()));
                    clt2.addResponse(
                            new Packet(PacketType.PLAYING_RES,
                                    "found-enemy",
                                    clt2.getAuthToken(),
                                    true,
                                    clt2.getClientID(),
                                    rp.getRequestID()));
                } else {
                    waitingToPlay.add(socketController.getClientByAuth(rp.getAuthToken()));
                }
            }
        }
        else if(args[0].equals("bye-from-waiting")){
            byeFromPlaying(socketController.getClientByAuth(rp.getAuthToken()));
        }
        else if(args[0].equals("generate-map")){
            int playerID = socketController.getClientByAuth(rp.getAuthToken()).getUserID();
            Game game = Game.getPlayingGame(playerID,games);
            if(game!=null)
                game.setGameMap(gameMapFactory.makeGameMap(playerID),playerID);
            sendMapToClient(game.getGameMap(playerID),playerID,rp.getRequestID(),false);
        }
        else if(args[0].equals("rearrange-map")){
            int playerID = socketController.getClientByAuth(rp.getAuthToken()).getUserID();
            Game game = Game.getPlayingGame(playerID,games);
            if(game!=null)
                gameMapFactory.reArrangeShips(game.getGameMap(playerID));
            sendMapToClient(game.getGameMap(playerID),playerID,rp.getRequestID(),false);
        }
        else if(args[0].equals("start-game")){
            int playerID = socketController.getClientByAuth(rp.getAuthToken()).getUserID();
            Game game = Game.getPlayingGame(playerID,games);
            game.setPlayerReady(true,playerID);
            if(game.arePlayersReady()){
                game.setUserName(context.userDB.get(game.getPlayerID1()).getUserName(),game.getPlayerID1());
                game.setUserName(context.userDB.get(game.getPlayerID2()).getUserName(),game.getPlayerID2());
                sendMapToClient(game.getGameMap(game.getPlayerID1()),game.getPlayerID2(),0,true);
                sendMapToClient(game.getGameMap(game.getPlayerID2()),game.getPlayerID1(),0,true);
                ClientHandler clt1 = socketController.getClientByID(game.getPlayerID1());
                clt1.addResponse(
                        new Packet(PacketType.PLAYING_RES,
                                "start-game,"+game.getUserName(game.getPlayerID1())+","+game.getUserName(game.getPlayerID2())
                                        +","+game.isPlayer1Turn(),
                                clt1.getAuthToken(),
                                true,
                                clt1.getClientID(),
                                0));
                ClientHandler clt2 = socketController.getClientByID(game.getPlayerID2());
                clt2.addResponse(
                        new Packet(PacketType.PLAYING_RES,
                                "start-game,"+game.getUserName(game.getPlayerID2())+","+game.getUserName(game.getPlayerID1())
                                        +","+(!game.isPlayer1Turn()),
                                clt2.getAuthToken(),
                                true,
                                clt2.getClientID(),
                                0));
            }
        }
        else if(args[0].equals("action")){
            int playerID = socketController.getClientByAuth(rp.getAuthToken()).getUserID();
            Game game = Game.getPlayingGame(playerID,games);
            boolean actionWin =
                    gameActionController.handlePlayerAction(
                            game,playerID,Integer.parseInt(args[1]),Integer.parseInt(args[2]));
            if(!actionWin) game.changeTurn();
            if(gameActionController.isFullDestroyed(game, playerID)) {
                endGame(game, playerID);
            }
            else {
                ClientHandler clt1 = socketController.getClientByID(game.getPlayerID1());
                clt1.addResponse(
                        new Packet(PacketType.PLAYING_RES,
                                "action-turn," + actionWin,
                                clt1.getAuthToken(),
                                true,
                                clt1.getClientID(),
                                0));
                ClientHandler clt2 = socketController.getClientByID(game.getPlayerID2());
                clt2.addResponse(
                        new Packet(PacketType.PLAYING_RES,
                                "action-turn," + actionWin,
                                clt2.getAuthToken(),
                                true,
                                clt2.getClientID(),
                                0));
            }
            sendMapToClient(game.getGameMap(game.getPlayerID1()),game.getPlayerID1(),0,false);
            sendMapToClient(game.getGameMap(game.getPlayerID2()),game.getPlayerID2(),0,false);
            sendMapToClient(game.getGameMap(game.getPlayerID2()),game.getPlayerID1(),0,true);
            sendMapToClient(game.getGameMap(game.getPlayerID1()),game.getPlayerID2(),0,true);

            int clientWatcherID, actionTurn;
            ClientHandler cltWatcher;
            for (int i=0; i<game.getWatchersIDList().size();i++) {
                clientWatcherID = game.getWatchersIDList().get(i);
                cltWatcher = socketController.getClient(clientWatcherID);
                if(game.isPlayer1Turn())
                    actionTurn=1;
                else
                    actionTurn=2;
                cltWatcher.addResponse(
                        new Packet(PacketType.WATCH_RES,
                                "action-turn," + actionTurn,
                                cltWatcher.getAuthToken(),
                                true,
                                cltWatcher.getClientID(),
                                0));
                sendMapToWatcher(game.getGameMap(game.getPlayerID1()),clientWatcherID,0,1);
                sendMapToWatcher(game.getGameMap(game.getPlayerID2()),clientWatcherID,0,2);
            }
        }
    }

    public void endGame(Game game, int winnerID){
        int loserID;
        int winnerNum;
        if(game.getPlayerID1()==winnerID) {
            loserID = game.getPlayerID2();
            winnerNum = 1;
        }
        else {
            loserID = game.getPlayerID1();
            winnerNum = 2;
        }

        ClientHandler clt1 = socketController.getClientByID(winnerID);
        clt1.addResponse(
                new Packet(PacketType.PLAYING_RES,
                        "end-game,true",
                        clt1.getAuthToken(),
                        true,
                        clt1.getClientID(),
                        0));
        ClientHandler clt2 = socketController.getClientByID(loserID);
        clt2.addResponse(
                new Packet(PacketType.PLAYING_RES,
                        "end-game,false",
                        clt2.getAuthToken(),
                        true,
                        clt2.getClientID(),
                        0));

        int clientWatcherID;
        ClientHandler cltWatcher;
        for (int i=0; i<game.getWatchersIDList().size();i++) {
            clientWatcherID = game.getWatchersIDList().get(i);
            cltWatcher = socketController.getClient(clientWatcherID);
            cltWatcher.addResponse(
                    new Packet(PacketType.WATCH_RES,
                            "end-game," + winnerNum,
                            cltWatcher.getAuthToken(),
                            true,
                            cltWatcher.getClientID(),
                            0));
        }

        User winner = context.userDB.get(winnerID);
        winner.increaseWonNo(1);
        context.userDB.add(winner);
        User loser = context.userDB.get(loserID);
        loser.increaseLostNo(1);
        context.userDB.add(loser);
        games.remove(game);
    }

    private void sendMapToClient(GameMap gameMap, int destinationUserID, int requestID, boolean enemy){
        ClientHandler clt = socketController.getClientByID(destinationUserID);
        if(enemy){
            clt.addResponse(
                    new Packet(PacketType.PLAYING_RES,
                            "map,enemy" + gameMap.getMapByString(true),
                            clt.getAuthToken(),
                            true,
                            clt.getClientID(),
                            requestID));
        }
        else {
            clt.addResponse(
                    new Packet(PacketType.PLAYING_RES,
                            "map,my" + gameMap.getMapByString(false),
                            clt.getAuthToken(),
                            true,
                            clt.getClientID(),
                            requestID));
        }
    }

    private void sendMapToWatcher(GameMap gameMap, int destinationClientID, int requestID, int player){
        ClientHandler clt = socketController.getClient(destinationClientID);
        String body = "";
        if(player==1)
            body = "map,player1" + gameMap.getMapByString(true);
        else
            body = "map,player2" + gameMap.getMapByString(true);
        clt.addResponse(
                new Packet(PacketType.WATCH_RES,
                        body,
                        clt.getAuthToken(),
                        true,
                        clt.getClientID(),
                        requestID));
    }

    public void handleWatchingReq(Packet rp){
        String[] args = rp.getBody().split(",");
        if(args[0].equals("list")) {
            Game g;
            String body = "list,";
            for (int i = 0; i < games.size(); i++) {
                g = games.get(i);
                if (g.arePlayersReady()) {
                    body += g.getUserName(g.getPlayerID1()) + ","
                            + g.getUserName(g.getPlayerID2()) + ","
                            + g.getGameID();
                }
                if (i != games.size() - 1) {
                    body += ",";
                }
            }

            ClientHandler clt = socketController.getClientByAuth(rp.getAuthToken());
            clt.addResponse(
                    new Packet(PacketType.WATCH_RES,
                            body,
                            clt.getAuthToken(),
                            true,
                            clt.getClientID(),
                            rp.getRequestID()));
        }
        else if(args[0].equals("watch")) {
            Game g = Game.getPlayingGameByID(Integer.parseInt(args[1]),games);
            int clientWatcherID = rp.getClientID();
            if(g!=null) g.addWatcher(clientWatcherID);

            int actionTurn;
            ClientHandler cltWatcher = socketController.getClient(clientWatcherID);
            if(g.isPlayer1Turn())
                actionTurn=1;
            else
                actionTurn=2;
            cltWatcher.addResponse(
                    new Packet(PacketType.WATCH_RES,
                            "action-turn," + actionTurn,
                            cltWatcher.getAuthToken(),
                            true,
                            cltWatcher.getClientID(),
                            0));
            sendMapToWatcher(g.getGameMap(g.getPlayerID1()),clientWatcherID,0,1);
            sendMapToWatcher(g.getGameMap(g.getPlayerID2()),clientWatcherID,0,2);
        }
        else if(args[0].equals("bye")) {
            Game g = Game.getPlayingGameByID(Integer.parseInt(args[1]),games);
            if(g!=null) g.removeWatcher(rp.getClientID());
        }
    }

    public void handleScoreTableReq(Packet rp){
        LinkedList<User> users = context.userDB.getAll();
        users.sort((o1, o2) -> {
            int o1Score = o1.getWonNo()- o1.getLostNo();
            int o2Score = o2.getWonNo()- o2.getLostNo();
            return (o2Score-o1Score);
        });
        String body = "";
        User u;
        for (int i = 0; i < users.size(); i++) {
            u = users.get(i);
            body += u.getUserName() + ","
                    + Integer.toString(u.getWonNo()-u.getLostNo()) + ",";
            if(socketController.isUserOnline(u.getUserID()))
                body += "online";
            else
                body += "offline";
            if(i!=users.size()-1){
                body += ",";
            }
        }

        ClientHandler clt = socketController.getClientByAuth(rp.getAuthToken());
        clt.addResponse(
                new Packet(PacketType.SCORE_TABLE_RES,
                        body,
                        clt.getAuthToken(),
                        true,
                        clt.getClientID(),
                        rp.getRequestID()));
    }

    public void byeFromPlaying(ClientHandler clt) {
        synchronized (waitingToPlay) {
            waitingToPlay.remove(clt);
            Game game = Game.getPlayingGame(clt.getUserID(), games);
            //if(game!=null) game.playerLeaved(clt.getUserID());
            if(game!=null) games.remove(game);
        }
    }
}
