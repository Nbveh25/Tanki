package util;

import config.GameConfig;
import entity.Bullet;
import entity.Entity;
import manager.TileManager;

import java.awt.*;

public class CollisionChecker {
    private final TileManager tileManager;

    public CollisionChecker(TileManager tileManager) {
        this.tileManager = tileManager;
    }

    public void checkTile(Entity entity) {
        // Вычисляем координаты углов хитбокса сущности
        int entityLeftWorldX = entity.getWorldX() + entity.getSolidArea().x;
        int entityRightWorldX = entity.getWorldX() + entity.getSolidArea().x + entity.getSolidArea().width;
        int entityTopWorldY = entity.getWorldY() + entity.getSolidArea().y;
        int entityBottomWorldY = entity.getWorldY() + entity.getSolidArea().y + entity.getSolidArea().height;

        // Преобразуем координаты в индексы тайлов
        int entityLeftCol = entityLeftWorldX / GameConfig.TILE_SIZE;
        int entityRightCol = entityRightWorldX / GameConfig.TILE_SIZE;
        int entityTopRow = entityTopWorldY / GameConfig.TILE_SIZE;
        int entityBottomRow = entityBottomWorldY / GameConfig.TILE_SIZE;

        int tileNum1, tileNum2;

        switch (entity.getDirection()) {
            case UP -> {
                entityTopRow = (entityTopWorldY - entity.getSpeed()) / GameConfig.TILE_SIZE;
                tileNum1 = tileManager.getTileNumber(entityLeftCol, entityTopRow);
                tileNum2 = tileManager.getTileNumber(entityRightCol, entityTopRow);
                if (tileManager.isTileHasCollision(tileNum1) || tileManager.isTileHasCollision(tileNum2)) {
                    entity.setCollisionOn(true);
                }
            }
            case DOWN -> {
                entityBottomRow = (entityBottomWorldY + entity.getSpeed()) / GameConfig.TILE_SIZE;
                tileNum1 = tileManager.getTileNumber(entityLeftCol, entityBottomRow);
                tileNum2 = tileManager.getTileNumber(entityRightCol, entityBottomRow);
                if (tileManager.isTileHasCollision(tileNum1) || tileManager.isTileHasCollision(tileNum2)) {
                    entity.setCollisionOn(true);
                }
            }
            case LEFT -> {
                entityLeftCol = (entityLeftWorldX - entity.getSpeed()) / GameConfig.TILE_SIZE;
                tileNum1 = tileManager.getTileNumber(entityLeftCol, entityTopRow);
                tileNum2 = tileManager.getTileNumber(entityLeftCol, entityBottomRow);
                if (tileManager.isTileHasCollision(tileNum1) || tileManager.isTileHasCollision(tileNum2)) {
                    entity.setCollisionOn(true);
                }
            }
            case RIGHT -> {
                entityRightCol = (entityRightWorldX + entity.getSpeed()) / GameConfig.TILE_SIZE;
                tileNum1 = tileManager.getTileNumber(entityRightCol, entityTopRow);
                tileNum2 = tileManager.getTileNumber(entityRightCol, entityBottomRow);
                if (tileManager.isTileHasCollision(tileNum1) || tileManager.isTileHasCollision(tileNum2)) {
                    entity.setCollisionOn(true);
                }
            }
        }
    }

    public boolean checkBulletCollision(Bullet bullet) {
        Rectangle bulletArea = bullet.getCollisionArea();
        for (int row = 0; row < GameConfig.MAX_WORLD_ROW; row++) {
            for (int col = 0; col < GameConfig.MAX_WORLD_COL; col++) {
                if (tileManager.getTileNumber(col, row) == 0) {
                    Rectangle blockArea = new Rectangle(col * GameConfig.TILE_SIZE, row * GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE);
                    if (bulletArea.intersects(blockArea)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean checkBulletPlayerCollision(Bullet bullet, Rectangle playerArea) {
        return bullet.getCollisionArea().intersects(playerArea);
    }

    public boolean checkPlayerCollision(Rectangle player1Area, Rectangle player2Area) {
        return player1Area.intersects(player2Area);
    }
}
