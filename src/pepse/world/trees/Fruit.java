package src.pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.world.Avatar;

import java.util.function.Consumer;

public class Fruit extends GameObject {

    public static final String FRUIT_TAG = "fruit";
    private static final Vector2 FRUIT_SIZE = new Vector2(17, 27);
    private static final int FRUIT_ENERGY_BONUS = 20;
//    private final Consumer<GameObject> eatFruit;
    private static final float FRUIT_RESPAWN_DELAY = 3;
    private boolean collidable = true;
    Renderable fruitImage;

    public Fruit(Vector2 topLeftCorner, Renderable renderable) {
//                 Consumer<GameObject> eatFruit) {
        super(topLeftCorner, FRUIT_SIZE ,renderable);
        this.setTag(FRUIT_TAG);
        this.fruitImage = renderable;
    }

    /**
     * Defines that the fruit should collide with nothing except the game avatar.
     *
     * @param other The other GameObject.
     * @return true if the other object is the game avatar, false otherwise.
     */
    @Override
    public boolean shouldCollideWith(GameObject other) {
        if (!collidable || !other.getTag().equals("avatar")) return false;
        return true;
    }

    /**
     * Called on the first frame of a collision.
     *
     * @param other     The GameObject with which a collision occurred.
     * @param collision Information regarding this collision.
     *                  A reasonable elastic behavior can be achieved with:
     *                  setVelocity(getVelocity().flipped(collision.getNormal()));
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if (!other.getTag().equals("avatar")) return;
        Avatar avatar = (Avatar) other;
        avatar.changeEnergy(FRUIT_ENERGY_BONUS);
        despawnFruit();
        new ScheduledTask(
                this, FRUIT_RESPAWN_DELAY,false,
                this::respawnFruit // Action to re-add the fruit
        );
    }

    private void respawnFruit () {
        collidable = true;
        renderer().setRenderable(fruitImage);
    }
    private void despawnFruit () {
        collidable = false;
        renderer().setRenderable(null);
    }
}
