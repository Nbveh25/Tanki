package core;

import config.GameConfig;
import entity.Player;

public class Camera {
    private static Camera instance;
    private int x;
    private int y;
    private final int width;
    private final int height;

    private Camera(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public static Camera getInstance(int width, int height) {
        if (instance == null) {
            instance = new Camera(width, height);
        }
        return instance;
    }

    public void update(Player player) {
        // Центрируем камеру на игроке
        x = player.getWorldX() - (width / 2) + (GameConfig.TILE_SIZE / 2);
        y = player.getWorldY() - (height / 2) + (GameConfig.TILE_SIZE / 2);

        // Ограничиваем камеру, чтобы она не выходила за пределы мира
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x > GameConfig.WORLD_WIDTH - width) {
            x = GameConfig.WORLD_WIDTH - width;
        }
        if (y > GameConfig.WORLD_HEIGHT - height) {
            y = GameConfig.WORLD_HEIGHT - height;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}