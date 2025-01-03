package src.pepse.world;

import src.pepse.util.ColorSupplier;
import src.pepse.util.NoiseGenerator;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * this class does not extend gameObject buy only creates block (which are gameObjects)
 */
public class Terrain {
    public static final int DEFAULT_DEPTH = 20;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String BLOCK_TAG = "block";
    private static final float DEFAULT_PART_OF_SCREEN_TO_FILL = ((float)2)/3;
    public static final int NOISE_FACTOR = 7;
    private final int groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final NoiseGenerator noiseGenerator;

    //    private int groundHeightAtX0;
    public Terrain(Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        this.groundHeightAtX0 = (int) (DEFAULT_PART_OF_SCREEN_TO_FILL * windowDimensions.y());
        this.noiseGenerator = new NoiseGenerator(seed, this.groundHeightAtX0);
    }

    //basic method to define ground height for a given x
//    public float groundHeightAt(float x){return groundHeightAtX0;}

    public float groundHeightAt(float x) {
        float noise = (float) noiseGenerator.noise(x, Block.SIZE * NOISE_FACTOR);
        return groundHeightAtX0 + noise; }



    public List<Block> createInRange(int minX, int maxX){
        ArrayList<Block> blockList = new ArrayList<>();
        minX = minX-minX%Block.SIZE; // add buffer to left
        maxX = maxX+ Block.SIZE- maxX%Block.SIZE; //add buffer to the right
        //iterate of x's in range
        for (int i = minX; i < maxX; i=i+Block.SIZE) {
            //for each x make the column of terrain so that terrain is filled through till the bottom of
            // the screen
            double height = Math.floor(groundHeightAt(i)/Block.SIZE) * Block.SIZE;
//            for (int j = (int) height; j < windowDimensions.y(); j+=Block.SIZE) { //this is a better
//            implementation but the targil instructions gave a default depth of 20 bricks
            for (int j = (int) height; j < (int) height + DEFAULT_DEPTH * Block.SIZE; j+=Block.SIZE) {
                Block block = new Block(new Vector2(i, j),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                block.setTag(BLOCK_TAG);
                blockList.add(block);
            }

        }
        return blockList;
    }

}
