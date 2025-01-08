import core.Game;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.setTitle("Tanki");

        Game game = new Game();
        window.add(game.getPanel());
        game.start();

        window.pack();

        window.setLocationRelativeTo(null);
        window.setVisible(true);

    }
}
