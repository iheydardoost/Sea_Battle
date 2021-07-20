package controller;

import controller.DB.UserDB;

public class Context {
    public UserDB userDB;

    public Context() {
        this.userDB = new UserDB();
    }
}
