package view.playing;

import main.SeaBattleClient;
import model.GameMap;
import model.Square;
import view.FrameStatus;
import view.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class PlayingPanel extends JLayeredPane implements ActionListener {
    JLabel backImage, status;
    public PlayersBoardPanel playersBoardPanel;
    SeaMapPanel myMapPanel, enemyMapPanel;
    MapArrangePanel mapArrangePanel;
    boolean isPlaying, foundEnemy;
    boolean isWatchingPanel;
    String myUserName, enemyUserName;
    JLabel userTurnLabel;
    boolean myTurn;
    JButton backToMenu;

    public PlayingPanel() {
        isPlaying = false;
        foundEnemy = false;
        myTurn = false;
        isWatchingPanel = false;
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source.equals(backToMenu)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.MENU);
        }
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);

        if(!foundEnemy){
            status = new JLabel();
            status.setBounds(150,100,700,200);
            status.setText("wait for another player to connect...");
            status.setFont(new Font("Arial",Font.BOLD,40));
            add(status);

            backToMenu = new JButton("Menu");
            backToMenu.setBounds(30, 800, 100, 50);
            backToMenu.setFont(new Font("Broadway", Font.PLAIN, 20));
            backToMenu.setBackground(Color.RED);
            backToMenu.setFocusable(false);
            backToMenu.addActionListener(this);
            add(backToMenu);
        }
        else {
            if (!isPlaying) {
                myMapPanel = new SeaMapPanel(10);
                myMapPanel.setBounds(100,350,500,500);
                myMapPanel.addClickListener(event -> {});
                mapArrangePanel = new MapArrangePanel(myMapPanel);
                mapArrangePanel.setBounds(0, 0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
                mapArrangePanel.addFormListener(event -> {
                    SeaBattleClient.getMainController().handlePlayingEvent(event);
                });
                if(!isWatchingPanel) mapArrangePanel.generateFirstMap();
                add(mapArrangePanel);
            }
            else{
                playersBoardPanel = new PlayersBoardPanel(myUserName,enemyUserName);
                playersBoardPanel.setBounds(0,0,ViewConfig.FRAME_WIDTH,330);
                if(!isWatchingPanel) {
                    playersBoardPanel.addClickListener(event -> {
                        if (myTurn)
                            SeaBattleClient.getMainController().handleClickEvent(event);
                    });
                }
                else{
                    playersBoardPanel.addClickListener(event -> {});
                }
                add(playersBoardPanel);

                userTurnLabel = new JLabel();
                userTurnLabel.setBounds(600,550,50,100);
                if(myTurn)
                    userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-left.png"));
                else
                    userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-right.png"));
                add(userTurnLabel);

                add(myMapPanel);
                if(enemyMapPanel==null){
                    enemyMapPanel = new SeaMapPanel(10);
                    enemyMapPanel.setBounds(650,350,500,500);
                    if(!isWatchingPanel) {
                        enemyMapPanel.addClickListener(event -> {
                            if (this.myTurn)
                                SeaBattleClient.getMainController().handleClickEvent(event);
                        });
                    }
                    else{
                        enemyMapPanel.addClickListener(event -> {});
                    }
                }
                add(enemyMapPanel);
            }
        }

        backImage = new JLabel();
        backImage.setBounds(0,0,ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
        backImage.setIcon(new ImageIcon("Client/resources/paper.png"));
        add(backImage);
    }

    public void setPlaying(boolean playing, String myUserName, String enemyUserName, boolean isMyTurn) {
        isPlaying = playing;
        this.myUserName = myUserName;
        this.enemyUserName = enemyUserName;
        this.myTurn = isMyTurn;
        removeAll();
        configurePanel();
    }

    public void setFoundEnemy(boolean foundEnemy) {
        this.foundEnemy = foundEnemy;
        removeAll();
        configurePanel();
    }

    public boolean isFoundEnemy() {
        return foundEnemy;
    }

    public void showUpdatedMap(ArrayList<Square> squares, boolean enemy){
        if(enemy) {
            if(enemyMapPanel==null){
                enemyMapPanel = new SeaMapPanel(10);
                enemyMapPanel.setBounds(650,350,500,500);
                if(!isWatchingPanel) {
                    enemyMapPanel.addClickListener(event -> {
                        if (this.myTurn)
                            SeaBattleClient.getMainController().handleClickEvent(event);
                    });
                }
            }
            enemyMapPanel.updateLabels(squares);
        }
        else {
            myMapPanel.updateLabels(squares);
        }
    }

    public void changeTurn(boolean actionWin){
        playersBoardPanel.resetTime();
        if(!actionWin) {
            myTurn = !myTurn;
            if (myTurn)
                userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-left.png"));
            else
                userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-right.png"));
        }
    }

    public void changeTurn1(int turn){
        playersBoardPanel.resetTime();
        if(turn==1) myTurn = true;
        else if(turn==2) myTurn = false;

        if (myTurn)
            userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-left.png"));
        else
            userTurnLabel.setIcon(new ImageIcon("Client/resources/arrow-right.png"));
    }

    public void setWatchingPanel(boolean watchingPanel) {
        isWatchingPanel = watchingPanel;
    }

}
