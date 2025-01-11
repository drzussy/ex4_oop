package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.BiConsumer;

import static src.pepse.PepseConstants.*;

/**
 * A Raindrop class. Randomly created by the Cloud class when the game avatar jumps.
 * Falls down while becoming more transparent, deleting itself when fully transparent.
 */
public class Raindrop extends GameObject {

    private static final float RAINDROP_GRAVITY = 2*GRAVITY;
    private static final Vector2 DIMENSIONS = new Vector2(20, 30);
    private static final float TEARDROP_FALL_TIME = 2f;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner     Position of the raindrop, in pixels. Always gets passed the center of the
     *                          cloud block which spawned this raindrop.
     * @param renderable        The renderable representing the raindrop.
     * @param gameObjectsRemove A callback to the method which removes a GameObject once opacity is 0,
     *                          by using the optional afterFadeOut parameter of RendererComponent.fadeOut().
     */
    public Raindrop(Vector2 topLeftCorner, Renderable renderable,
                    BiConsumer<GameObject, Integer> gameObjectsRemove){
        super(topLeftCorner, DIMENSIONS, renderable);
        transform().setAccelerationY(RAINDROP_GRAVITY);
        setTag(RAIN_TAG);
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        renderer().fadeOut(TEARDROP_FALL_TIME, ()-> gameObjectsRemove.accept(this, LEAF_AND_RAIN_LAYER));
    }
}
