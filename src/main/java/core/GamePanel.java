package core;

import config.GameConfig;
import entity.Player;
import input.InputHandler;
import manager.TileManager;
import util.CollisionChecker;


import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final InputHandler inputHandler;
    private final Player player;
    private final Camera camera;

    private TileManager tileManager;

    public GamePanel() {
        setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        this.tileManager = new TileManager();
        this.inputHandler = new InputHandler();
        CollisionChecker collisionChecker = new CollisionChecker(tileManager);
        this.player = new Player(inputHandler, collisionChecker);
        this.camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        addKeyListener(inputHandler);
        setFocusable(true);
    }

    public void startGameThread() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public void run() {
        double drawInterval = 1_000_000_000D / GameConfig.FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        
        while (gameThread != null) {
            long currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    private void update() {
        player.update();
        camera.update(player);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);
        player.draw(g2);


        g2.dispose();
    }
} 