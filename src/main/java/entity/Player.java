package entity;

import config.GameConfig;
import core.Camera;
import enums.Direction;
import input.InputHandler;
import ui.GameOverMenu;
import util.CollisionChecker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Map;


public class Player extends Entity {
    private final InputHandler inputHandler;
    private final int screenX;
    private final int screenY;
    private final CollisionChecker collisionChecker;
    private final List<Bullet> bullets;

    private long lastFireTime;
    private final long fireCooldown = 500;

    private long lastDamageTime;
    private final long DAMAGE_COOLDOWN = 500;

    private boolean isDead = false;

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
        update(null);
    }

    public void update(Map<String, Player> otherPlayers) {
        if (inputHandler != null) {
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

                // Сохраняем предыдущие координаты
                int prevWorldX = worldX;
                int prevWorldY = worldY;

                // Проверяем коллизии с тайлами
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

                // Проверяем коллизии с другими танками
                if (otherPlayers != null && collisionChecker != null) {
                    Rectangle newPosition = getWorldSolidArea();
                    boolean hasCollision = false;

                    for (Player otherPlayer : otherPlayers.values()) {
                        if (collisionChecker.checkPlayerCollision(newPosition, otherPlayer.getWorldSolidArea())) {
                            hasCollision = true;
                            break;
                        }
                    }

                    // Если есть коллизия с другим танком, возвращаемся на предыдущую позицию
                    if (hasCollision) {
                        worldX = prevWorldX;
                        worldY = prevWorldY;
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
        int screenX = worldX - camera.getX();
        int screenY = worldY - camera.getY();

        BufferedImage image = null;
        switch (direction) {
            case UP -> image = (spriteNum == 1) ? up1 : up2;
            case DOWN -> image = (spriteNum == 1) ? down1 : down2;
            case LEFT -> image = (spriteNum == 1) ? left1 : left2;
            case RIGHT -> image = (spriteNum == 1) ? right1 : right2;
        }

        if (image != null) {
            g2.drawImage(image, screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
        }

        // Отрисовываем все пули
        bullets.forEach(bullet -> bullet.draw(g2));
    }

    private void setDefaultValues() {
        Random random = new Random();
        worldX = random.nextInt(5) * GameConfig.TILE_SIZE;
        worldY = random.nextInt(5) * GameConfig.TILE_SIZE;
        health = 100;
        //worldX = 5 * GameConfig.TILE_SIZE;
        //worldY = 5 * GameConfig.TILE_SIZE;
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

    public void updatePlayer(int newWorldX, int newWorldY, Direction newDirection, int spriteNum) {
        this.worldX = newWorldX;
        this.worldY = newWorldY;
        this.direction = newDirection;
        this.spriteNum = spriteNum;
    }

    public void takeDamage(int damage) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDamageTime < DAMAGE_COOLDOWN) {
            return; // Игрок неуязвим после недавнего получения урона
        }

        if (!isDead) {
            health -= damage;
            lastDamageTime = currentTime;

            if (health <= 0) {
                health = 0;
                isDead = true;
                showGameOverMenu();
            }
        }
    }

    private void showGameOverMenu() {
        GameOverMenu menu = new GameOverMenu(this::respawn, this::exitGame);
        menu.setVisible(true);
    }

    private void respawn() {
        // Удаляем спрайт игрока с карты
        // Здесь можно добавить логику для удаления спрайта из игрового мира
        isDead = false;
        health = 100; // Восстанавливаем здоровье

        // Генерируем новое место для респавна
        Random random = new Random();
        int newX = random.nextInt(GameConfig.MAX_WORLD_COL) * GameConfig.TILE_SIZE;
        int newY = random.nextInt(GameConfig.MAX_WORLD_ROW) * GameConfig.TILE_SIZE;

        // Устанавливаем новые координаты
        this.worldX = newX;
        this.worldY = newY;
    }

    private void exitGame() {
        System.exit(0); // Закрываем игру
    }

    public void addBullet(int x, int y, Direction direction) {
        bullets.add(new Bullet(x, y, direction));
    }

    public List<Bullet> getBullets() {
        return bullets;
    }
}