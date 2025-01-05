package src.pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Function;

public class Flora {
    private static final int MIN_TREE_HEIGHT = 5;
    private static final int MAX_TREE_HEIGHT = 12;
    private static final Color LEAF_COLOR = new Color(76, 253, 52);
    private static final RectangleRenderable LEAF_RENDERABLE = new RectangleRenderable(LEAF_COLOR);
    private static final Vector2 LEAF_SIZE = new Vector2(60, 90);
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
        Random tempRand = new Random((long) (x/Block.SIZE*(seed%Block.SIZE)));
        return tempRand.nextInt(MIN_TREE_HEIGHT, MAX_TREE_HEIGHT+1);
    }

    public Map<Tree, GameObject> createInRange(int minX, int maxX){
//        ArrayList<Tree> treeList = new ArrayList<>();
//        ArrayList<GameObject> leafList = new ArrayList<>();
        Map<Tree, GameObject> map = new HashMap<>();
        minX = ((minX + Block.SIZE - 1) / Block.SIZE) * Block.SIZE;
        maxX = (maxX / Block.SIZE) * Block.SIZE;
        for (float i = minX; i < maxX; i=i+Block.SIZE) {
            if (random.nextFloat()<0.2f) { // create tree for 20% of blocks
                float treeHeight = treeHeightAtInBlocks(i) * Block.SIZE;
                Vector2 treeTopLeftCorner = new Vector2(i, getGroundHeightAt.apply(i) - treeHeight);
                Vector2 treeSize = new Vector2(Block.SIZE, treeHeight);
                Tree tree = new Tree (treeTopLeftCorner, treeSize, TREE_TRUNK);
//                treeList.add(tree);
                float leftShift = (LEAF_SIZE.x()-Block.SIZE)/2;
                Vector2 leafTopLeft = treeTopLeftCorner.subtract(new Vector2(leftShift, 0));
                GameObject leaves = new GameObject(leafTopLeft, LEAF_SIZE, LEAF_RENDERABLE);
                map.put(tree, leaves);
//                leafList.add(leaves);
            }
        }
        return map;
    }
}
