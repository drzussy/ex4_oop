package src.pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.util.ColorSupplier;
import src.pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Flora {
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final int CANOPY_HEIGHT = 5;
    private static final int CANOPY_WIDTH = 7;
    private static final Color LEAF_COLOR = new Color(76, 253, 52);
    private static final RectangleRenderable LEAF_RENDERABLE = new RectangleRenderable(LEAF_COLOR);
//    private static final Vector2 LEAF_SIZE = new Vector2(60, 90);
    private static final float LEAF_CHANCE = 0.6F;
    private static final float LEAF_MAX_ANGLE = 45F;
    private static final float LEAF_CYCLE_LENGTH = 2F;
    private static final float LEAF_WIND_WIDTH_FACTOR = 0.75f;
    private static final float LEAF_WIND_HEIGHT_FACTOR = 0.95f;
    private static final Color TREE_TRUNK_COLOR = new Color(124, 51, 10);
    private static final int TREE_COLOR_DELTA = 50;
    private final Vector2 windowDimensions;
//    private final NoiseGenerator noiseGenerator;
    private final Random random;
    private final Function<Float, Float> getGroundHeightAt;
//    private static final int TREE_NOISE_FACTOR = 10;
    private final static Renderable TREE_TRUNK = new RectangleRenderable(new Color(124, 51, 10));
    private final int seed;

    public Flora(Vector2 windowDimensions, int seed, Function<Float, Float> getGroundHeightAt) {
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        this.random = new Random(seed);
        this.getGroundHeightAt = getGroundHeightAt;
    }

    private int treeHeightAtInBlocks (float x) {
        Random tempRand = new Random(Objects.hash(x, seed));
        return tempRand.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT+1);
    }

    public Map<Tree, List<Block>> createInRange(int minX, int maxX){
//        ArrayList<Tree> treeList = new ArrayList<>();
//
        Map<Tree, List<Block>> map = new HashMap<>();
        minX = ((minX + Block.SIZE - 1) / Block.SIZE) * Block.SIZE;
        maxX = (maxX / Block.SIZE) * Block.SIZE;
        for (float x = minX; x < maxX; x=x+Block.SIZE) {
            if (random.nextFloat()<0.2f) { // create tree for 20% of blocks
                float treeHeight = treeHeightAtInBlocks(x) * Block.SIZE;
                Vector2 treeTopLeftCorner = new Vector2(x, getGroundHeightAt.apply(x) - treeHeight);
                Vector2 treeSize = new Vector2(Block.SIZE, treeHeight);
                Renderable treeRender = new RectangleRenderable(
                        ColorSupplier.approximateColor(TREE_TRUNK_COLOR, TREE_COLOR_DELTA));
                Tree tree = new Tree (treeTopLeftCorner, treeSize, treeRender);
                float rightShift = (float) (CANOPY_WIDTH-1)*Block.SIZE/2;
                float downShift = (float) (CANOPY_HEIGHT*Block.SIZE)/2;
                Random tempRand = new Random(Objects.hash(x, seed));
                ArrayList<Block> leafList = new ArrayList<>();
                for (int row=0; row<CANOPY_HEIGHT; row++) {
                    for (int col=0; col<CANOPY_WIDTH; col++) {
                        if (tempRand.nextFloat()<LEAF_CHANCE) {
                            Renderable leafRender = new RectangleRenderable(
                                    ColorSupplier.approximateColor(LEAF_COLOR));
                            Vector2 leafTopLeft = treeTopLeftCorner.add(new Vector2(
                                    col*Block.SIZE-rightShift, row*Block.SIZE-downShift));
                            Block leaf = new Block(leafTopLeft, leafRender);
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
                                        new Vector2(Block.SIZE, Block.SIZE),
                                        new Vector2(Block.SIZE*LEAF_WIND_WIDTH_FACTOR, Block.SIZE*LEAF_WIND_HEIGHT_FACTOR),
                                        Transition.LINEAR_INTERPOLATOR_VECTOR,
                                        LEAF_CYCLE_LENGTH,
                                        Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                                        null);
                            };
                            float delayTime = tempRand.nextFloat()*2;
                            new ScheduledTask(leaf, delayTime, false, createLeafTransitions);
                            leafList.add(leaf);
                        }
                    }
                }
                map.put(tree, leafList);
            }
        }
        return map;
    }
}
