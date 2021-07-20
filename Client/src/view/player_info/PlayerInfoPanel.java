package view.player_info;

import main.SeaBattleClient;
import view.FrameStatus;
import view.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayerInfoPanel extends JLayeredPane implements ActionListener {
    JLabel backImage;
    JButton backToMenu;
    JLabel connectionStatusLabel, scoreLabel, wonLabel, lostLabel, userNameLabel;
    int lost,won;

    public PlayerInfoPanel() {
        this.lost=0;
        this.won=0;
        configureButtons();
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(backToMenu)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.MENU);
        }
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);

        userNameLabel = new JLabel();
        userNameLabel.setBounds(200,100,700,300);
        userNameLabel.setText("userName: ");
        userNameLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(userNameLabel);

        connectionStatusLabel = new JLabel();
        connectionStatusLabel.setBounds(200,200,700,300);
        connectionStatusLabel.setText("connection status: ");
        connectionStatusLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(connectionStatusLabel);

        wonLabel = new JLabel();
        wonLabel.setBounds(200,300,700,300);
        wonLabel.setText("won games: ");
        wonLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(wonLabel);

        lostLabel = new JLabel();
        lostLabel.setBounds(200,400,700,300);
        lostLabel.setText("lost games: ");
        lostLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(lostLabel);

        scoreLabel = new JLabel();
        scoreLabel.setBounds(200,500,700,300);
        scoreLabel.setText("total score: ");
        scoreLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(scoreLabel);

        backImage = new JLabel();
        backImage.setBounds(0,0,ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
        backImage.setIcon(new ImageIcon("Client/resources/paper.png"));
        add(backImage);
    }

    private void configureButtons() {
        backToMenu = new JButton("Menu");
        backToMenu.setBounds(30, 800, 100, 50);
        backToMenu.setFont(new Font("Broadway", Font.PLAIN, 20));
        backToMenu.setBackground(Color.RED);
        backToMenu.setFocusable(false);
        backToMenu.addActionListener(this);
        add(backToMenu);
    }

    public void setUserName(String userName) {
        userNameLabel.setText("userName: "+userName);
    }

    public void setLost(int lost) {
        this.lost = lost;
        lostLabel.setText("lost: "+lost);
        scoreLabel.setText("total score: "+(won-lost));
    }

    public void setWon(int won) {
        this.won = won;
        wonLabel.setText("won: "+won);
        scoreLabel.setText("total score: "+(won-lost));
    }

    public void setConnectionStatus(boolean connectionStatus) {
        if(connectionStatus)
            connectionStatusLabel.setText("connection status: online");
        else
            connectionStatusLabel.setText("connection status: offline");
    }
}
