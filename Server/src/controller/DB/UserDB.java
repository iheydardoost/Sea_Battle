package controller.DB;

import model.User;

import java.io.*;
import java.util.LinkedList;

public class UserDB implements DBset<User>{
    String folderPath;

    public UserDB() {
        folderPath = "Server/data_base/users";
    }

    @Override
    public User get(int ID) {
        FileInputStream f = null;
        ObjectInputStream o = null;
        User user = null;
        try {
            f = new FileInputStream(folderPath+"/"+ID+".txt");
            o = new ObjectInputStream(f);
            user = (User) o.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            if(f!=null) f.close();
            if(o!=null) o.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return user;
    }


    @Override
    public LinkedList<User> getAll() {
        File userDirectory = new File(folderPath);
        File[] files = userDirectory.listFiles();
        FileInputStream f = null;
        ObjectInputStream o = null;
        User user = null;
        LinkedList<User> users = new LinkedList<>();

        for(int i=0;i<files.length-1;i++) {
            try {
                f = new FileInputStream(files[i]);
                o = new ObjectInputStream(f);
                user = (User) o.readObject();
                users.add(user);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            if(f!=null) f.close();
            if(o!=null) o.close();
        }catch (IOException e){
            e.printStackTrace();
        }

        return users;
    }

    @Override
    public void add(User user) {
        File file;
        FileOutputStream f = null;
        ObjectOutputStream o = null;
        try {
            file = new File(folderPath+"/"+user.getUserID()+".txt");
            if(!file.exists()) file.createNewFile();
            f = new FileOutputStream(file);
            o = new ObjectOutputStream(f);
            o.writeObject(user);
            o.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            if(f!=null) f.close();
            if(o!=null) o.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean remove(User user) {
        return false;
    }
}
