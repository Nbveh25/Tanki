package handler;

import config.GameConfig;
import entity.Player;
import enums.Direction;
import util.CollisionChecker;

import java.awt.Rectangle;
import java.util.Map;

public class MovementHandler {
    private final InputHandler inputHandler;
    private final CollisionChecker collisionChecker;

    public MovementHandler(InputHandler inputHandler, CollisionChecker collisionChecker) {
        this.inputHandler = inputHandler;
        this.collisionChecker = collisionChecker;
    }

    public Direction handleMovement(Player player, Map<String, Player> otherPlayers) {
        Direction direction = player.getDirection();
        
        if (inputHandler == null) return direction;

        if (inputHandler.isUpPressed()) {
            direction = Direction.UP;
        } else if (inputHandler.isDownPressed()) {
            direction = Direction.DOWN;
        } else if (inputHandler.isLeftPressed()) {
            direction = Direction.LEFT;
        } else if (inputHandler.isRightPressed()) {
            direction = Direction.RIGHT;
        } else {
            return direction;
        }

        // Вычисляем новые координаты
        int newWorldX = player.getWorldX();
        int newWorldY = player.getWorldY();

        // Проверяем коллизии с тайлами
        collisionChecker.checkTile(player);

        if (!player.isCollisionOn()) {
            switch (direction) {
                case UP -> newWorldY = player.getWorldY() - player.getSpeed();
                case DOWN -> newWorldY = player.getWorldY() + player.getSpeed();
                case LEFT -> newWorldX = player.getWorldX() - player.getSpeed();
                case RIGHT -> newWorldX = player.getWorldX() + player.getSpeed();
            }
        }

        // Проверяем границы карты
        if (isWithinBounds(newWorldX, newWorldY)) {
            // Проверяем коллизии с другими танками
            if (!checkPlayerCollisions(newWorldX, newWorldY, player, otherPlayers)) {
                player.setPosition(newWorldX, newWorldY);
            }
        }

        return direction;
    }

    private boolean isWithinBounds(int x, int y) {
        return x >= 0 && x <= GameConfig.WORLD_WIDTH - GameConfig.TILE_SIZE &&
               y >= 0 && y <= GameConfig.WORLD_HEIGHT - GameConfig.TILE_SIZE;
    }

    private boolean checkPlayerCollisions(int newX, int newY, Player player, Map<String, Player> otherPlayers) {
        if (otherPlayers == null) return false;

        Rectangle newPosition = new Rectangle(
            newX + player.getSolidArea().x,
            newY + player.getSolidArea().y,
            player.getSolidArea().width,
            player.getSolidArea().height
        );

        for (Player otherPlayer : otherPlayers.values()) {
            if (!otherPlayer.isDead() && collisionChecker.checkPlayerCollision(newPosition, otherPlayer.getWorldSolidArea())) {
                return true;
            }
        }
        return false;
    }
} 