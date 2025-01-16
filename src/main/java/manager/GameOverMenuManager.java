package manager;

import ui.GameOverMenu;
import entity.Player;
import config.GameConfig;
import java.util.Random;

public class GameOverMenuManager {
    private static GameOverMenuManager instance;
    private boolean respawnMenuOpen = false;
    
    private GameOverMenuManager() {}
    
    public static GameOverMenuManager getInstance() {
        if (instance == null) {
            instance = new GameOverMenuManager();
        }
        return instance;
    }
    
    public void handlePlayerDeath(Player player) {
        respawnMenuOpen = true;
        showGameOverMenu(
            () -> respawnPlayer(player),
            this::exitGame
        );
    }
    
    private void showGameOverMenu(Runnable onRespawn, Runnable onExit) {
        GameOverMenu menu = new GameOverMenu(onRespawn, onExit);
        menu.setVisible(true);
    }
    
    private void respawnPlayer(Player player) {
        respawnMenuOpen = false;
        player.updateState(false);
        player.heal(100);
        
        Random random = new Random();
        int newX = random.nextInt(GameConfig.MAX_WORLD_COL) * GameConfig.TILE_SIZE;
        int newY = random.nextInt(GameConfig.MAX_WORLD_ROW) * GameConfig.TILE_SIZE;
        player.setPosition(newX, newY);
    }
    
    private void exitGame() {
        System.exit(0);
    }
    
    public boolean isRespawnMenuOpen() {
        return respawnMenuOpen;
    }
} 