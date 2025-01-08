package util;

import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TileLoader {
    public Tile[] loadTiles() {
        Tile[] tiles = new Tile[10];
        try {
            tiles[0] = createTile("/tile/dirt.png");
            tiles[0].setCollision(true);

            tiles[1] = createTile("/tile/grass.png");

            tiles[2] = createTile("/tile/ice.png");

            tiles[3] = createTile("/tile/sand.png");

        } catch (IOException e) {
            e.printStackTrace();
        }
        return tiles;
    }

    private Tile createTile(String path) throws IOException {
        Tile tile = new Tile();
        BufferedImage image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(path)));
        tile.setImage(image);
        return tile;
    }
}