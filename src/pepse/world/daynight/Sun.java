package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Sun {

    public static final String SUN_TAG = "sun";
    private static final float HORIZON_RATIO =2f/3;
    private static final float FULL_ROTATION_DEGREES = 360f;
    private static final float DIMENSIONS_CENTER = 0.5f;
    private static final int SUN_SIZE = 50;
    public static final float HEIGHT_AT_ZENITH = 1f / 3;

    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        Vector2 initialSunCenter = new Vector2(windowDimensions.x()*DIMENSIONS_CENTER,
                windowDimensions.y()* HEIGHT_AT_ZENITH);
        GameObject sun = new GameObject(initialSunCenter, new Vector2(SUN_SIZE, SUN_SIZE),
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
