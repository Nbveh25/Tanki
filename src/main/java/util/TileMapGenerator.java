package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TileMapGenerator {
    private static final int WIDTH = 100;
    private static final int HEIGHT = 100;
    private static final int[][] map = new int[HEIGHT][WIDTH];
    private static final Random random = new Random();

    public static void main(String[] args) {
        generateMap();
        saveMapToFile("map.txt");
    }

    private static void generateMap() {
        fillWithGrass();
        createSandPaths();
        createRivers();
        createForests();
        addHouses();
    }

    private static void createSandPaths() {
        // Горизонтальные дорожки
        for (int y = 20; y < HEIGHT; y += 10) {
            for (int x = 0; x < WIDTH; x++) {
                map[y][x] = 2; // песок (индекс 2)
            }
        }

        // Вертикальные дорожки
        for (int x = 20; x < WIDTH; x += 10) {
            for (int y = 0; y < HEIGHT; y++) {
                map[y][x] = 2; // песок (индекс 2)
            }
        }
    }

    private static void createRivers() {
        for (int i = 0; i < 2; i++) { // создаем 2 реки
            int x = random.nextInt(WIDTH - 40) + 20;
            int y = random.nextInt(HEIGHT - 40) + 20;
            int length = random.nextInt(20) + 10; // длина реки

            for (int step = 0; step < length; step++) {
                if (step == 0) {
                    map[y][x] = 3;     // начало реки (индекс 3)
                    map[y][x + 1] = 4; // верхняя часть реки (индекс 4)
                    map[y][x + 2] = 5; // правый верхний угол (индекс 5)
                } else {
                    map[y][x] = 13;    // левый край реки (индекс 13)
                    if (random.nextBoolean()) {
                        // Поворот реки
                        map[y][x + 1] = 9; // левый нижний угол (индекс 9)
                        map[y + 1][x + 1] = 14; // правый нижний угол (индекс 14)
                        x++;
                    } else {
                        // Прямой участок
                        map[y][x + 1] = 15; // вода (индекс 15)
                        map[y][x + 2] = 14; // правый край (индекс 14)
                        x += 2;
                    }
                }
                y++;
            }
        }
    }

    private static void createForests() {
        for (int i = 0; i < 5; i++) { // создаем 5 участков леса
            int startX = random.nextInt(WIDTH - 10) + 5;
            int startY = random.nextInt(HEIGHT - 10) + 5;
            int forestSize = random.nextInt(5) + 3; // размер леса

            for (int y = startY; y < startY + forestSize && y < HEIGHT; y++) {
                for (int x = startX; x < startX + forestSize && x < WIDTH; x++) {
                    map[y][x] = 0; // трава (индекс 0)
                }
            }
        }
    }

    private static void fillWithGrass() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                if (map[y][x] == 0) { // Заполняем травой только пустые клетки
                    map[y][x] = random.nextInt(2); // 0 или 1 (два типа травы)
                }
            }
        }
    }

    private static void addHouses() {
        for (int y = 10; y < HEIGHT; y += 20) {
            for (int x = 10; x < WIDTH; x += 20) {
                if (map[y][x] == 0 || map[y][x] == 1) { // Если там трава
                    map[y][x] = random.nextInt(2) + 16; // случайный выбор между серым (16) и красным (17) кирпичом
                }
            }
        }
    }

    private static void saveMapToFile(String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            for (int y = 0; y < HEIGHT; y++) {
                for (int x = 0; x < WIDTH; x++) {
                    writer.write(String.format("%1d ", map[y][x]));
                }
                writer.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
