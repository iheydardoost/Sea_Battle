package view.playing;

import main.SeaBattleClient;
import view.FrameStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayersBoardPanel extends JLayeredPane implements ActionListener {
    JLabel user1Label, user2Label, timeLabel;
    JLabel user1Winning, user2Winning;
    long totalTime, startTime;
    String myUserName, enemyUserName;
    SquareClickListener clickListener;
    boolean finished;
    JButton backToMenu;

    public PlayersBoardPanel(String myUserName, String enemyUserName) {
        this.myUserName = myUserName;
        this.enemyUserName = enemyUserName;
        this.finished = false;
        totalTime = 25000;
        startTime = System.currentTimeMillis();
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        updateTimeLeft();
        super.paintComponent(g);
    }

    private void configurePanel(){
        timeLabel = new JLabel();
        timeLabel.setBounds(575,200,150,50);
        timeLabel.setText("time");
        timeLabel.setFont(new Font("Arial",Font.BOLD,35));
        add(timeLabel);

        user1Label = new JLabel();
        user1Label.setBounds(100,200,425,50);
        user1Label.setText(myUserName);
        user1Label.setFont(new Font("Arial",Font.BOLD,35));
        add(user1Label);

        user2Label = new JLabel();
        user2Label.setBounds(725,200,425,50);
        user2Label.setText(enemyUserName);
        user2Label.setFont(new Font("Arial",Font.BOLD,35));
        user2Label.setHorizontalAlignment(SwingConstants.RIGHT);
        add(user2Label);
    }

    private void updateTimeLeft(){
        if(!finished) {
            long timeToShow = totalTime - (System.currentTimeMillis() - startTime);
            if (timeToShow <= 0) {
                startTime = System.currentTimeMillis();
                clickListener.SquareClickOccurred(new SquareClickEvent(this, -1, -1));
            }
            int timeSeconds = (int) (timeToShow / 1000);
            int timeHundred = (int) ((timeToShow - (timeSeconds * 1000)) / 10);
            timeLabel.setText(timeSeconds + ":" + timeHundred);
        }
    }

    public void resetTime(){
        startTime = System.currentTimeMillis();
    }

    public void addClickListener(SquareClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void showEndGame(boolean won){
        this.finished = true;

        user1Winning = new JLabel();
        user1Winning.setBounds(400,200,150,50);
        user1Winning.setFont(new Font("Arial",Font.BOLD,35));
        add(user1Winning);

        user2Winning = new JLabel();
        user2Winning.setBounds(650,200,150,50);
        user2Winning.setFont(new Font("Arial",Font.BOLD,35));
        user2Winning.setHorizontalAlignment(SwingConstants.RIGHT);
        add(user2Winning);

        if(won){
            user1Winning.setForeground(Color.GREEN);
            user1Winning.setText("WON");
            user2Winning.setForeground(Color.RED);
            user2Winning.setText("LOST");
        }
        else{
            user2Winning.setForeground(Color.GREEN);
            user2Winning.setText("WON");
            user1Winning.setForeground(Color.RED);
            user1Winning.setText("LOST");
        }

        backToMenu = new JButton("Menu");
        backToMenu.setBounds(575, 270, 100, 50);
        backToMenu.setFont(new Font("Broadway", Font.PLAIN, 20));
        backToMenu.setBackground(Color.WHITE);
        backToMenu.setFocusable(false);
        backToMenu.addActionListener(this);
        add(backToMenu);
    }

    public void showEndGame1(int winner){
        if(winner==1)
            showEndGame(true);
        else if(winner==2)
            showEndGame(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource().equals(backToMenu)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.MENU);
        }
    }
}
