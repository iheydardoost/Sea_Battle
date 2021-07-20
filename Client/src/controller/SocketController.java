package controller;

import main.LoopHandler;
import main.SeaBattleClient;
import model.Packet;
import model.PacketType;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.Scanner;

public class SocketController implements Runnable{
    private final Socket socket;
    private InputStream input;
    private OutputStream output;
    private final LoopHandler loopHandler;
    private InetAddress ipAddress;
    private int port;
    private final LinkedList<Packet> requests;
    private final LinkedList<Packet> responses;
    private int authToken;
    private boolean authTokenAvailable;

    public SocketController() {
        requests = new LinkedList<>();
        responses = new LinkedList<>();
        authToken = 0;
        authTokenAvailable = false;

        getConfigFromFile();
        InetSocketAddress socketAddress = new InetSocketAddress(ipAddress,port);
        this.socket = new Socket();
        try {
            socket.connect(socketAddress,5);
            this.input = socket.getInputStream();
            this.output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        SeaBattleClient.getMainFrame().showConnected(socket.isConnected());

        loopHandler = new main.LoopHandler(600, this);
        //loopHandler.setNonStop(true);
    }

    @Override
    public void run() {
        try {
            synchronized (requests) {
                if (!requests.isEmpty()) {
                    Packet request = requests.removeFirst();
                    output.write(request.getPacketStr().getBytes(StandardCharsets.UTF_8));
                }
            }
            synchronized (responses) {
                if (input.available() > 0) {
                    String inStr = "";
                    char ch = '0';
                    while(ch != '$'){
                        ch = (char) input.read();
                        inStr += ch;
                    }
                    System.out.println("inStrClient: "+inStr);
                    Packet response = Packet.parsePacket(inStr);
                    responses.add(response);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getConfigFromFile(){
        String path = "Client/src/client_config.txt";
        File file = new File(path);
        if(file.isFile()) {
            try {
                Scanner scanner = new Scanner(file);
                String[] strings = scanner.nextLine().split(",");
                ipAddress = InetAddress.getByName(strings[0]);
                port = Integer.parseInt(strings[1]);
                return;
            } catch (FileNotFoundException | UnknownHostException e) {
                e.printStackTrace();
            }
        }
        try {
            ipAddress = InetAddress.getByName("localhost");
            port = 8000;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void addRequest(Packet rp){
        synchronized (requests) {
            requests.add(rp);
        }
    }

    public Packet getResponse(){
        synchronized (responses) {
            if (responses.isEmpty())
                return null;
            else
                return responses.removeFirst();
        }
    }

    public void setAuthToken(int authToken){
        authTokenAvailable = true;
        this.authToken = authToken;
    }

    public int getAuthToken() {
        return authToken;
    }

    public boolean isAuthTokenAvailable() {
        return authTokenAvailable;
    }

    public void closeSocket(){
        Packet request = new Packet(PacketType.BYE,"",this.authToken,this.authTokenAvailable);
        try {
            output.write(request.getPacketStr().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            this.loopHandler.pause();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
