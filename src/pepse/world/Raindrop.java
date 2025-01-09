package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.function.Consumer;

import static src.pepse.util.PepseConstants.RAIN_TAG;

public class Raindrop extends GameObject {

    private static final float GRAVITY = 1000;
    public static final Vector2 DIMENSIONS = new Vector2(20, 30);
    public static final float TEARDROP_FALL_TIME = 1f;
    public static final float NO_OPACITY = 1f;
    private final Consumer<GameObject> gameObjectRemove;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner     Position of the object, in window coordinates (pixels).
     *                          Note that (0,0) is the top-left corner of the window.
     * @param renderable        The renderable representing the object. Can be null, in which case
     *                          the GameObject will not be rendered.
     * @param gameObjectsRemove callback to function thatll remove object once opacity is 0
     */
    public Raindrop(Vector2 topLeftCorner, Renderable renderable, Consumer<GameObject> gameObjectsRemove){
        super(topLeftCorner, DIMENSIONS, renderable);
        transform().setAccelerationY(GRAVITY);
        this.gameObjectRemove = gameObjectsRemove;
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        new Transition<>(this,
                renderer()::setOpaqueness,
                NO_OPACITY,
                0f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                Raindrop.TEARDROP_FALL_TIME,
                Transition.TransitionType.TRANSITION_ONCE,
//                null
                ()->gameObjectsRemove.accept(this)
                );
        setTag(RAIN_TAG);

    }

//    @Override
//    public void update(float deltaTime) {
//        super.update(deltaTime);
//        if(renderer().getOpaqueness()==0){
//            gameObjectRemove.accept(this);
//        }
//    }
}
