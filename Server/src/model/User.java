package model;

import java.io.*;
import java.util.Scanner;

public class User implements Serializable {
    private static int lastUserID;
    private int userID;
    private String userName, pass;
    private int wonNo, lostNo;

    public User(String userName, String pass) {
        this.userName = userName;
        this.pass = pass;
        this.wonNo = 0;
        this.lostNo = 0;

        File f = new File("Server/data_base/users/lastUserID.txt");
        try {
            Scanner scanner = new Scanner(f);
            lastUserID = Integer.parseInt(scanner.nextLine());
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        lastUserID++;
        this.userID = lastUserID;

        try {
            FileWriter fw = new FileWriter(f);
            fw.write(lastUserID+"\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return userID;
    }

    public String getUserName() {
        return userName;
    }

    public String getPass() {
        return pass;
    }

    public int getWonNo() {
        return wonNo;
    }

    public void setWonNo(int wonNo) {
        this.wonNo = wonNo;
    }

    public void increaseWonNo(int delta){
        this.wonNo += delta;
    }

    public int getLostNo() {
        return lostNo;
    }

    public void setLostNo(int lostNo) {
        this.lostNo = lostNo;
    }

    public void increaseLostNo(int delta){
        this.lostNo += delta;
    }
}
