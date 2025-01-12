package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static pepse.util.PepseConstants.*;

/**
 * A Raindrop class. Randomly created by the Cloud class when the game avatar jumps.
 * Falls down while becoming more transparent, deleting itself when fully transparent.
 */
public class Raindrop extends GameObject {
    private static final float RAINDROP_GRAVITY = 2*GRAVITY;
    private static final Vector2 DIMENSIONS = new Vector2(20, 30);

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the raindrop, in pixels. Always gets passed the center of the
     *                      cloud block which spawned this raindrop.
     * @param renderable    The renderable representing the raindrop.
     */
    public Raindrop(Vector2 topLeftCorner, Renderable renderable){
        super(topLeftCorner, DIMENSIONS, renderable);
        transform().setAccelerationY(RAINDROP_GRAVITY);
        setTag(RAIN_TAG);
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }
}
