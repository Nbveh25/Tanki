package entity;

import config.GameConfig;
import core.Camera;
import enums.Direction;
import handler.InputHandler;
import util.CollisionChecker;
import handler.MovementHandler;
import handler.WeaponHandler;
import handler.AnimationHandler;
import manager.GameOverMenuManager;

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
    private final MovementHandler movementHandler;
    private final WeaponHandler weaponHandler;
    private final AnimationHandler animationHandler;
    private final List<Bullet> bullets;
    private String name;
    private boolean isDead = false;
    private final CollisionChecker collisionChecker;

    public Player(InputHandler inputHandler, CollisionChecker collisionChecker, String name) {
        this.collisionChecker = collisionChecker;
        this.movementHandler = new MovementHandler(inputHandler, collisionChecker);
        this.weaponHandler = new WeaponHandler(inputHandler);
        this.animationHandler = new AnimationHandler(inputHandler);
        this.bullets = new ArrayList<>();
        this.name = name;

        setDefaultValues();
        setCollision(true);
        loadSprites();
    }

    private void setDefaultValues() {
        Random random = new Random();
        worldX = random.nextInt(5) * GameConfig.TILE_SIZE;
        worldY = random.nextInt(5) * GameConfig.TILE_SIZE;
        speed = 10;
        direction = Direction.DOWN;
        health = 100;
    }

    private void setCollision(boolean mode) {
        if (mode) {
            solidArea = new Rectangle();
            solidArea.x = 6;
            solidArea.y = 6;
            solidArea.width = 36;
            solidArea.height = 36;
        } else {
            solidArea = null;
        }
    }

    private void loadSprites() {
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

    public void update(Map<String, Player> otherPlayers) {
        if (GameOverMenuManager.getInstance().isRespawnMenuOpen() || isDead) return;

        // Сбрасываю состояние коллизии чтобы игрок не застревал
        collisionOn = false;
        // Обновляем пули
        bullets.removeIf(bullet -> {
            bullet.update(collisionChecker);
            return !bullet.isActive();
        });

        // Обновляем движение
        direction = movementHandler.handleMovement(this, otherPlayers);

        // Обновляем стрельбу
        weaponHandler.handleShooting(this, bullets);

        // Обновляем анимацию
        animationHandler.update();
    }

    @Override
    public void draw(Graphics2D g2) {
        if (isDead) return;

        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);
        int screenX = worldX - camera.getX();
        int screenY = worldY - camera.getY();

        // Отрисовка спрайта
        BufferedImage image = animationHandler.getCurrentSprite(
            direction, up1, up2, down1, down2, left1, left2, right1, right2
        );
        if (image != null) {
            g2.drawImage(image, screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
        }

        // Отрисовка имени
        drawName(g2, screenX, screenY - 10);
        drawHealthBar(g2, screenX, screenY - 7);

        // Отрисовка пуль
        bullets.forEach(bullet -> bullet.draw(g2));
    }

    private void drawName(Graphics2D g2, int screenX, int screenY) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 12));
        FontMetrics metrics = g2.getFontMetrics();
        int nameWidth = metrics.stringWidth(name);
        int nameX = screenX + (GameConfig.TILE_SIZE - nameWidth) / 2;
        int nameY = screenY - 5;

        // Фон для имени
        g2.setColor(new Color(0, 0, 0, 128));
        g2.fillRect(nameX - 2, nameY - metrics.getAscent(), nameWidth + 4, metrics.getHeight());

        // Само имя
        g2.setColor(Color.WHITE);
        g2.drawString(name, nameX, nameY);
        

    }

    private void drawHealthBar(Graphics2D g2, int screenX, int y) {
        int barWidth = GameConfig.TILE_SIZE - 10;
        int barHeight = 5;
        int x = screenX + 5;

        // Фон полоски здоровья
        g2.setColor(Color.GRAY);
        g2.fillRect(x, y, barWidth, barHeight);

        // Определяем цвет в зависимости от количества здоровья
        Color healthColor;
        if (health > 70) {
            healthColor = Color.GREEN;
        } else if (health > 30) {
            healthColor = Color.YELLOW;
        } else {
            healthColor = Color.RED;
        }

        // Заполненная часть полоски здоровья
        g2.setColor(healthColor);
        int currentBarWidth = (int) ((health / 100.0) * barWidth);
        g2.fillRect(x, y, currentBarWidth, barHeight);

        // Обводка полоски здоровья
        g2.setColor(Color.BLACK);
        g2.drawRect(x, y, barWidth, barHeight);
    }

    public void takeDamage(int damage) {
        if (!isDead) {
            health -= damage;
            if (health <= 0) {
                health = 0;
                isDead = true;
                GameOverMenuManager.getInstance().handlePlayerDeath(this);
            }
        }
    }

    public void heal(int amount) {
        if (!isDead) {
            health = Math.min(100, health + amount);
        }
    }

    // Геттеры и сеттеры
    public boolean isDead() {
        return isDead;
    }

    public void updateState(boolean isDead) {
        this.isDead = isDead;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public void addBullet(int x, int y, Direction direction) {
        bullets.add(new Bullet(x, y, direction));
    }

    public void updatePlayer(int newWorldX, int newWorldY, Direction newDirection, int newSpriteNum, int newHealth) {
        this.worldX = newWorldX;
        this.worldY = newWorldY;
        this.direction = newDirection;
        this.spriteNum = newSpriteNum;
        this.health = newHealth;
    }

    public int getHealth() {
        return health;
    }
}