package view;

import main.LoopHandler;
import main.SeaBattleClient;
import model.PlayerInfo;
import model.Square;
import view.authentication.AuthenticationPanel;
import view.menu.MenuPanel;
import view.player_info.PlayerInfoPanel;
import view.playing.PlayingFormEvent;
import view.playing.PlayingPanel;
import view.playing.PlayingReqType;
import view.score_table.ScoreTablePanel;
import view.watching.WatchRequestListener;
import view.watching.WatchingPanel;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

public class MainFrame extends JFrame implements Runnable, WindowListener {
    private LoopHandler loopHandler;
    private AuthenticationPanel authPanel;
    private MenuPanel menuPanel;
    private PlayingPanel playingPanel;
    private WatchingPanel watchingPanel;
    private ScoreTablePanel scoreTablePanel;
    private PlayerInfoPanel playerInfoPanel;
    private FrameStatus frameStatus;

    public MainFrame() {
        configFrame();
        frameStatus = FrameStatus.AUTHENTICATION;
        addPanelsToFrame();
        loopHandler = new LoopHandler(ViewConfig.FPS, this);
    }

    private void configFrame(){
        setSize(ViewConfig.FRAME_WIDTH,ViewConfig.FRAME_LENGTH);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(this);
        setResizable(false);
        setAlwaysOnTop(true);
        setVisible(true);
        setFocusable(true);
        setTitle("<< Sea Battle >>");
        setIconImage(new ImageIcon("Client/resources/icon.png").getImage());
    }

    private void addPanelsToFrame(){
        getContentPane().removeAll();
        switch (frameStatus){
            case AUTHENTICATION:
                authPanel = new AuthenticationPanel();
                authPanel.addFormListener(e -> {
                    SeaBattleClient.getMainController().authenticationRequest(e);
                });
                getContentPane().add(authPanel);
                break;
            case MENU:
                menuPanel = new MenuPanel();
                getContentPane().add(menuPanel);
                break;
            case PLAYING:
                SeaBattleClient.getMainController().findOneToPlay();
                playingPanel = new PlayingPanel();
                getContentPane().add(playingPanel);
                break;
            case WATCHING:
                watchingPanel = new WatchingPanel();
                watchingPanel.addWatchingRequestListener(index -> {
                    SeaBattleClient.getMainController().watchingReq(index);
                });
                getContentPane().add(watchingPanel);
                break;
            case SCORE_TABLE:
                scoreTablePanel = new ScoreTablePanel();
                getContentPane().add(scoreTablePanel);
                break;
            case PLAYER_INFO:
                SeaBattleClient.getMainController().getPlayerInfo();
                playerInfoPanel = new PlayerInfoPanel();
                getContentPane().add(playerInfoPanel);
                break;
            case SHIP_MAP:
                break;
        }
    }

    @Override
    public void run() {
        updateFrame();
    }

    public void updateFrame() {
        repaint();
        revalidate();
    }

    public FrameStatus getFrameStatus() {
        return frameStatus;
    }

    public void setFrameStatus(FrameStatus frameStatus) {
        if(frameStatus==FrameStatus.MENU){
            if(this.frameStatus==FrameStatus.WATCHING) {
                SeaBattleClient.getMainController().watchingReq(-1);
            }
            else if(this.frameStatus==FrameStatus.PLAYING && !playingPanel.isFoundEnemy()) {
                SeaBattleClient.getMainController().handlePlayingEvent(
                        new PlayingFormEvent(this, PlayingReqType.BYE_FROM_WAITING));
            }
        }
        this.frameStatus = frameStatus;
        this.addPanelsToFrame();
    }

    public int getWatchingGameIndex(){
        return watchingPanel.getGameIndex();
    }

    public void showAuthenticationError(String error){
        if(authPanel != null)
            authPanel.authenticationError(error);
    }

    public void showAuthenticationSuccess(String success){
        if(authPanel != null) {
            authPanel.authenticationSuccess(success);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.setFrameStatus(FrameStatus.MENU);
        }
    }

    public void showConnected(boolean b){
        if(authPanel != null)
            authPanel.connected(b);
    }

    public void showPlayerInfo(PlayerInfo playerInfo){
        playerInfoPanel.setUserName(playerInfo.getUserName());
        playerInfoPanel.setLost(playerInfo.getLost());
        playerInfoPanel.setWon(playerInfo.getWon());
        playerInfoPanel.setConnectionStatus(playerInfo.isConnected());
    }

    public void showFoundEnemy(){
        playingPanel.setFoundEnemy(true);
    }

    public void updateMap(ArrayList<Square> squares, boolean enemy){
        playingPanel.showUpdatedMap(squares,enemy);
    }

    public void updateMap1(ArrayList<Square> squares, int num){
        if(num==1)
            watchingPanel.playingPanel.showUpdatedMap(squares,false);
        else if(num==2)
            watchingPanel.playingPanel.showUpdatedMap(squares,true);
    }

    public void startGame(String myUserName, String enemyUserName, boolean isMyTurn){
        playingPanel.setPlaying(true, myUserName, enemyUserName, isMyTurn);
    }

    public void changePlayingTurn(boolean actionWin){
        playingPanel.changeTurn(actionWin);
    }

    public void endGame(boolean won){
        if(playingPanel!=null) {
            playingPanel.playersBoardPanel.showEndGame(won);
        }
    }

    public void showScoreTable(DefaultListModel<String> listModel){
        scoreTablePanel.setListModel(listModel);
    }

    public void showGamesTable(DefaultListModel<String> listModel){
        watchingPanel.setListModel(listModel);
    }

    public void changeWatchingTurn(int turn){
        watchingPanel.playingPanel.changeTurn1(turn);
    }

    public void endWatchingGame(int winner){
        watchingPanel.playingPanel.playersBoardPanel.showEndGame1(winner);
    }

    @Override
    public void windowClosing(WindowEvent e) {
        this.loopHandler.pause();
        SeaBattleClient.getMainController().doClose();
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}