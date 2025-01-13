package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameOverMenu extends JFrame {
    private final Runnable onRespawn;
    private final Runnable onExit;

    public GameOverMenu(Runnable onRespawn, Runnable onExit) {
        this.onRespawn = onRespawn;
        this.onExit = onExit;

        setTitle("Game Over");
        setSize(300, 150);
        setLayout(new GridLayout(3, 1));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel messageLabel = new JLabel("You have died!", SwingConstants.CENTER);
        add(messageLabel);

        JButton respawnButton = new JButton("Respawn");
        respawnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onRespawn.run();
                dispose(); // Закрываем меню
            }
        });
        add(respawnButton);

        JButton exitButton = new JButton("Exit Game");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onExit.run();
                dispose(); // Закрываем меню
            }
        });
        add(exitButton);
    }
}