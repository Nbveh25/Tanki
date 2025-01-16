package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TileMapGenerator {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;
    private static final int[][] map = new int[HEIGHT][WIDTH];
    private static final Random random = new Random();

    // Индексы тайлов травы
    private static final int GRASS1 = 0;
    private static final int GRASS2 = 1;

    public static void generateMap(String filename) {
        generateGrassland();
        createGrassPatterns();
        saveMapToFile(filename);
    }

    private static void generateGrassland() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                // Базовый слой травы - преимущественно GRASS1
                map[y][x] = random.nextInt(100) < 80 ? GRASS1 : GRASS2;
            }
        }
    }

    private static void createGrassPatterns() {
        // Создаем несколько кластеров второго типа травы
        int numClusters = random.nextInt(10) + 5;
        
        for (int i = 0; i < numClusters; i++) {
            int centerX = random.nextInt(WIDTH);
            int centerY = random.nextInt(HEIGHT);
            int radius = random.nextInt(4) + 2;
            
            // Создаем круглый кластер травы второго типа
            for (int y = centerY - radius; y <= centerY + radius; y++) {
                for (int x = centerX - radius; x <= centerX + radius; x++) {
                    if (y >= 0 && y < HEIGHT && x >= 0 && x < WIDTH) {
                        // Проверяем, находится ли точка в пределах круга
                        if (Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2) <= Math.pow(radius, 2)) {
                            // 70% шанс разместить траву второго типа
                            if (random.nextInt(100) < 70) {
                                map[y][x] = GRASS2;
                            }
                        }
                    }
                }
            }
        }
    }

    private static void saveMapToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    writer.write(String.format("%d ", map[y][x]));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        generateMap("map.txt");
    }
}

