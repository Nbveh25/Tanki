package util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class TileMapGenerator {
    public static void main(String[] args) {
        int rows = 50;
        int cols = 50;
        int[][] tileMap = new int[rows][cols];
        Random random = new Random();

        // Заполнение массива случайными числами от 0 до 3
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                tileMap[i][j] = random.nextInt(4); // Генерация числа от 0 до 3
            }
        }

        // Запись в текстовый файл
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("tile_map.txt"))) {
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    writer.write(tileMap[i][j] + " ");
                }
                writer.newLine(); // Переход на новую строку после каждой строки массива
            }
            System.out.println("Файл успешно создан: tile_map.txt");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
