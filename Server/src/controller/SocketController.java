package controller;

import main.LoopHandler;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class SocketController implements Runnable{
    private ServerSocket serverSocket;
    private InetAddress ipAddress;
    private int port;
    private final LoopHandler loopHandler;
    private final LinkedList<ClientHandler> clients;
    private static final Random random = new Random();

    public SocketController() {
        clients = new LinkedList<>();
        getConfigFromFile();
        try {
            this.serverSocket = new ServerSocket(port,50, ipAddress);
        } catch (IOException e){
            e.printStackTrace();
        }
        loopHandler = new LoopHandler(600, this);
        //loopHandler.setNonStop(true);
    }

    @Override
    public void run() {
        Socket socket = null;
        try {
            socket = serverSocket.accept();
            ClientHandler clt = new ClientHandler(socket, random.nextInt());
            synchronized (clients) {
                clients.add(clt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getConfigFromFile(){
        String path = "Server/src/server_config.txt";
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

    public ClientHandler getClient(int clientID){
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getClientID() == clientID)
                    return client;
            }
            return null;
        }
    }

    public ClientHandler getClientByAuth(int authToken){
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getAuthToken() == authToken)
                    return client;
            }
            return null;
        }
    }

    public ClientHandler getClientByID(int userID){
        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (client.getUserID() == userID)
                    return client;
            }
            return null;
        }
    }

    public void removeClient(ClientHandler clt){
        clients.remove(clt);
    }

    public boolean isUserOnline(int userID){
        synchronized (clients) {
            for (ClientHandler ch : clients) {
                if (ch.getUserID() == userID)
                    return true;
            }
        }
        return false;
    }
}
