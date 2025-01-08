package util;

import config.GameConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MapLoader {
    public int[][] loadMap(String filePath) {
        int[][] mapTileNum = new int[GameConfig.MAX_WORLD_COL][GameConfig.MAX_WORLD_ROW];
        
        try (InputStream is = getClass().getResourceAsStream(filePath)) {
            if (is == null) {
                throw new IOException("Не удалось найти файл карты: " + filePath);
            }
            
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            int row = 0;
            
            String line;
            while ((line = br.readLine()) != null && row < GameConfig.MAX_WORLD_ROW) {
                String[] numbers = line.split(" ");
                
                int colCount = Math.min(numbers.length, GameConfig.MAX_WORLD_COL);
                
                for (int col = 0; col < colCount; col++) {
                    try {
                        mapTileNum[col][row] = Integer.parseInt(numbers[col]);
                    } catch (NumberFormatException e) {
                        System.err.println("Ошибка при парсинге числа в позиции [" + col + "," + row + "]: " + numbers[col]);
                        mapTileNum[col][row] = 0;
                    }
                }
                
                for (int col = colCount; col < GameConfig.MAX_WORLD_COL; col++) {
                    mapTileNum[col][row] = 0;
                }
                
                row++;
            }
            
            for (int r = row; r < GameConfig.MAX_WORLD_ROW; r++) {
                for (int col = 0; col < GameConfig.MAX_WORLD_COL; col++) {
                    mapTileNum[col][r] = 0;
                }
            }
            
        } catch (IOException e) {
            System.err.println("Ошибка при загрузке карты: " + e.getMessage());
            e.printStackTrace();
            
            return new int[GameConfig.MAX_WORLD_COL][GameConfig.MAX_WORLD_ROW];
        }
        
        return mapTileNum;
    }
}