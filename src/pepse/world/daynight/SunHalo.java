package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;

import java.awt.*;

public class SunHalo {
    public static final Color HALO_COLOR = new Color(255, 255, 0, 60);
    private static final Vector2 HALO_BUFFER = new Vector2(20, 20);
    public static final String SUN_HALO_TAG = "sunHalo";

    public static GameObject create(GameObject sun){
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().add(HALO_BUFFER),
                new OvalRenderable(HALO_COLOR));
//        sunHalo.setCenter(sun.getCenter());
        sunHalo.addComponent((float deltaTime)->sunHalo.setCenter(sun.getCenter()));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);
        return sunHalo;
    }
}
