package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class Night {

    public static final String NIGHT_TAG = "night";
    public static final float MIDNIGHT_OPAQUENESS = 0.5f;
    public static final float HALF_DAY_TRANSITION_TIME = 15f;

    public static GameObject create(Vector2 windowDimensions, float cycleLength){
        GameObject night = new GameObject(Vector2.ZERO,
                windowDimensions,
                new RectangleRenderable(Color.BLACK));
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        new Transition<>(night, night.renderer()::setOpaqueness, 0f,
                MIDNIGHT_OPAQUENESS, Transition.CUBIC_INTERPOLATOR_FLOAT, HALF_DAY_TRANSITION_TIME,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH, null);
        return night;
    }
}
