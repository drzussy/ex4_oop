package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.util.function.Supplier;

public class EnergyDisplay extends GameObject {

    public static final String FULL_HEALTH = "100";
    private final Supplier<Double> callback;

    /**
     * Construct a new HealthDisplay instance.
     *
     * @param topLeftCorner Position of the display, in window coordinates (pixels).
     *                      Note: this will stay constant with respect to the camera.
     * @param dimensions    Width and height in window coordinates.
     */
    public EnergyDisplay(Vector2 topLeftCorner, Vector2 dimensions, Supplier<Double> callback
                         ) {
        super(topLeftCorner, Vector2.ONES.mult(100), new TextRenderable(FULL_HEALTH));
        this.callback = callback;
        setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.renderer().setRenderable(new TextRenderable(
                Integer.toString((int) Math.floor(callback.get()))));

    }
}

