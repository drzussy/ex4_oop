package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.*;
import static src.pepse.PepseConstants.SUN_TAG;

/**
 * A class for creating a Sun object.
 */
public class Sun {
    private static final float HORIZON_RATIO =2f/3;
    private static final float FULL_ROTATION_DEGREES = 360f;
    private static final float DIMENSIONS_CENTER = 0.5f;
    private static final int SUN_SIZE = 50;
    private static final float HEIGHT_AT_ZENITH = 1f / 6;

    /** A static method for creating a default sun GameObject, which will orbit the center of the screen
     * counterclockwise (and moving with the character). The default sun is yellow and starts at zenith.
     * @param windowDimensions The dimensions of the game window, around which the sun revolves.
     * @param cycleLength The length of a day/night cycle.
     * @return The sun GameObject, which already handles day/night rotation.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        // TODO: delete the following code
//        Vector2 initialSunCenter = new Vector2(windowDimensions.x()*DIMENSIONS_CENTER,
//                windowDimensions.y()* HEIGHT_AT_ZENITH);
//        GameObject sun = new GameObject(initialSunCenter, new Vector2(SUN_SIZE, SUN_SIZE),
//                new OvalRenderable(Color.YELLOW));
//        sun.setCenter(initialSunCenter);
//        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
//        sun.setTag(SUN_TAG);
//
//        Vector2 cycleCenter = new Vector2(windowDimensions.x()* DIMENSIONS_CENTER,
//                HORIZON_RATIO * windowDimensions.y());
//        new Transition<>(sun,
//                (Float angle)->
//                    sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
//                0f,
//                FULL_ROTATION_DEGREES,
//                Transition.LINEAR_INTERPOLATOR_FLOAT,
//                cycleLength,
//                Transition.TransitionType.TRANSITION_LOOP,
//                null);
        return create(windowDimensions, cycleLength, Color.YELLOW, 0);
    }

    /** A static method for creating a more detailed sun GameObject, which will orbit the center of the screen
     * (moving with the character). Allows specifying the desired color and initial angle (counterclockwise).
     * @param windowDimensions The dimensions of the game window, around which the sun revolves.
     * @param cycleLength The length of a day/night cycle.
     * @param color The color of the sun to be created.
     * @param initialAngle The initial
     * @return The sun GameObject, which already handles day/night rotation.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength,
                                    Color color, float initialAngle){
        Vector2 initialSunCenter = new Vector2(windowDimensions.x()*DIMENSIONS_CENTER,
                windowDimensions.y()* HEIGHT_AT_ZENITH);
        GameObject sun = new GameObject(initialSunCenter, new Vector2(SUN_SIZE, SUN_SIZE),
                new OvalRenderable(color));
        sun.setCenter(initialSunCenter);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sun.setTag(SUN_TAG);
        Vector2 cycleCenter = new Vector2(windowDimensions.x()* DIMENSIONS_CENTER,
                HORIZON_RATIO * windowDimensions.y());
        new Transition<>(sun,
                (Float angle)->
                        sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
                initialAngle,
                initialAngle+FULL_ROTATION_DEGREES,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null);
        return sun;
    }
}
