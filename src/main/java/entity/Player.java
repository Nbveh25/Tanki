package entity;

import config.GameConfig;
import core.Camera;
import enums.Direction;
import input.InputHandler;
import util.CollisionChecker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player extends Entity {
    private final InputHandler inputHandler;
    private final int screenX;
    private final int screenY;
    private final CollisionChecker collisionChecker;
    private final List<Bullet> bullets;

    private long lastFireTime;
    private final long fireCooldown = 500;

    public Player(InputHandler inputHandler, CollisionChecker collisionChecker) {
        this.inputHandler = inputHandler;
        this.collisionChecker = collisionChecker;
        this.bullets = new ArrayList<>();

        screenX = GameConfig.SCREEN_WIDTH / 2 - (GameConfig.TILE_SIZE / 2);
        screenY = GameConfig.SCREEN_HEIGHT / 2 - (GameConfig.TILE_SIZE / 2);

        solidArea = new Rectangle();
        solidArea.x = 4;
        solidArea.y = 4;
        solidArea.width = 40;
        solidArea.height = 40;

        setDefaultValues();
        getPlayerImage();
    }

    @Override
    public void update() {
        // Обновляем существующие пули
        bullets.removeIf(bullet -> {
            bullet.update(collisionChecker);
            return !bullet.isActive();
        });

        if (inputHandler.isUpPressed() || inputHandler.isDownPressed() || 
            inputHandler.isLeftPressed() || inputHandler.isRightPressed()) {
            
            collisionOn = false;
            
            if (inputHandler.isUpPressed()) {
                direction = Direction.UP;
            } else if (inputHandler.isDownPressed()) {
                direction = Direction.DOWN;
            } else if (inputHandler.isLeftPressed()) {
                direction = Direction.LEFT;
            } else if (inputHandler.isRightPressed()) {
                direction = Direction.RIGHT;
            }

            // Проверяем коллизии
            collisionChecker.checkTile(this);

            // Двигаемся только если нет коллизии
            if (!collisionOn) {
                switch (direction) {
                    case UP -> worldY -= speed;
                    case DOWN -> worldY += speed;
                    case LEFT -> worldX -= speed;
                    case RIGHT -> worldX += speed;
                }
            }

            spriteCounter++;
            if (spriteCounter > 14) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }

        if (inputHandler.isFirePressed()) {
            fire();
        }

    }

    private void fire() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFireTime >= fireCooldown) {
            int bulletX = worldX;
            int bulletY = worldY;

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
            lastFireTime = currentTime;
        }
    }

    @Override
    public void draw(Graphics2D g2) {

        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        int screenX = getWorldX() - camera.getX();
        int screenY = getWorldY() - camera.getY();
        BufferedImage image = null;

        switch (direction) {
            case UP:
                if (spriteNum == 1) {
                    image = up1;
                } else if (spriteNum == 2) {
                    image = up2;
                }
                break;
            case DOWN:
                if (spriteNum == 1) {
                    image = down1;
                } else if (spriteNum == 2) {
                    image = down2;
                }
                break;
            case LEFT:
                if (spriteNum == 1) {
                    image = left1;
                } else if (spriteNum == 2) {
                    image = left2;
                }
                break;
            case RIGHT:
                if (spriteNum == 1) {
                    image = right1;
                } else if (spriteNum == 2) {
                    image = right2;
                }
                break;
            default:
                image = null;
        }
        g2.drawImage(image, screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
        bullets.forEach(bullet -> bullet.draw(g2));
    }

    private void setDefaultValues() {
        worldX = 5 * GameConfig.TILE_SIZE;
        worldY = 5 * GameConfig.TILE_SIZE;
        speed = 1;
        direction = Direction.DOWN;
    }

    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_down_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_left_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/smoki_right_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}