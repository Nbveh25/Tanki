package util;

import tile.Tile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TileLoader {
    public Tile[] loadTiles() {
        Tile[] tiles = new Tile[18];
        try {
            // GRASS
            tiles[0] = createTile("/tile/grass01.png");
            tiles[1] = createTile("/tile/grass02.png");

            // ROAD
            tiles[2] = createTile("/tile/road_horizontal.png");
            tiles[3] = createTile("/tile/road_vertical.png");
            tiles[4] = createTile("/tile/road_cross.png");

            // RIVER
            tiles[5] = createTile("/tile/cost_down.png");
            tiles[6] = createTile("/tile/cost_up.png");

            // RIVER CORNER
            tiles[7] = createTile("/tile/cost_left_down_corner.png");
            tiles[8] = createTile("/tile/cost_right_down_corner.png");
            tiles[9] = createTile("/tile/cost_left_up_corner.png");
            tiles[10] = createTile("/tile/cost_right_up_corner.png");

            // RIVER SIDE
            tiles[11] = createTile("/tile/cost_left.png");
            tiles[12] = createTile("/tile/cost_right.png");

            // WATER
            tiles[13] = createTile("/tile/water.png");

            // BRICK
            tiles[14] = createTile("/tile/red_brick.png");
            tiles[15] = createTile("/tile/gray_brick.png");

            // BUSH
            tiles[16] = createTile("/tile/grass01.png");


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