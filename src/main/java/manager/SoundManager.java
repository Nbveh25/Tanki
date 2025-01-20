package manager;

import javax.sound.sampled.*;
import java.io.IOException;
import java.util.Objects;

public class SoundManager {
    private Clip backgroundMusic;
    private Clip shootSound;
    private Clip explosionSound;
    private Clip bonusPickupSound; // Новый звук для поднятия бонуса

    public SoundManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Загрузка звука выстрела
            shootSound = loadSound("/sound/shoot.wav");
            // Загрузка звука взрыва
            //explosionSound = loadSound("/sound/explosion.wav");
            // Загрузка звука поднятия бонуса
            bonusPickupSound = loadSound("/sound/bonus_pickup.wav"); // Новый звук
            // Загрузка фоновой музыки
            //backgroundMusic = loadSound("/sound/background.wav");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Clip loadSound(String path) throws IOException, UnsupportedAudioFileException, LineUnavailableException {
        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
                Objects.requireNonNull(getClass().getResource(path))
        );
        Clip clip = AudioSystem.getClip();
        clip.open(audioInputStream);
        return clip;
    }

    public void playBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            backgroundMusic.start();
        }
    }

    public void playShootSound() {
        if (shootSound != null) {
            shootSound.setFramePosition(0);
            shootSound.start();
        }
    }

    public void playExplosionSound() {
        if (explosionSound != null) {
            explosionSound.setFramePosition(0);
            explosionSound.start();
        }
    }

    public void playBonusPickupSound() {
        if (bonusPickupSound != null) {
            bonusPickupSound.setFramePosition(0);
            bonusPickupSound.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
        }
    }
}