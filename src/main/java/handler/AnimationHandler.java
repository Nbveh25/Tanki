package handler;

import java.awt.image.BufferedImage;
import enums.Direction;

public class AnimationHandler {
    private int spriteCounter = 0;
    private int spriteNum = 1;
    private static final int ANIMATION_SPEED = 14;
    private final InputHandler inputHandler;
    private final boolean isRemotePlayer;

    public AnimationHandler(InputHandler inputHandler) {
        this.inputHandler = inputHandler;
        this.isRemotePlayer = (inputHandler == null);
    }

    public void update() {
        if (!isRemotePlayer && isMoving()) {
            spriteCounter++;
            if (spriteCounter > ANIMATION_SPEED) {
                spriteNum = (spriteNum == 1) ? 2 : 1;
                spriteCounter = 0;
            }
        }
    }

    private boolean isMoving() {
        return inputHandler != null && (
            inputHandler.isUpPressed() ||
            inputHandler.isDownPressed() ||
            inputHandler.isLeftPressed() ||
            inputHandler.isRightPressed()
        );
    }

    public BufferedImage getCurrentSprite(Direction direction, BufferedImage up1, BufferedImage up2,
                                        BufferedImage down1, BufferedImage down2,
                                        BufferedImage left1, BufferedImage left2,
                                        BufferedImage right1, BufferedImage right2) {
        return switch (direction) {
            case UP -> (spriteNum == 1) ? up1 : up2;
            case DOWN -> (spriteNum == 1) ? down1 : down2;
            case LEFT -> (spriteNum == 1) ? left1 : left2;
            case RIGHT -> (spriteNum == 1) ? right1 : right2;
        };
    }

    public int getSpriteNum() {
        return spriteNum;
    }

    public void setSpriteNum(int num) {
        if (isRemotePlayer) {
            this.spriteNum = num;
        }
    }
} 