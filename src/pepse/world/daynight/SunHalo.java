package src.pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.Color;
import static src.pepse.util.PepseConstants.SUN_HALO_TAG;

/**
 * A class to create halos for already-created suns. Used via its static method, create(sun).
 */
public class SunHalo {
    private static final Color HALO_COLOR = new Color(255, 255, 0, 60);
    private static final Vector2 HALO_BUFFER = new Vector2(20, 20);

    /**
     * A static method to create a halo for the sun.
     * @param sun The sun to have a halo created for.
     * @return The halo for the passed sun.
     */
    public static GameObject create(GameObject sun){
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().add(HALO_BUFFER),
                new OvalRenderable(HALO_COLOR));
        sunHalo.addComponent((float deltaTime)->sunHalo.setCenter(sun.getCenter()));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);
        return sunHalo;
    }
}
