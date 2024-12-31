package Src.Pepse.world;

import Src.Pepse.util.ColorSupplier;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * this class does not extend gameObject buy only creates block (which are gameObjects)
 */
public class Terrain {
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final String GROUND_BLOCK_TAG = "ground block";
    private static final float DEFAULT_HEIGHT_AT_X0 = 2/3;
    private final float groundHeightAtX0;
    private final Vector2 windowDimensions;


    //    private int groundHeightAtX0;
    public Terrain(Vector2 windowDimensions, int seed) {
        this.windowDimensions = windowDimensions;
        groundHeightAtX0 = ((float) DEFAULT_HEIGHT_AT_X0) * windowDimensions.y();
    }

    //basic method to define ground height for a given x
    public float groundHeightAt(float x){return groundHeightAtX0;}

    public List<Block> createInRange(int minX, int maxX){
        ArrayList<Block> blockList = new ArrayList<>();
        for (int i = minX; i < maxX; i=i+Block.SIZE) {
            float height = groundHeightAt(i);
            for (int j = (int) height; j < windowDimensions.y(); j+=Block.SIZE) {
                Block block = new Block(new Vector2(i, j),
                        new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR)));
                block.setTag(GROUND_BLOCK_TAG);
                blockList.add(block);
            }

        }
        return blockList;
    }

}
