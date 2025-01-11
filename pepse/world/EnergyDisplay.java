package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.util.function.Supplier;

/**
 * A display GameObject, intended for displaying the current energy of the avatar.
 */
public class EnergyDisplay extends GameObject {
    private static final String FULL_HEALTH = "100";
    private final Supplier<Double> callback;

    /**
     * Construct a new EnergyDisplay instance.
     *
     * @param topLeftCorner Position of the display, in window coordinates (pixels).
     *                      Note: this will stay constant with respect to the camera.
     * @param dimensions    Width and height in window coordinates.
     * @param valueGetter   A callback providing the number to display, using get().
     *                      In this case - the avatar's energy.
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Supplier<Double> valueGetter) {
        super(topLeftCorner, dimensions, new TextRenderable(FULL_HEALTH));
        this.callback = valueGetter;
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * The update method which makes sure the energy display correctly reflects the avatar's energy level.
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     *                  Used only by the super.update() method, and not here.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.renderer().setRenderable(new TextRenderable((int) Math.floor(callback.get()) + "%"));
    }
}

