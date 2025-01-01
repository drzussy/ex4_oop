package src.pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import javax.security.auth.callback.Callback;

public class HealthDisplay extends GameObject {

    public static final String FULL_HEALTH = "100";
    private final ValueProvider callback;

    /**
     * Construct a new GameObject instance.
     *
     * @param topLeftCorner Position of the object, in window coordinates (pixels).
     *                      Note that (0,0) is the top-left corner of the window.
     * @param dimensions    Width and height in window coordinates.
     */
    public HealthDisplay(Vector2 topLeftCorner, Vector2 dimensions,ValueProvider callback
                         ) {
        super(topLeftCorner, Vector2.ONES.mult(100), new TextRenderable(FULL_HEALTH));
        this.callback = callback;

    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        this.renderer().setRenderable(new TextRenderable(Double.toString(Math.floor(callback.getValue()))));

    }
}

