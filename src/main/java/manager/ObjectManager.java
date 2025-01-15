package manager;

import config.GameConfig;
import core.Camera;
import entity.Player;
import object.OBJ_Heart;
import network.GameClient;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ObjectManager {
    private final List<OBJ_Heart> hearts = new ArrayList<>();
    private final GameClient gameClient;

    public ObjectManager(GameClient gameClient) {
        this.gameClient = gameClient;
    }

    public void draw(Graphics2D g2) {
        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        for (OBJ_Heart heart : hearts) {
            if (heart.isActive) {
                int screenX = heart.worldX - camera.getX();
                int screenY = heart.worldY - camera.getY();

                if (isVisible(screenX, screenY)) {
                    g2.drawImage(heart.image, screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
                }
            }
        }
    }

    private boolean isVisible(int screenX, int screenY) {
        return screenX >= -GameConfig.TILE_SIZE &&
               screenX <= GameConfig.SCREEN_WIDTH &&
               screenY >= -GameConfig.TILE_SIZE &&
               screenY <= GameConfig.SCREEN_HEIGHT;
    }

    public void addHeart(OBJ_Heart heart) {
        hearts.add(heart);
    }

    public void checkCollision(Player player) {
        Iterator<OBJ_Heart> iterator = hearts.iterator();
        while (iterator.hasNext()) {
            OBJ_Heart heart = iterator.next();
            if (heart.isActive && checkCollision(player, heart)) {
                heart.isActive = false;
                player.heal(20);
                gameClient.sendBonusPickup(heart.worldX, heart.worldY);
                System.out.println("Player collected heart! Health: " + player.getHealth());
            }
        }
    }

    private boolean checkCollision(Player player, OBJ_Heart heart) {
        Rectangle playerRect = player.getWorldSolidArea();
        Rectangle heartRect = new Rectangle(heart.worldX, heart.worldY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
        return playerRect.intersects(heartRect);
    }

    public void removeInactiveHearts() {
        hearts.removeIf(heart -> !heart.isActive);
    }

    public List<OBJ_Heart> getHearts() {
        return hearts;
    }

    public void deactivateHeart(int x, int y) {
        for (OBJ_Heart heart : hearts) {
            if (heart.worldX == x && heart.worldY == y) {
                heart.isActive = false;
                break;
            }
        }
    }
}
