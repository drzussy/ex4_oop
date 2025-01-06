package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {

    public static final String SUN_TAG = "sun";
    public static final float DEFAULT_TRANSITION_TIME = 30f;
    public static final float HORIZON_RATIO =2f/3;
    public static final float FULL_ROTATION_DEGREES = 360f;
    public static final float DIMENSIONS_CENTER = 0.5f;
    public static final int DEFAULT_SUN_SIZE = 50;
    public static final float MIDDLE_OF_SKY_IN_Y = 1f / 3;

    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        Vector2 initialSunCenter = new Vector2(windowDimensions.x()*DIMENSIONS_CENTER,
                windowDimensions.y()* MIDDLE_OF_SKY_IN_Y);
        GameObject sun = new GameObject(initialSunCenter, new Vector2(DEFAULT_SUN_SIZE, DEFAULT_SUN_SIZE),
                new OvalRenderable(Color.YELLOW));
        sun.setCenter(initialSunCenter);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);

        Vector2 cycleCenter = new Vector2(windowDimensions.x()* DIMENSIONS_CENTER,
                HORIZON_RATIO * windowDimensions.y());
        new Transition<>(sun,
                (Float angle)->
                        sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                0f,
                FULL_ROTATION_DEGREES,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}
