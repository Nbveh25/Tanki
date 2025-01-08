package input;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean firePressed;

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        setKeyState(e.getKeyCode(), true);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        setKeyState(e.getKeyCode(), false);
    }
    
    private void setKeyState(int keyCode, boolean pressed) {
        if (keyCode == KeyEvent.VK_W) {
            upPressed = pressed;
        }
        else if (keyCode == KeyEvent.VK_S) {
            downPressed = pressed;
        }
        else if (keyCode == KeyEvent.VK_A) {
            leftPressed = pressed;
        }
        else if (keyCode == KeyEvent.VK_D) {
            rightPressed = pressed;
        }

        if (keyCode == KeyEvent.VK_SPACE) {
            firePressed = pressed;
        }
    }
    
    public boolean isUpPressed() { return upPressed; }
    public boolean isDownPressed() { return downPressed; }
    public boolean isLeftPressed() { return leftPressed; }
    public boolean isRightPressed() { return rightPressed; }
    public boolean isFirePressed() { return firePressed; }
} 