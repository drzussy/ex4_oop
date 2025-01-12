package pepse.world;

import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pepse.util.PepseConstants.BLOCK_SIZE;

/**
 * A class for creating terrain, in the form of Block objects (which extend GameObject) at differing,
 * continuous, consistent yet random heights. After construction, can either create terrain or get the
 * ground height at a specific location.
 */
public class Terrain {
    private static final int DEFAULT_DEPTH = 20;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final float DEFAULT_PART_OF_SCREEN_TO_FILL = ((float)2)/3;
    private static final int NOISE_FACTOR = 7;
    private final int groundHeightAtX0;
    private final NoiseGenerator noiseGenerator;

    /**
     * Creates a new Terrain object, to be further used in creating terrain in a desired range.
     * @param windowDimensions The dimensions of the game window.
     * @param seed The current game's random seed. Used for consistent random generation.
     */
    public Terrain(Vector2 windowDimensions, int seed) {
        this.groundHeightAtX0 = (int) (DEFAULT_PART_OF_SCREEN_TO_FILL * windowDimensions.y());
        this.noiseGenerator = new NoiseGenerator(seed, this.groundHeightAtX0);
    }

    /**
     * Getter method for the randomly-generated height at a specific X coordinate.
     * @param x A horizontal coordinate.
     * @return The height of the ground surface at the passed coordinate.
     */
    public float groundHeightAt(float x) {
        x = x - (x % BLOCK_SIZE);
        float noise = (float) noiseGenerator.noise(x, BLOCK_SIZE * NOISE_FACTOR);
        float rawHeight = groundHeightAtX0 + noise;
        return (float) (Math.floor(rawHeight/ BLOCK_SIZE) * BLOCK_SIZE);
    }

    /**
     * Creates terrain in the desired range in the form of ground blocks, and returns those blocks.
     * Always returns the same blocky terrain for the same coordinates.
     * @param minX The leftmost x coordinate in the range to generate terrain for.
     * @param maxX The rightmost x coordinate in the range to generate terrain for.
     * @return The list of ground blocks in the generated terrain.
     */
    public List<Block> createInRange(int minX, int maxX){
        ArrayList<Block> blockList = new ArrayList<>();
        minX = minX-minX% BLOCK_SIZE; // add buffer to left
        maxX = maxX+ BLOCK_SIZE - maxX% BLOCK_SIZE; //add buffer to the right
        //iterate block columns in range
        for (int x = minX; x <= maxX; x+= BLOCK_SIZE) {
            // for each x make the column of terrain so that terrain is filled to the desired depth
            double height = groundHeightAt(x);
            for (int j = (int) height; j < (int) height + DEFAULT_DEPTH * BLOCK_SIZE; j+= BLOCK_SIZE) {
                Block block = new Block(new Vector2(x, j),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                blockList.add(block);
            }
        }
        return blockList;
    }
}
