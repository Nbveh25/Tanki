package handler;

import config.GameConfig;
import entity.Bullet;
import entity.Player;
import enums.Direction;

import java.util.List;

public class WeaponHandler {
    private final InputHandler inputHandler;
    private long lastFireTime;
    private static final long FIRE_COOLDOWN = 2000;

    public WeaponHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
    }

    public void handleShooting(Player player, List<Bullet> bullets) {
        if (inputHandler != null && inputHandler.isFirePressed()) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastFireTime >= FIRE_COOLDOWN) {
                player.isShooting = true;
                createBullet(player, bullets);
                lastFireTime = currentTime;
            }
        }
    }

    private void createBullet(Player player, List<Bullet> bullets) {
        int bulletX = player.getWorldX();
        int bulletY = player.getWorldY();
        Direction direction = player.getDirection();

        switch (direction) {
            case UP -> {
                bulletX += GameConfig.TILE_SIZE / 2 - 4;
                bulletY -= 8;
            }
            case DOWN -> {
                bulletX += GameConfig.TILE_SIZE / 2 - 4;
                bulletY += GameConfig.TILE_SIZE;
            }
            case LEFT -> {
                bulletX -= 8;
                bulletY += GameConfig.TILE_SIZE / 2 - 4;
            }
            case RIGHT -> {
                bulletX += GameConfig.TILE_SIZE;
                bulletY += GameConfig.TILE_SIZE / 2 - 4;
            }
        }

        bullets.add(new Bullet(bulletX, bulletY, direction));
    }
} 