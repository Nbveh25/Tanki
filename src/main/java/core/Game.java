package core;

import javax.swing.*;

public class Game {
    private final GamePanel gamePanel;

    public Game() {
        this.gamePanel = new GamePanel();
    }

    public void start() {
        gamePanel.startGameThread();
    }

    public JPanel getPanel() {
        return gamePanel;
    }
} 