package view.menu;

import main.SeaBattleClient;
import view.FrameStatus;
import view.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPanel extends JLayeredPane implements ActionListener {
    JLabel backImage;
    JLabel title;
    JButton watchBtn, playBtn, scoreTableBtn, myInfoBtn, backToAuth;

    public MenuPanel() {
        configureButtons();
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source.equals(playBtn)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.PLAYING);
        }
        else if(source.equals(watchBtn)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.WATCHING);
        }
        else if(source.equals(scoreTableBtn)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.SCORE_TABLE);
        }
        else if(source.equals(myInfoBtn)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.PLAYER_INFO);
        }
//        else if(source.equals(backToAuth)){
//            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.AUTHENTICATION);
//        }
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);

        title = new JLabel();
        title.setBounds(270,100,700,300);
        title.setText("<< Sea Battle >>");
        title.setFont(new Font("Arial",Font.BOLD,90));
        add(title);

        backImage = new JLabel();
        backImage.setBounds(0,0,ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
        backImage.setIcon(new ImageIcon("Client/resources/paper.png"));
        add(backImage);
    }

    private void configureButtons(){
        playBtn = new JButton("Play");
        playBtn.setBounds(470,370,300,70);
        playBtn.setFont(new Font("Algerian",Font.PLAIN,25));
        playBtn.setBackground(Color.ORANGE);
        playBtn.setFocusable(false);
        playBtn.addActionListener(this);
        add(playBtn);

        watchBtn = new JButton("Watch");
        watchBtn.setBounds(470,470,300,70);
        watchBtn.setFont(new Font("Algerian",Font.PLAIN,25));
        watchBtn.setBackground(Color.ORANGE);
        watchBtn.setFocusable(false);
        watchBtn.addActionListener(this);
        add(watchBtn);

        scoreTableBtn = new JButton("Score Table");
        scoreTableBtn.setBounds(470,570,300,70);
        scoreTableBtn.setFont(new Font("Algerian",Font.PLAIN,25));
        scoreTableBtn.setBackground(Color.ORANGE);
        scoreTableBtn.setFocusable(false);
        scoreTableBtn.addActionListener(this);
        add(scoreTableBtn);

        myInfoBtn = new JButton("My Info");
        myInfoBtn.setBounds(470,670,300,70);
        myInfoBtn.setFont(new Font("Algerian",Font.PLAIN,25));
        myInfoBtn.setBackground(Color.ORANGE);
        myInfoBtn.setFocusable(false);
        myInfoBtn.addActionListener(this);
        add(myInfoBtn);

//        backToAuth = new JButton("Log Out");
//        backToAuth.setBounds(30, 800, 100, 80);
//        backToAuth.setFont(new Font("Broadway", Font.PLAIN, 20));
//        backToAuth.setBackground(Color.RED);
//        backToAuth.setFocusable(false);
//        backToAuth.addActionListener(this);
//        add(backToAuth);
    }
}
