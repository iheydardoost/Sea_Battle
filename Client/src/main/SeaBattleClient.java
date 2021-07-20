package main;

import controller.MainController;
import view.MainFrame;

public class SeaBattleClient {
    private static MainFrame mainFrame;
    private static MainController mainController;

    public static void main(String[] args) {
        mainFrame = new MainFrame();
        mainController = new MainController();
    }

    public static MainFrame getMainFrame() {
        return mainFrame;
    }

    public static MainController getMainController() {
        return mainController;
    }

}
