package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import static pepse.util.PepseConstants.*;

/**
 * A utility class for generating trees, leaves, and associated game objects within a given range.
 * The generated flora elements are consistently seeded to ensure reproducible results across executions.
 */

public class Flora {
    private static final String PATH_TO_FRUIT_IMAGE = "assets/pineapple.png";
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final int MAX_CANOPY_HEIGHT = 20;
    private static final int MAX_CANOPY_WIDTH = 10;
    private static final int MIN_CANOPY_HEIGHT = 4;
    private static final int MIN_CANOPY_WIDTH = 3;
    private static final float HALF = 0.5f;
    private static final float CANOPY_TO_TREE_HEIGHT_RATIO = 1.5f;
    private static final Color TREE_TRUNK_COLOR = new Color(124, 51, 10);
    private static final int TREE_COLOR_DELTA = 50;
    private static final Color LEAF_COLOR = new Color(21, 84, 12);
    private static final int LEAF_COLOR_DELTA = 50;
    private static final float LEAF_CHANCE = 0.6F;
    private static final float LEAF_MAX_ANGLE = 30F;
    private static final float LEAF_CYCLE_LENGTH = 4F;
    private static final float LEAF_WIND_WIDTH_FACTOR = 0.75f;
    private static final float LEAF_WIND_HEIGHT_FACTOR = 0.95f;
    private static final float FRUIT_CHANCE = 0.05f;
    private static final float TREE_CHANCE = 0.1f;
    private static final int MAX_RGB_VAL = 255;
    private static final float MAX_LEAF_DELAY = 3;
    private final Function<Float, Float> getGroundHeightAt;
    private final int seed;
    private final Renderable fruitImage;

    /**
     * Constructs a Flora generator with a specific seed and ground height function.
     *
     * @param seed            the seed used for consistent randomization.
     * @param getGroundHeightAt a function mapping an x-coordinate to the ground height at that position.
     * @param readImage       a function to read and create a {@link Renderable} from an image path.
     */
    public Flora(int seed, Function<Float, Float> getGroundHeightAt,
                 BiFunction<String, Boolean, Renderable> readImage) {
        this.seed = seed;
        this.getGroundHeightAt = getGroundHeightAt;
        this.fruitImage = readImage.apply(PATH_TO_FRUIT_IMAGE, true);
    }

    /**
     * Creates trees and their associated leaves and fruits within the given x-coordinate range.
     *
     * @param minX the minimum x-coordinate of the range.
     * @param maxX the maximum x-coordinate of the range.
     * @return a mapping of {@link Tree} objects to their associated game objects (leaves and fruits).
     */
    public Map<Tree, List<GameObject>> createInRange(int minX, int maxX){
        Map<Tree, List<GameObject>> map = new HashMap<>();
        minX = ((minX + BLOCK_SIZE - 1) / BLOCK_SIZE) * BLOCK_SIZE;
        maxX = (maxX / BLOCK_SIZE) * BLOCK_SIZE;
        for (float x = minX; x <= maxX; x=x+BLOCK_SIZE) {
            Random random = new Random(Objects.hash(x, seed));
            if (random.nextFloat()<=TREE_CHANCE) { // create tree for 20% of blocks
                Tree tree = generateTree(x);
                ArrayList<GameObject> leafList = generateLeaves(
                                        x, tree.getTopLeftCorner(), tree.getDimensions().y());
                map.put(tree, leafList);
            }
        }
        return map;
    }

    private Tree generateTree (float x) {
        float treeHeight = treeHeightAtInBlocks(x) * BLOCK_SIZE;
        Vector2 treeTopLeftCorner = new Vector2(x, getGroundHeightAt.apply(x) - treeHeight);
        Vector2 tree_dimensions = new Vector2(BLOCK_SIZE, treeHeight);
        Renderable treeRender = new RectangleRenderable(
                seededApproximateColor(TREE_TRUNK_COLOR, TREE_COLOR_DELTA, new Random((Objects.hash(x, seed)))));
        return new Tree (treeTopLeftCorner, tree_dimensions, treeRender);
    }

    private int treeHeightAtInBlocks (float x) {
        Random tempRand = new Random(Objects.hash(x, seed));
        return tempRand.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT+1);
    }

    // Helper method for creating leaves around a particular tree.
    private ArrayList<GameObject> generateLeaves(float x, Vector2 treeTopLeftCorner, float treeHeight) {
        ArrayList<GameObject> foliageList = new ArrayList<>();
        Random random = new Random(Objects.hash(x, seed));
        int canopyHeight = random.nextInt(MIN_CANOPY_HEIGHT, MAX_CANOPY_HEIGHT+1);
        canopyHeight = Math.min(canopyHeight, (int) (CANOPY_TO_TREE_HEIGHT_RATIO*treeHeight/BLOCK_SIZE));
        int canopyWidth = random.nextInt(MIN_CANOPY_WIDTH, MAX_CANOPY_WIDTH+1);
        int rightShift = (int) (canopyWidth*HALF);
        int downShift = (int) (canopyHeight*HALF);
        Renderable leafRender = new RectangleRenderable(
                seededApproximateColor(LEAF_COLOR, LEAF_COLOR_DELTA, random));
        for (int row = 0; row< canopyHeight; row++) {
            for (int col = 0; col< canopyWidth; col++) {
                if (random.nextFloat()<LEAF_CHANCE) {
                    Vector2 leafTopLeft = treeTopLeftCorner.add(new Vector2(
                            (col-rightShift)*BLOCK_SIZE, (row-downShift)*BLOCK_SIZE));
                    Block leaf = new Block(leafTopLeft, leafRender);
                    leaf.setTag(LEAF_TAG);
                    Runnable createAngleTransitions = () -> new Transition<>(leaf,
                            leaf.renderer()::setRenderableAngle,
                            -LEAF_MAX_ANGLE, LEAF_MAX_ANGLE,
                            Transition.CUBIC_INTERPOLATOR_FLOAT, LEAF_CYCLE_LENGTH,
                            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
                    Runnable createSizeTransitions = () -> new Transition<>(leaf, leaf::setDimensions,
                            new Vector2(BLOCK_SIZE, BLOCK_SIZE),
                            new Vector2(BLOCK_SIZE*LEAF_WIND_WIDTH_FACTOR,
                                    BLOCK_SIZE*LEAF_WIND_HEIGHT_FACTOR),
                            Transition.LINEAR_INTERPOLATOR_VECTOR, LEAF_CYCLE_LENGTH,
                            Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
                    new ScheduledTask(leaf, random.nextFloat()*MAX_LEAF_DELAY, false, createAngleTransitions);
                    new ScheduledTask(leaf, random.nextFloat()*MAX_LEAF_DELAY, false, createSizeTransitions);
                    foliageList.add(leaf);
                }
                if (random.nextFloat() < FRUIT_CHANCE && col!= rightShift) { // Create a fruit
                    Vector2 leafTopLeft = treeTopLeftCorner.add(new Vector2(
                            (col-rightShift)*BLOCK_SIZE, (row-downShift)*BLOCK_SIZE));
                    Fruit fruit = new Fruit(leafTopLeft, fruitImage);
                    fruit.setTag(FRUIT_TAG);
                    foliageList.add(fruit);
                }
            }
        }
        return foliageList;
    }

    /*
        We altered these methods, copied from ColorSupplier, so we could seed the randomness
        and have the trees be consistently re-generated,
        even with regard to color (and not just position and height) .
     */
    private Color seededApproximateColor(Color baseColor, int colorDelta, Random random) {
        return new Color(
                seededRandomChannelInRange(baseColor.getRed()-colorDelta,
                        baseColor.getRed()+colorDelta, random),
                seededRandomChannelInRange(baseColor.getGreen()-colorDelta,
                        baseColor.getGreen()+colorDelta, random),
                seededRandomChannelInRange(baseColor.getBlue()-colorDelta,
                        baseColor.getBlue()+colorDelta, random));
    }
    private static int seededRandomChannelInRange(int min, int max, Random random) {
        int channel = random.nextInt(max-min+1) + min;
        return Math.min(MAX_RGB_VAL, Math.max(channel, 0));
    }
}
