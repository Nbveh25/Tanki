package core;

import config.GameConfig;
import entity.Player;
import handler.InputHandler;
import manager.BushManager;
import manager.TileManager;
import util.CollisionChecker;
import network.GameServer;
import network.GameClient;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class GamePanel extends JPanel implements Runnable {
    private Thread gameThread;
    private final InputHandler inputHandler;
    private final Player player;
    private final Camera camera;

    private TileManager tileManager;
    private BushManager bushManager;

    private final CollisionChecker collisionChecker;

    private GameClient gameClient;

    public GamePanel(boolean isHost, String serverIp, String playerName) {
        setPreferredSize(new Dimension(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT));
        setBackground(Color.BLACK);
        setDoubleBuffered(true);

        this.tileManager = TileManager.getInstance();
        this.bushManager = BushManager.getInstance();

        this.inputHandler = new InputHandler();
        this.collisionChecker = new CollisionChecker();
        this.player = new Player(inputHandler, collisionChecker, playerName);
        this.camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        addKeyListener(inputHandler);
        setFocusable(true);

        try {
            if (isHost) {
                GameServer server = new GameServer();
                new Thread(server).start();
            }
            gameClient = new GameClient(serverIp, player);
            new Thread(gameClient).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        if (gameClient != null) {
            player.update(gameClient.getOtherPlayers());
            camera.update(player);
            
            // Проверяем коллизии с бонусами
            gameClient.getObjectManager().checkCollision(player);
            gameClient.getObjectManager().removeInactiveHearts();
            
            gameClient.sendPlayerPosition();
        } else {
            player.update(null);
            camera.update(player);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        tileManager.draw(g2);
        player.draw(g2);

        if (gameClient != null) {
            gameClient.getObjectManager().draw(g2);
            gameClient.getOtherPlayers().values().forEach(otherPlayer -> {
                otherPlayer.draw(g2);
                otherPlayer.getBullets().forEach(bullet -> bullet.draw(g2));
            });
        }
        bushManager.draw(g2);

        g2.dispose();
    }
} 