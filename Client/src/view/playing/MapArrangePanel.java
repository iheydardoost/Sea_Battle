package view.playing;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapArrangePanel extends JLayeredPane implements ActionListener {
    JLabel timeLabel;
    SeaMapPanel seaMapPanel;
    JButton playBtn, newMapBtn;
    long totalTime, startTime;
    int newMapTimes;
    PlayingFormListener formListener;
    boolean waitToStart;

    public MapArrangePanel(SeaMapPanel seaMapPanel) {
        this.seaMapPanel = seaMapPanel;
        totalTime = 30000;
        startTime = System.currentTimeMillis();
        newMapTimes = 0;
        waitToStart = false;
        configureButtons();
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        updateTimeLeft();
        super.paintComponent(g);
    }

    private void configurePanel(){
        timeLabel = new JLabel();
        timeLabel.setBounds(800,400,400,50);
        timeLabel.setText("time left = 30:00");
        timeLabel.setFont(new Font("Arial",Font.BOLD,30));
        add(timeLabel);
        add(seaMapPanel);
    }

    private void configureButtons(){
        playBtn = new JButton("Play");
        playBtn.setBounds(800,600,120,50);
        playBtn.setFont(new Font("Arial",Font.ITALIC,20));
        playBtn.setBackground(Color.ORANGE);
        playBtn.setFocusable(false);
        playBtn.addActionListener(this);
        add(playBtn);

        newMapBtn = new JButton("New Map");
        newMapBtn.setBounds(950,600,120,50);
        newMapBtn.setFont(new Font("Arial",Font.ITALIC,20));
        newMapBtn.setBackground(Color.ORANGE);
        newMapBtn.setFocusable(false);
        newMapBtn.addActionListener(this);
        add(newMapBtn);
    }

    private void updateTimeLeft(){
        if(!waitToStart) {
            long timeToShow = totalTime - (System.currentTimeMillis() - startTime);
            if (timeToShow <= 0) {
                formListener.playingEventOccurred(new PlayingFormEvent(this, PlayingReqType.START_PLAY));
                waitToStart = true;
            }
            int timeSeconds = (int) (timeToShow / 1000);
            int timeHundred = (int) ((timeToShow - (timeSeconds * 1000)) / 10);
            timeLabel.setText("time left = " + timeSeconds + ":" + timeHundred);
        }
        else {
            timeLabel.setText("wait for your enemy");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if(source.equals(playBtn)){
            formListener.playingEventOccurred(new PlayingFormEvent(this,PlayingReqType.START_PLAY));
            waitToStart = true;
        }
        else if(source.equals(newMapBtn)){
            if(newMapTimes<3) {
                newMapTimes++;
                totalTime += 10000;
                formListener.playingEventOccurred(new PlayingFormEvent(this,PlayingReqType.REARRANGE_MAP));
            }
        }
    }

    public void generateFirstMap(){
        formListener.playingEventOccurred(new PlayingFormEvent(this,PlayingReqType.NEW_MAP_REQ));
    }

    public void addFormListener(PlayingFormListener formListener){
        this.formListener = formListener;
    }
}
