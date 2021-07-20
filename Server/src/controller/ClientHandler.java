package controller;

import main.LoopHandler;
import main.SeaBattleServer;
import model.Packet;
import model.PacketType;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class ClientHandler implements Runnable{
    private LoopHandler loopHandler;
    private Socket socket;
    private OutputStream output;
    private InputStream input;
    private int authToken;
    private int clientID;
    private int userID;
    private final LinkedList<Packet> requests;
    private final LinkedList<Packet> responses;

    public ClientHandler(Socket socket, int clientID) {
        requests = new LinkedList<>();
        responses = new LinkedList<>();
        this.clientID = clientID;

        this.socket = socket;
        try {
            this.output = socket.getOutputStream();
            this.input = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loopHandler = new LoopHandler(200, this);
    }

    @Override
    public void run() {
        String inStr = "";
        char ch;
        try {
            synchronized (requests) {
                if (input.available() > 0) {
                    inStr = "";
                    ch = '0';
                    while(ch != '$'){
                        ch = (char) input.read();
                        inStr += ch;
                    }
                    System.out.println("inStrServer: "+inStr);
                    Packet request = Packet.parsePacket(inStr,clientID);
                    if((request.isAuthTokenAvailable() && request.getAuthToken()==this.authToken)
                            || request.getPacketType()==PacketType.AUTHENTICATION_REQ)
                        requests.add(request);
                    if(request.getPacketType()==PacketType.BYE) {
                        SeaBattleServer.mainController.getSocketController().removeClient(this);
                        SeaBattleServer.mainController.byeFromPlaying(this);
                        this.socket.close();
                        this.loopHandler.pause();
                    }
                }
            }
            synchronized (responses) {
                if (!responses.isEmpty()) {
                    Packet response = responses.removeFirst();
                    output.write(response.getPacketStr().getBytes(StandardCharsets.UTF_8));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        synchronized (requests){
            if(requests.size()>0){
                parseRequest(requests.removeFirst());
            }
        }
    }

    public void parseRequest(Packet rp){
        switch (rp.getPacketType()){
            case AUTHENTICATION_REQ:
                SeaBattleServer.mainController.authenticationReq(rp);
                break;
            case INFO_REQ:
                SeaBattleServer.mainController.playerInfoReq(rp);
                break;
            case PLAYING_REQ:
                SeaBattleServer.mainController.handlePlayingReq(rp);
                break;
            case WATCH_REQ:
                SeaBattleServer.mainController.handleWatchingReq(rp);
                break;
            case SCORE_TABLE_REQ:
                SeaBattleServer.mainController.handleScoreTableReq(rp);
                break;
        }
    }

    public void addResponse(Packet rp){
        synchronized (responses) {
            responses.add(rp);
        }
    }

    public int getAuthToken() {
        return authToken;
    }

    public void setAuthToken(int authToken){
        this.authToken = authToken;
    }

    public int getClientID() {
        return clientID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}
