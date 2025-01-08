package entity;

import config.GameConfig;
import core.Camera;
import enums.Direction;
import util.CollisionChecker;

import java.awt.*;

public class Bullet {
    private int worldX, worldY;
    private final int speed;
    private final Direction direction;
    private boolean active;
    private static final int BULLET_SIZE = 8;

    public Bullet(int worldX, int worldY, Direction direction) {
        this.worldX = worldX;
        this.worldY = worldY;
        this.direction = direction;
        this.speed = 4;
        this.active = true;
    }

    public void update(CollisionChecker collisionChecker) {
        switch (direction) {
            case UP -> worldY -= speed;
            case DOWN -> worldY += speed;
            case LEFT -> worldX -= speed;
            case RIGHT -> worldX += speed;
        }

        if (collisionChecker.checkBulletCollision(this)) {
            active = false; // Деактивируем снаряд при столкновении
        }

        // Деактивируем снаряд, если он выходит за пределы мира
        if (worldX < 0 || worldX > GameConfig.WORLD_WIDTH || 
            worldY < 0 || worldY > GameConfig.WORLD_HEIGHT) {
            active = false;
        }
    }

    public void draw(Graphics2D g2) {
        if (!active) return;

        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        int screenX = worldX - camera.getX();
        int screenY = worldY - camera.getY();

        g2.setColor(Color.YELLOW);
        g2.fillOval(screenX, screenY, BULLET_SIZE, BULLET_SIZE);
    }

    public boolean isActive() {
        return active;
    }

    public Rectangle getCollisionArea() {
        return new Rectangle(worldX, worldY, BULLET_SIZE, BULLET_SIZE);
    }
}