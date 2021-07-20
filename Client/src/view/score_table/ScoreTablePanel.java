package view.score_table;

import main.SeaBattleClient;
import view.FrameStatus;
import view.ViewConfig;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ScoreTablePanel extends JLayeredPane implements ActionListener {
    JLabel backImage;
    JButton backToMenu;
    JList<String> scoresList;
    DefaultListModel<String> listModel;

    public ScoreTablePanel() {
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
    }

    private void configurePanel(){
        setBounds(0,0, ViewConfig.FRAME_WIDTH, ViewConfig.FRAME_LENGTH);

        listModel = new DefaultListModel<>();
        scoresList = new JList<>(listModel);
        scoresList.setBounds(150,200,800,600);
        scoresList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scoresList.setFont(new Font("Arial", Font.PLAIN, 25));
        add(scoresList);

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

    public void setListModel(DefaultListModel<String> listModel) {
        this.listModel.removeAllElements();
        for (int i=0; i<listModel.size();i++) {
            this.listModel.addElement(listModel.get(i));
        }
    }
}
