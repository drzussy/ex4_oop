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

import static pepse.PepseConstants.*;

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
    private static final float LEAF_MAX_ANGLE = 45F;
    private static final float LEAF_CYCLE_LENGTH = 2F;
    private static final float LEAF_WIND_WIDTH_FACTOR = 0.75f;
    private static final float LEAF_WIND_HEIGHT_FACTOR = 0.95f;
    private static final float FRUIT_CHANCE = 0.05f;
    private static final float TREE_CHANCE = 0.1f;
    private static final int MAX_RGB_VAL = 255;
    private final Function<Float, Float> getGroundHeightAt;
    private final int seed;
    private final Renderable fruitImage;

    public Flora(int seed, Function<Float, Float> getGroundHeightAt,
                 BiFunction<String, Boolean, Renderable> readImage) {
        this.seed = seed;
        this.getGroundHeightAt = getGroundHeightAt;
        this.fruitImage = readImage.apply(PATH_TO_FRUIT_IMAGE, true);
    }


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
                approximateColor(TREE_TRUNK_COLOR, TREE_COLOR_DELTA, new Random((Objects.hash(x, seed)))));
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
        float rightShift = canopyWidth*HALF*BLOCK_SIZE;
        float downShift = canopyHeight*HALF*BLOCK_SIZE;
        Renderable leafRender = new RectangleRenderable(
                approximateColor(LEAF_COLOR, LEAF_COLOR_DELTA, random));
        for (int row = 0; row< canopyHeight; row++) {
            for (int col = 0; col< canopyWidth; col++) {
                if (random.nextFloat()<LEAF_CHANCE) {
                    Vector2 leafTopLeft = treeTopLeftCorner.add(new Vector2(
                            col*BLOCK_SIZE- rightShift, row*BLOCK_SIZE- downShift));
                    Block leaf = new Block(leafTopLeft, leafRender);
                    leaf.setTag(LEAF_TAG);
                    Runnable createLeafTransitions = () -> {
                        new Transition<>(leaf,
                                leaf.renderer()::setRenderableAngle,
                                0f,
                                LEAF_MAX_ANGLE,
                                Transition.LINEAR_INTERPOLATOR_FLOAT,
                                LEAF_CYCLE_LENGTH,
                                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                null);
                        new Transition<>(leaf,
                                leaf::setDimensions,
                                new Vector2(BLOCK_SIZE, BLOCK_SIZE),
                                new Vector2(BLOCK_SIZE*LEAF_WIND_WIDTH_FACTOR,
                                                BLOCK_SIZE*LEAF_WIND_HEIGHT_FACTOR),
                                Transition.LINEAR_INTERPOLATOR_VECTOR,
                                LEAF_CYCLE_LENGTH,
                                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                null);
                    };
                    float delayTime = random.nextFloat()*2;
                    new ScheduledTask(leaf, delayTime, false, createLeafTransitions);
                    foliageList.add(leaf);
                }
                if (random.nextFloat() < FRUIT_CHANCE) { // Create a fruit
                    Vector2 leafTopLeft = treeTopLeftCorner.add(new Vector2(
                            col*BLOCK_SIZE- rightShift, row*BLOCK_SIZE- downShift));
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
    private Color approximateColor(Color baseColor, int colorDelta, Random random) {
        return new Color(
                randomChannelInRange(baseColor.getRed()-colorDelta, baseColor.getRed()+colorDelta,
                        random),
                randomChannelInRange(baseColor.getGreen()-colorDelta, baseColor.getGreen()+colorDelta,
                        random),
                randomChannelInRange(baseColor.getBlue()-colorDelta, baseColor.getBlue()+colorDelta,
                        random));
    }
    private static int randomChannelInRange(int min, int max, Random random) {
        int channel = random.nextInt(max-min+1) + min;
        return Math.min(MAX_RGB_VAL, Math.max(channel, 0));
    }
}
