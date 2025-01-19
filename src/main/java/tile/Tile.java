package tile;

import java.awt.image.BufferedImage;

public class Tile {
    private BufferedImage image;
    private boolean collision = false;
    private boolean isBulletPassable = false;


    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public boolean isCollision() {
        return collision;
    }

    public void setCollision(boolean collision) {
        this.collision = collision;
    }

    public boolean isBulletPassable() {return isBulletPassable; }

    public void setBulletPassable(boolean bulletPassable) { this.isBulletPassable = bulletPassable; }
}
