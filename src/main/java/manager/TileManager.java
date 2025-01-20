package manager;

import config.GameConfig;
import core.Camera;
import tile.Tile;
import util.MapLoader;
import util.TileLoader;

import java.awt.*;

public class TileManager {
    private static final TileManager instance = new TileManager();
    private final Tile[] tiles;
    private final int[][] mapTileNum;

    private TileManager() {
        TileLoader tileLoader = new TileLoader();
        this.tiles = tileLoader.loadTiles();

        MapLoader mapLoader = new MapLoader();
        this.mapTileNum = mapLoader.loadMap("/map/tile_map.txt");
    }

    public static TileManager getInstance() {
        return instance;
    }

    public void draw(Graphics2D g2) {
        Camera camera = Camera.getInstance(GameConfig.SCREEN_WIDTH, GameConfig.SCREEN_HEIGHT);

        int startCol = Math.max(0, camera.getX() / GameConfig.TILE_SIZE);
        int startRow = Math.max(0, camera.getY() / GameConfig.TILE_SIZE);
        int endCol = Math.min(GameConfig.MAX_WORLD_COL - 1, (camera.getX() + GameConfig.SCREEN_WIDTH) / GameConfig.TILE_SIZE);
        int endRow = Math.min(GameConfig.MAX_WORLD_ROW - 1, (camera.getY() + GameConfig.SCREEN_HEIGHT) / GameConfig.TILE_SIZE);

        for (int row = startRow; row <= endRow; row++) {
            for (int col = startCol; col <= endCol; col++) {
                int tileNum = mapTileNum[col][row];
                int worldX = col * GameConfig.TILE_SIZE;
                int worldY = row * GameConfig.TILE_SIZE;

                int screenX = worldX - camera.getX();
                int screenY = worldY - camera.getY();

                g2.drawImage(tiles[tileNum].getImage(), screenX, screenY, GameConfig.TILE_SIZE, GameConfig.TILE_SIZE, null);
            }
        }
    }

    public int getTileNumber(int col, int row) {
        if (col >= 0 && col < GameConfig.MAX_WORLD_COL && row >= 0 && row < GameConfig.MAX_WORLD_ROW) {
            return mapTileNum[col][row];
        }
        return 0;
    }

    public boolean isTileHasCollision(int tileNum) {
        if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null) {
            return tiles[tileNum].isCollision();
        }
        return false;
    }

    public boolean isTileBulletPassable(int tileNum) {
        if (tileNum >= 0 && tileNum < tiles.length && tiles[tileNum] != null) {
            return tiles[tileNum].isBulletPassable();
        }
        return false;
    }

    public boolean isTileFreeForSpawn(int col, int row) {
        int tileNum = getTileNumber(col, row);
        return !isTileHasCollision(tileNum); // Проверяем, что тайл не имеет коллизии
    }
}