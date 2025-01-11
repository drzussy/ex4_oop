package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;
import static pepse.PepseConstants.NIGHT_TAG;

/**
 * A class for creating nighttime darkness cycles. Used only via the static method Night.create().
 */
public class Night {
    private static final float MIDNIGHT_OPAQUENESS = 0.5f;
    /*  The following factor is needed because of the behaviour of TRANSITION_BACK_AND_FORTH,
        which uses the full cycle length for each of BACK and FORTH.    */
    private static final float NIGHTTIME_CYCLE_LENGTH_FACTOR = 0.5f;

    /**
     * A static method for creating a nighttime GameObject, which will transition between being semi-opaque
     * and full transparency with the day/night cycle.
     * @param windowDimensions The dimensions of the game window, which the darkness needs to match.
     * @param cycleLength The length of a day/night cycle.
     * @return The nighttime darkness GameObject, which already handles day/night cycle transitions.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        float nighttimeCycleLength = cycleLength*NIGHTTIME_CYCLE_LENGTH_FACTOR; // see above
        GameObject night = new GameObject(Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        new Transition<>(night, night.renderer()::setOpaqueness,
                0f, MIDNIGHT_OPAQUENESS,
                Transition.CUBIC_INTERPOLATOR_FLOAT, nighttimeCycleLength,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }
}
