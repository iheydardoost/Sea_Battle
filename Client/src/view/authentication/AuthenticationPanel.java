package view.authentication;

import view.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AuthenticationPanel extends JLayeredPane implements ActionListener {
    JLabel backImage;
    JLabel title, userLabel, passLabel, statusLabel, connectionLabel;
    JButton logInBtn, signUpBtn;
    JTextField userField, passField;
    AuthenticationFormListener formListener;

    public AuthenticationPanel() {
        configureButtons();
        configureFields();
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);

        title = new JLabel();
        title.setBounds(270,100,700,300);
        title.setText("<< Sea Battle >>");
        title.setFont(new Font("Arial",Font.BOLD,90));
        add(title);

        userLabel = new JLabel();
        userLabel.setBounds(200,400,100,50);
        userLabel.setText("User :");
        userLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(userLabel);

        passLabel = new JLabel();
        passLabel.setBounds(200,500,100,50);
        passLabel.setText("Pass :");
        passLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(passLabel);

        statusLabel = new JLabel();
        statusLabel.setBounds(630,450,500,50);
        statusLabel.setText("");
        statusLabel.setFont(new Font("Arial",Font.BOLD,25));
        statusLabel.setVisible(false);
        add(statusLabel);

        connectionLabel = new JLabel();
        connectionLabel.setBounds(300,700,300,50);
        connectionLabel.setText("Connecting...");
        connectionLabel.setFont(new Font("Arial",Font.BOLD,20));
        add(connectionLabel);

        backImage = new JLabel();
        backImage.setBounds(0,0,ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
        backImage.setIcon(new ImageIcon("Client/resources/paper.png"));
        add(backImage);
    }

    private void configureButtons(){
        logInBtn = new JButton("Log In");
        logInBtn.setBounds(300,600,120,50);
        logInBtn.setFont(new Font("Arial",Font.ITALIC,20));
        logInBtn.setBackground(Color.ORANGE);
        logInBtn.setFocusable(false);
        logInBtn.addActionListener(this);
        add(logInBtn);

        signUpBtn = new JButton("Sign Up");
        signUpBtn.setBounds(450,600,120,50);
        signUpBtn.setFont(new Font("Arial",Font.ITALIC,20));
        signUpBtn.setBackground(Color.ORANGE);
        signUpBtn.setFocusable(false);
        signUpBtn.addActionListener(this);
        add(signUpBtn);
    }

    private void configureFields(){
        userField = new JTextField();
        userField.setBounds(300,400,300,50);
        userField.setFont(new Font("Arial", Font.PLAIN,20));
        add(userField);

        passField = new JTextField();
        passField.setBounds(300,500,300,50);
        passField.setFont(new Font("Arial", Font.PLAIN,20));
        add(passField);
    }

    public void addFormListener(AuthenticationFormListener formListener){
        this.formListener = formListener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isLogIn = false;
        boolean isSignUp = false;
        if(e.getSource() == logInBtn){
            isLogIn = true;
        }
        if(e.getSource() == signUpBtn){
            isSignUp = true;
        }
        AuthenticationFormEvent event =
                new AuthenticationFormEvent(this,
                        isLogIn,
                        isSignUp,
                        userField.getText(),
                        passField.getText());
        formListener.authenticationEventOccurred(event);
    }

    public void authenticationError(String errorStr){
        userField.setText("");
        passField.setText("");
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(errorStr);
        statusLabel.setVisible(true);
    }

    public void authenticationSuccess(String successStr){
        userField.setText("");
        passField.setText("");
        statusLabel.setForeground(Color.GREEN);
        statusLabel.setText(successStr);
        statusLabel.setVisible(true);
    }

    public void connected(boolean b){
        if(b) connectionLabel.setText("Connected.");
        else connectionLabel.setText("Not Connected.");
    }
}
