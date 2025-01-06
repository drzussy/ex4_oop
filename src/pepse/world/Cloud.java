package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Component;
import danogl.components.SwitchComponent;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import src.pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BooleanSupplier;

public class Cloud {
    private static final Color CLOUD_COLOR = new Color(149, 166, 166, 128);
    private static final float CLOUD_SPEED = 300;
    private static final float CLOUD_BLOCK_CHANCE = 0.8f;
    private static final int CLOUD_COLOR_DELTA = 20;
    private static final float CLOUD_CYCLE_TIME = 5;
    private static final float CLOUD_BUFFER = 0.1F;

    /**
     * Construct a new cloud.
     *
     * @param topLeftCorner Position of the cloud in pixels. Note: Cloud will move with the camera.
     * @param dimensions    Width and height in window coordinates.
     * @param windowDimensions The game window dimensions,
     *                         used for resetting the cloud when it goes out-of-bounds.
     */
    public static List<GameObject> create (Vector2 topLeftCorner, Vector2 dimensions, Vector2 windowDimensions) {
        int cloudWidth = (int) (dimensions.x()/Block.SIZE);
        int cloudHeight = (int) (dimensions.y()/Block.SIZE);
        List<GameObject> cloudList = new ArrayList<>();
        List<List<Boolean>> cloudShape = generateCloud(cloudWidth, cloudHeight);
        if (cloudShape==null) return null;
        for (int row = 0; row<cloudHeight; row++) {
            List<Boolean> cloudRow = cloudShape.get(row);
            for (int col = 0; col<cloudWidth; col++) {
                if (cloudRow.get(col)) {
                    float rightShift = col*Block.SIZE;
                    Vector2 cloudTopLeft = topLeftCorner.add(new Vector2(col, row).mult(Block.SIZE));
                    Block cloud = new Block(cloudTopLeft, new RectangleRenderable(
                            ColorSupplier.approximateColor(CLOUD_COLOR, CLOUD_COLOR_DELTA)));
                    cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    new Transition<>(cloud, (Float x)->cloud.transform().setTopLeftCornerX(x),
                            windowDimensions.x()*(-2*CLOUD_BUFFER)+rightShift,
                            windowDimensions.x()*(1+CLOUD_BUFFER)+rightShift,
                            Transition.LINEAR_INTERPOLATOR_FLOAT, CLOUD_CYCLE_TIME,
                            Transition.TransitionType.TRANSITION_LOOP,
                            null);
                    cloudList.add(cloud);
                }
            }
        }
        return cloudList;
    }
    private static List<List<Boolean>> generateCloud (int width, int height) {
        if (width<=0 || height<=0) return null;
        Random random = new Random();
        List<List<Boolean>> cloudSpotsToFill = new ArrayList<>();
        for (int row=0; row<height; row++) {
            List<Boolean> cloudRow = new ArrayList<>();
            for (int col=0; col<width; col++) {
                cloudRow.add(random.nextFloat()<CLOUD_BLOCK_CHANCE);
            }
            cloudSpotsToFill.add(cloudRow);
        }
        return cloudSpotsToFill;
    }
}
