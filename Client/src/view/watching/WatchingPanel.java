package view.watching;

import main.SeaBattleClient;
import view.FrameStatus;
import view.ViewConfig;
import view.playing.PlayingPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class WatchingPanel extends JLayeredPane implements ActionListener, MouseListener {
    JLabel backImage;
    JButton backToMenu;
//    JButton watchButton;
    String userName1, userName2;
    JList<String> gamesList;
    DefaultListModel<String> listModel;
    WatchRequestListener watchRequestListener;
    boolean isWatching;
    int gameIndex;
    public PlayingPanel playingPanel;

    public WatchingPanel() {
        isWatching = false;
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
        if(source.equals(backToMenu)){
            SeaBattleClient.getMainFrame().setFrameStatus(FrameStatus.MENU);
        }
//        else if(source.equals(watchButton)){
//            System.out.println(gamesList.getSelectedIndex());
//        }
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
        if(isWatching){
            playingPanel = new PlayingPanel();
            playingPanel.setWatchingPanel(true);
            playingPanel.setFoundEnemy(true);
            playingPanel.setPlaying(true,userName1,userName2,true);
            add(playingPanel);
        }
        else {
            listModel = new DefaultListModel<>();
            gamesList = new JList<>(listModel);
            gamesList.setBounds(150, 200, 800, 600);
            gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            gamesList.setFont(new Font("Arial", Font.PLAIN, 25));
            gamesList.addMouseListener(this);
            add(gamesList);

            backImage = new JLabel();
            backImage.setBounds(0,0,ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);
            backImage.setIcon(new ImageIcon("Client/resources/paper.png"));
            add(backImage);
        }
    }

    private void configureButtons() {
//        watchButton = new JButton("Watch");
//        watchButton.setBounds(850, 400, 150, 80);
//        watchButton.setFont(new Font("Broadway", Font.PLAIN, 20));
//        watchButton.setBackground(Color.PINK);
//        watchButton.setFocusable(false);
//        watchButton.addActionListener(this);
//        add(watchButton);

        backToMenu = new JButton("Menu");
        if(isWatching){
            backToMenu.setBounds(575, 270, 100, 50);
        }
        else{
            backToMenu.setBounds(30, 800, 100, 50);
        }
        backToMenu.setFont(new Font("Broadway", Font.PLAIN, 20));
        backToMenu.setBackground(Color.RED);
        backToMenu.setFocusable(false);
        backToMenu.addActionListener(this);
        add(backToMenu);
    }

    public void setListModel(DefaultListModel<String> listModel) {
        this.listModel.removeAllElements();
        for (int i=0; i<listModel.size();i++) {
            this.listModel.addElement(listModel.get(i));
        }
    }

    public void addWatchingRequestListener(WatchRequestListener watchRequestListener){
        this.watchRequestListener = watchRequestListener;
    }

    public int getGameIndex() {
        if(isWatching)
            return gameIndex;
        else
            return -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        gameIndex = gamesList.getSelectedIndex();
        watchRequestListener.watchRequestOccurred(gameIndex);
        String[] args = listModel.get(gamesList.getSelectedIndex()).split("   vs.   ");
        userName1 = args[0];
        userName2 = args[1];
        isWatching = true;
        removeAll();
        configureButtons();
        configurePanel();
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
