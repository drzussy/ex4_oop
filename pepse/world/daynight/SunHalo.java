package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import java.awt.Color;
import static pepse.util.PepseConstants.SUN_HALO_TAG;

/**
 * A class to create halos for already-created suns. Used via its static method, create(sun).
 */
public class SunHalo {
    private static final int HALO_OPACITY = 60;
    private static final Color HALO_COLOR = new Color(255, 255, 0, 60);
    private static final Vector2 HALO_BUFFER = new Vector2(20, 20);

    /**
     * A static method to create a default halo for the sun.
     * @param sun The sun to have a halo created for.
     * @return The halo for the passed sun.
     */
    public static GameObject create(GameObject sun){
        return create(sun, HALO_COLOR);
    }
    /**
     * A static method to create a custom-colored halo for the sun.
     * @param sun The sun to have a halo created for.
     * @param color The color of the desired halo. Note: only the RGB values of the color matter,
     *              as the passed opacity will be overridden.
     * @return The halo for the passed sun.
     */
    public static GameObject create(GameObject sun, Color color){
        Color haloColor = new Color (color.getRed(), color.getGreen(), color.getBlue(), HALO_OPACITY);
        GameObject sunHalo = new GameObject(sun.getTopLeftCorner(), sun.getDimensions().add(HALO_BUFFER),
                new OvalRenderable(haloColor));
        sunHalo.addComponent((float deltaTime)->sunHalo.setCenter(sun.getCenter()));
        sunHalo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        sunHalo.setTag(SUN_HALO_TAG);
        return sunHalo;
    }
}
