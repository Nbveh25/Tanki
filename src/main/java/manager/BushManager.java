package manager;

import config.GameConfig;
import core.Camera;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BushManager {
    private static BushManager instance;
    private final List<Point> bushPositions;
    private BufferedImage bushSprite;
    private final TileManager tileManager;

    private BushManager() {
        bushPositions = new ArrayList<>();
        tileManager = TileManager.getInstance();
        loadSprite();
        loadBushesFromMap();
    }

    public static BushManager getInstance() {
        if (instance == null) {
            instance = new BushManager();
        }
        return instance;
    }

    private void loadSprite() {
        try {
            bushSprite = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tile/bush.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadBushesFromMap() {
        for (int row = 0; row < GameConfig.MAX_WORLD_ROW; row++) {
            for (int col = 0; col < GameConfig.MAX_WORLD_COL; col++) {
                if (tileManager.getTileNumber(col, row) == 16) {
                    bushPositions.add(new Point(
                        col * GameConfig.TILE_SIZE,
                        row * GameConfig.TILE_SIZE
                    ));
                }
            }
        }
    }

    public void draw(Graphics2D g2) {
        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        
        for (Point bush : bushPositions) {
            int screenX = bush.x - camera.getX();
            int screenY = bush.y - camera.getY();

            // Отрисовываем куст только если он находится в пределах экрана
            if (screenX + GameConfig.TILE_SIZE > 0 &&
                screenX - GameConfig.TILE_SIZE < GameConfig.SCREEN_WIDTH &&
                screenY + GameConfig.TILE_SIZE > 0 &&
                screenY - GameConfig.TILE_SIZE < GameConfig.SCREEN_HEIGHT) {
                
                g2.drawImage(bushSprite, screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
            }
        }
    }
} 