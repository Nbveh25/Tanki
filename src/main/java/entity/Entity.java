package entity;

import enums.Direction;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Entity {
    protected int worldX;
    protected int worldY;
    protected int speed;
    protected int health;

    protected BufferedImage up1, up2, down1, down2, left1, left2, right1, right2;
    protected Direction direction;

    protected int spriteCounter = 0;
    protected int spriteNum = 1;

    protected Rectangle solidArea;
    protected boolean collisionOn = false;

    public int getWorldX() { return worldX; }
    public int getWorldY() { return worldY; }

    public Rectangle getSolidArea() {
        return solidArea;
    }

    public void setCollisionOn(boolean collisionOn) {
        this.collisionOn = collisionOn;
    }

    public int getSpeed() {
        return speed;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public int getHealth() {
        return health;
    }

    public Rectangle getWorldSolidArea() {
        return new Rectangle(
            worldX + solidArea.x,
            worldY + solidArea.y,
            solidArea.width,
            solidArea.height
        );
    }
}