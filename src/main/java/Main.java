import core.Game;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 250);
        frame.setLayout(new GridLayout(4, 2));

        // Поле для ввода имени
        JLabel nameLabel = new JLabel("Player Name:");
        JTextField nameField = new JTextField("Player1");

        // Поле для ввода IP-адреса
        JLabel ipLabel = new JLabel("Server IP:");
        JTextField ipField = new JTextField("localhost");

        // Чекбокс для выбора хоста
        JLabel hostLabel = new JLabel("Is Host:");
        JCheckBox hostCheckBox = new JCheckBox("", true);

        // Кнопка для подтверждения ввода
        JButton startButton = new JButton("Start Game");

        // Добавляем элементы на панель
        frame.add(nameLabel);
        frame.add(nameField);
        frame.add(ipLabel);
        frame.add(ipField);
        frame.add(hostLabel);
        frame.add(hostCheckBox);
        frame.add(new JLabel());
        frame.add(startButton);

        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a player name!");
                return;
            }

            boolean isHost = hostCheckBox.isSelected();
            String serverIp = ipField.getText();

            frame.dispose();

            JFrame gameWindow = new JFrame();
            gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameWindow.setResizable(false);
            gameWindow.setTitle("Tanki" + (isHost ? " (Server)" : " (Client)"));

            Game game = new Game(isHost, serverIp, playerName);
            gameWindow.add(game.getPanel());
            game.start();

            gameWindow.pack();
            gameWindow.setLocationRelativeTo(null);
            gameWindow.setVisible(true);
        });

        frame.setVisible(true);
    }
}