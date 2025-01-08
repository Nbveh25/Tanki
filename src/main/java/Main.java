import core.Game;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        // Создаем окно для ввода значений
        JFrame frame = new JFrame("Game Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(new GridLayout(3, 2));

        // Поле для ввода IP-адреса
        JLabel ipLabel = new JLabel("Server IP:");
        JTextField ipField = new JTextField("localhost");

        // Чекбокс для выбора хоста
        JLabel hostLabel = new JLabel("Is Host:");
        JCheckBox hostCheckBox = new JCheckBox("", true); // По умолчанию true

        // Кнопка для подтверждения ввода
        JButton startButton = new JButton("Start Game");

        // Добавляем элементы на панель
        frame.add(ipLabel);
        frame.add(ipField);
        frame.add(hostLabel);
        frame.add(hostCheckBox);
        frame.add(new JLabel()); // Пустая ячейка для выравнивания
        frame.add(startButton);

        // Обработчик события для кнопки
        startButton.addActionListener(e -> {
            boolean isHost = hostCheckBox.isSelected();
            String serverIp = ipField.getText();

            // Закрываем окно
            frame.dispose();

            // Запускаем игру с введенными значениями
            JFrame gameWindow = new JFrame();
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setResizable(false);
            gameWindow.setTitle("Tanki" + (isHost ? " (Server)" : " (Client)"));

            Game game = new Game(isHost, serverIp);
            gameWindow.add(game.getPanel());
            game.start();

            gameWindow.pack();
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);
        });

        // Отображаем окно
        frame.setVisible(true);
    }
}