package view.authentication;

import java.util.EventObject;

public class AuthenticationFormEvent extends EventObject {
    private boolean isLogIn, isSignUp;
    private String user, pass;

    public AuthenticationFormEvent(Object source, boolean isLogIn, boolean isSignUp, String user, String pass) {
        super(source);
        this.isLogIn = isLogIn;
        this.isSignUp = isSignUp;
        this.user = user;
        this.pass = pass;
    }

    public boolean isLogIn() {
        return isLogIn;
    }

    public void setLogIn(boolean logIn) {
        isLogIn = logIn;
    }

    public boolean isSignUp() {
        return isSignUp;
    }

    public void setSignUp(boolean signUp) {
        isSignUp = signUp;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
}
