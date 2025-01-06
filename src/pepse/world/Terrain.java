package src.pepse.world;

import src.pepse.util.ColorSupplier;
import src.pepse.util.NoiseGenerator;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * this class does not extend gameObject, and only creates Block objects (which do extend GameObject)
 */
public class Terrain {
    public static final int DEFAULT_DEPTH = 20;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final float DEFAULT_PART_OF_SCREEN_TO_FILL = ((float)2)/3;
    public static final int NOISE_FACTOR = 7;
    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = (int) (DEFAULT_PART_OF_SCREEN_TO_FILL * windowDimensions.y());
        this.noiseGenerator = new NoiseGenerator(seed, this.groundHeightAtX0);
    }

    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * NOISE_FACTOR);
        float rawHeight = groundHeightAtX0 + noise;
        return (float) (Math.floor(rawHeight/Block.SIZE) * Block.SIZE);
    }

    public List<Block> createInRange(int minX, int maxX){
        ArrayList<Block> blockList = new ArrayList<>();
        minX = minX-minX%Block.SIZE; // add buffer to left
        maxX = maxX+ Block.SIZE- maxX%Block.SIZE; //add buffer to the right
        //iterate block columns in range
        for (int x = minX; x < maxX; x+=Block.SIZE) {
            //for each x make the column of terrain so that terrain is filled through till the bottom of
            // the screen
            double height = groundHeightAt(x);
            //  for (int j = (int) height; j < windowDimensions.y(); j+=Block.SIZE) {
            //  ^ this is a better implementation but the targil instructions gave a default depth of 20 bricks
            for (int j = (int) height; j < (int) height + DEFAULT_DEPTH * Block.SIZE; j+=Block.SIZE) {
                Block block = new Block(new Vector2(x, j),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                blockList.add(block);
            }
        }
        return blockList;
    }

}
