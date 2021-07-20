package view.playing;

import model.Square;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class SeaMapPanel extends JPanel implements MouseListener {
    private int mapSize;
    private ArrayList<ArrayList<JLabel>> labels;
    SquareClickListener clickListener;

    public SeaMapPanel(int mapSize) {
        this.mapSize = mapSize;
        labels = new ArrayList<>();
        for(int i=0;i<mapSize;i++){
            labels.add(new ArrayList<>());
        }
        configurePanel();
    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
    }

    private void configurePanel(){
        this.setLayout(new GridLayout(mapSize,mapSize));
        this.addMouseListener(this);
        for (int j =0; j<mapSize; j++){
            for (int i =0; i<mapSize; i++){
                labels.get(j).add(new JLabel());
                labels.get(j).get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK));
                this.add(labels.get(j).get(i));
            }
        }
    }

    public void updateLabels(ArrayList<Square> squares){
        for (Square s: squares) {
            switch (s.getContent()){
                case EMPTY:
                case NEIGHBOR:
                    labels.get(s.getY()).get(s.getX()).setIcon(null);
                    break;
                case EMPTY_ATTACKED:
                    labels.get(s.getY()).get(s.getX()).setIcon(new ImageIcon("Client/resources/empty_attacked.jpg"));
                    break;
                case FULL:
                    labels.get(s.getY()).get(s.getX()).setIcon(new ImageIcon("Client/resources/full.jpg"));
                    break;
                case FULL_ATTACKED:
                    labels.get(s.getY()).get(s.getX()).setIcon(new ImageIcon("Client/resources/full_attacked.png"));
                    break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(e.getSource().equals(this)) {
            int i = e.getX()/50;
            int j = e.getY()/50;
            clickListener.SquareClickOccurred(new SquareClickEvent(this, i, j));
        }
    }

    public void addClickListener(SquareClickListener clickListener) {
        this.clickListener = clickListener;
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
