package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Consumer;

public class Raindrop extends GameObject {
    public static final String RAIN_TAG = "rain";
    private static final float GRAVITY = 100;
    public static final Vector2 DIMENSIONS = new Vector2(20, 30);
    public static final float TEARDROP_FALL_TIME = 3f;
    public static final float NO_OPACITY = 1f;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param renderable    The renderable representing the object. Can be null, in which case
     *                      the GameObject will not be rendered.
     */
    public Raindrop(Vector2 topLeftCorner, Renderable renderable){
        //}, Consumer gameObjectsRemove) {
        super(topLeftCorner, DIMENSIONS, renderable);
//        super(topLeftCorner, DIMENSIONS, new OvalRenderable(Color.BLACK));
        transform().setAccelerationY(GRAVITY);

        new Transition<>(this, renderer()::setOpaqueness, NO_OPACITY,
                0f, Transition.LINEAR_INTERPOLATOR_FLOAT, Raindrop.TEARDROP_FALL_TIME,
                Transition.TransitionType.TRANSITION_ONCE, null);
        setTag(RAIN_TAG);

    }
}
