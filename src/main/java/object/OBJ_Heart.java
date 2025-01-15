package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Heart extends SuperObject {

    public OBJ_Heart() {
        name = "Heart";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/object/heart.png")));
            if (image == null) {
                System.err.println("Failed to load heart image!");
            }
        } catch (IOException e) {
            System.err.println("Error loading heart image: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
