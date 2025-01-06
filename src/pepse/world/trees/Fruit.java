package src.pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Fruit extends Block {

    public static final String FRUIT_TAG = "fruit";

    public Fruit(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, renderable);
        this.setTag(FRUIT_TAG);
    }


    public static Color getRandomBrightColor() {
        Random random = new Random();

        // Ensure at least one component (R, G, B) is high
        int r = random.nextInt(156) + 100; // Random value between 100 and 255
        int g = random.nextInt(156) + 100; // Random value between 100 and 255
        int b = random.nextInt(156); // Random value between 100 and 255

        return new Color(r, g, b);
    }

}
