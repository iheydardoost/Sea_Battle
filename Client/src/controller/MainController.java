package controller;

import main.LoopHandler;
import main.SeaBattleClient;
import model.*;
import view.authentication.AuthenticationFormEvent;
import view.FrameStatus;
import view.playing.PlayingFormEvent;
import view.playing.PlayingReqType;
import view.playing.SquareClickEvent;

import javax.swing.*;
import java.util.ArrayList;

public class MainController implements Runnable{
    private main.LoopHandler loopHandler;
    private SocketController socketController;
    private long scoreTableReqTime, watchTableReqTime;
    private ArrayList<Integer> watchingListIDs;

    public MainController() {
        this.socketController = new SocketController();
        scoreTableReqTime = System.currentTimeMillis();
        watchTableReqTime = System.currentTimeMillis();
        watchingListIDs = new ArrayList<>();
        this.loopHandler = new LoopHandler(ControllerConfig.CONTROLLER_FREQ,this);
    }

    @Override
    public void run() {
        long currentTime = System.currentTimeMillis();
        if(SeaBattleClient.getMainFrame().getFrameStatus()==FrameStatus.WATCHING){
            if(currentTime-watchTableReqTime>=1000){
                watchTableReqTime = currentTime;
                Packet rp = new Packet(
                        PacketType.WATCH_REQ,
                        "list",
                        socketController.getAuthToken(),
                        socketController.isAuthTokenAvailable());
                socketController.addRequest(rp);
            }
        }
        else if(SeaBattleClient.getMainFrame().getFrameStatus()==FrameStatus.SCORE_TABLE){
            if(currentTime-scoreTableReqTime>=1000){
                scoreTableReqTime = currentTime;
                Packet rp = new Packet(
                        PacketType.SCORE_TABLE_REQ,
                        "",
                        socketController.getAuthToken(),
                        socketController.isAuthTokenAvailable());
                socketController.addRequest(rp);
            }
        }
        /********************************************************************/
        Packet response = socketController.getResponse();
        if(response==null) return;
        if(response.isAuthTokenAvailable()) socketController.setAuthToken(response.getAuthToken());

        switch (response.getPacketType()){
            case AUTHENTICATION_ERROR:
                SeaBattleClient.getMainFrame().showAuthenticationError(response.getBody());
                break;
            case AUTHENTICATION_SUCCESS:
                SeaBattleClient.getMainFrame().showAuthenticationSuccess(response.getBody());
                break;
            case INFO_RES:
                this.sendPlayerInfo(response);
                break;
            case PLAYING_RES:
                handlePlayingRes(response);
                break;
            case WATCH_RES:
                handleWatchingRes(response);
                break;
            case SCORE_TABLE_RES:
                handleScoreTableRes(response);
                break;
        }
    }

    public void authenticationRequest(AuthenticationFormEvent formEvent){
        String body = "";
        if(formEvent.isLogIn())
            body = "LOGIN";
        if(formEvent.isSignUp())
            body = "SIGNUP";
        body += ","+formEvent.getUser() + "," + formEvent.getPass();
        Packet rp = new Packet(PacketType.AUTHENTICATION_REQ,body,0,false);
        socketController.addRequest(rp);
    }

    public void doClose(){
        this.loopHandler.pause();
        socketController.closeSocket();
    }

    public void getPlayerInfo(){
        Packet rp = new Packet(
                PacketType.INFO_REQ,
                "",
                socketController.getAuthToken(),
                socketController.isAuthTokenAvailable());
        socketController.addRequest(rp);
    }

    public void sendPlayerInfo(Packet response){
        String[] args = response.getBody().split(",");
        PlayerInfo playerInfo =
                new PlayerInfo(
                        args[0],
                        Integer.parseInt(args[1]),
                        Integer.parseInt(args[2]),
                        Boolean.parseBoolean(args[3]));
        SeaBattleClient.getMainFrame().showPlayerInfo(playerInfo);
    }

    public void findOneToPlay(){
        Packet rp = new Packet(
                PacketType.PLAYING_REQ,
                "find-player",
                socketController.getAuthToken(),
                socketController.isAuthTokenAvailable());
        socketController.addRequest(rp);
    }

    public void handlePlayingEvent(PlayingFormEvent event){
        Packet rp = null;
        if(event.getReqType() == PlayingReqType.NEW_MAP_REQ){
            rp = new Packet(
                    PacketType.PLAYING_REQ,
                    "generate-map",
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        else if(event.getReqType() == PlayingReqType.REARRANGE_MAP) {
            rp = new Packet(
                    PacketType.PLAYING_REQ,
                    "rearrange-map",
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        else if(event.getReqType() == PlayingReqType.START_PLAY) {
            rp = new Packet(
                    PacketType.PLAYING_REQ,
                    "start-game",
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        else if(event.getReqType() == PlayingReqType.BYE_FROM_WAITING) {
            rp = new Packet(
                    PacketType.PLAYING_REQ,
                    "bye-from-waiting",
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        socketController.addRequest(rp);
    }

    private void handlePlayingRes(Packet rp){
        String[] args = rp.getBody().split(",");
        if(args[0].equals("found-enemy")){
            SeaBattleClient.getMainFrame().showFoundEnemy();
        }
        else if(args[0].equals("map")){
            ArrayList<Square> squares = new ArrayList<>();
            int x=0,y=0,kk=0;
            for(int i=2;i<args.length;i++){
                if(i%3==2){
                    x = Integer.parseInt(args[i]);
                }
                else if(i%3==0){
                    y = Integer.parseInt(args[i]);
                }
                else if(i%3==1){
                    kk = Integer.parseInt(args[i]);
                    squares.add(new Square(x,y,SquareContent.getByIndex(kk)));
                }
            }
            if(args[1].equals("my"))
                SeaBattleClient.getMainFrame().updateMap(squares,false);
            else if(args[1].equals("enemy"))
                SeaBattleClient.getMainFrame().updateMap(squares,true);
        }
        else if(args[0].equals("start-game")){
            SeaBattleClient.getMainFrame().startGame(args[1],args[2],Boolean.parseBoolean(args[3]));
        }
        else if(args[0].equals("action-turn")){
            SeaBattleClient.getMainFrame().changePlayingTurn(Boolean.parseBoolean(args[1]));
        }
        else if(args[0].equals("end-game")){
            SeaBattleClient.getMainFrame().endGame(Boolean.parseBoolean(args[1]));
        }
    }

    public void handleClickEvent(SquareClickEvent event){
        Packet rp = new Packet(
                PacketType.PLAYING_REQ,
                "action,"+event.getX()+","+event.getY(),
                socketController.getAuthToken(),
                socketController.isAuthTokenAvailable());
        socketController.addRequest(rp);
    }

    private void handleWatchingRes(Packet rp){
        String[] args = rp.getBody().split(",");
        if(args[0].equals("list")) {
            DefaultListModel<String> listModel = new DefaultListModel<>();
            watchingListIDs.clear();
            for (int i = 1; i <= (args.length-1)/3; i++) {
                listModel.addElement(args[3 * i - 2] + "   vs.   " + args[3 * i - 1]);
                watchingListIDs.add(Integer.parseInt(args[3*i]));
            }
            SeaBattleClient.getMainFrame().showGamesTable(listModel);
        }
        else if(args[0].equals("map")) {
            ArrayList<Square> squares = new ArrayList<>();
            int x=0,y=0,kk=0;
            for(int i=2;i<args.length;i++){
                if(i%3==2){
                    x = Integer.parseInt(args[i]);
                }
                else if(i%3==0){
                    y = Integer.parseInt(args[i]);
                }
                else if(i%3==1){
                    kk = Integer.parseInt(args[i]);
                    squares.add(new Square(x,y,SquareContent.getByIndex(kk)));
                }
            }
            if(args[1].equals("player1"))
                SeaBattleClient.getMainFrame().updateMap1(squares,1);
            else if(args[1].equals("player2"))
                SeaBattleClient.getMainFrame().updateMap1(squares,2);
        }
        else if(args[0].equals("action-turn")) {
            SeaBattleClient.getMainFrame().changeWatchingTurn(Integer.parseInt(args[1]));
        }
        else if(args[0].equals("end-game")) {
            SeaBattleClient.getMainFrame().endWatchingGame(Integer.parseInt(args[1]));
        }
    }

    public void watchingReq(int index){
        Packet rp;
        if(index==-1){
            int watchingGameIndex = SeaBattleClient.getMainFrame().getWatchingGameIndex();
            if((watchingGameIndex<0) || (watchingGameIndex>=watchingListIDs.size())) return;
            rp = new Packet(
                    PacketType.WATCH_REQ,
                    "bye," + watchingListIDs.get(watchingGameIndex),
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        else {
            rp = new Packet(
                    PacketType.WATCH_REQ,
                    "watch," + watchingListIDs.get(index),
                    socketController.getAuthToken(),
                    socketController.isAuthTokenAvailable());
        }
        socketController.addRequest(rp);
    }

    private void handleScoreTableRes(Packet rp){
        String[] args = rp.getBody().split(",");
        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (int i = 0; i < (args.length/3); i++) {
            listModel.addElement(args[3 * i] + " , score=" + args[3 * i + 1] + " , " + args[3*i+2]);
        }
        SeaBattleClient.getMainFrame().showScoreTable(listModel);
    }

}
