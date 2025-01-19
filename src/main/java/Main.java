import core.Game;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Game Settings");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);
        frame.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 240, 240));

        JLabel nameLabel = new JLabel("Player Name:");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField nameField = new JTextField("Player1");
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel ipLabel = new JLabel("Server IP:");
        ipLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JTextField ipField = new JTextField("localhost");
        ipField.setFont(new Font("Arial", Font.PLAIN, 14));

        JLabel hostLabel = new JLabel("Is Host:");
        hostLabel.setFont(new Font("Arial", Font.BOLD, 14));
        JCheckBox hostCheckBox = new JCheckBox("", true);
        hostCheckBox.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton startButton = new JButton("Start Game");
        startButton.setFont(new Font("Arial", Font.BOLD, 14));
        startButton.setBackground(new Color(50, 150, 250));
        startButton.setForeground(Color.WHITE);
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(nameLabel);
        mainPanel.add(nameField);
        mainPanel.add(ipLabel);
        mainPanel.add(ipField);
        mainPanel.add(hostLabel);
        mainPanel.add(hostCheckBox);
        mainPanel.add(new JLabel());
        mainPanel.add(startButton);

        frame.add(mainPanel, BorderLayout.CENTER);

        startButton.addActionListener(e -> {
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a player name!", "Error", JOptionPane.ERROR_MESSAGE);
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

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}