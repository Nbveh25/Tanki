package object;

import java.awt.image.BufferedImage;

public abstract class SuperObject {
    public BufferedImage image;
    public String name;
    public boolean collision = false;
    public int worldX, worldY;
    public boolean isActive = true;
}
