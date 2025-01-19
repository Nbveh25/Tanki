package manager;

import config.GameConfig;
import network.GameServer;
import network.packet.impl.BonusPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BonusManager {
    private static final int MAX_BONUSES = 8;
    private final List<BonusInfo> bonuses;
    private final GameServer gameServer;
    private boolean running;
    private Thread spawnThread;

    public BonusManager(GameServer gameServer) {
        this.bonuses = new ArrayList<>();
        this.gameServer = gameServer;
        this.running = true;
    }

    public void start() {
        spawnThread = new Thread(this::runSpawnLoop);
        spawnThread.start();
    }

    public void stop() {
        running = false;
        if (spawnThread != null) {
            spawnThread.interrupt();
        }
    }

    private void runSpawnLoop() {
        while (running) {
            try {
                Thread.sleep(10000); // Каждые 10 секунд
                bonuses.removeIf(bonus -> !bonus.isActive);
                for (int i = 0; i < 3 && bonuses.size() < MAX_BONUSES; i++) {
                    spawnBonus();
                }
            } catch (InterruptedException e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void spawnBonus() {
        if (!gameServer.hasClients()) return;

        long activeCount = bonuses.stream().filter(b -> b.isActive).count();
        if (activeCount >= MAX_BONUSES) {
            return;
        }

        Random random = new Random();
        int maxAttempts = 100;
        int attempts = 0;

        while (attempts < maxAttempts) {
            int x = random.nextInt(GameConfig.MAX_WORLD_COL) * GameConfig.TILE_SIZE;
            int y = random.nextInt(GameConfig.MAX_WORLD_ROW) * GameConfig.TILE_SIZE;

            int col = x / GameConfig.TILE_SIZE;
            int row = y / GameConfig.TILE_SIZE;

            TileManager tileManager = TileManager.getInstance();
            int tileNum = tileManager.getTileNumber(col, row);
            if (!tileManager.isTileHasCollision(tileNum)) {
                BonusInfo bonus = new BonusInfo(x, y);
                bonuses.add(bonus);

                BonusPacket packet = new BonusPacket(x, y, true, 0);
                gameServer.broadcastPacket(packet);
                System.out.println("Spawned bonus at: " + x + ", " + y + " (tile: " + col + ", " + row + ")");
                System.out.println("Active bonuses: " + (activeCount + 1) + "/" + MAX_BONUSES);
                return;
            }

            attempts++;
        }
    }

    public void deactivateBonus(int x, int y) {
        for (BonusInfo bonus : bonuses) {
            if (bonus.x == x && bonus.y == y) {
                bonus.isActive = false;
                break;
            }
        }
    }

    public List<BonusInfo> getActiveBonuses() {
        return bonuses.stream()
                .filter(b -> b.isActive)
                .toList();
    }

    public static class BonusInfo {
        public final int x;
        public final int y;
        public boolean isActive;

        public BonusInfo(int x, int y) {
            this.x = x;
            this.y = y;
            this.isActive = true;
        }
    }
}