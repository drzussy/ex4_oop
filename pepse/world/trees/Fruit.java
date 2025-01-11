package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

import static pepse.PepseConstants.*;

/**
 * The Fruit class represents a fruit object in the game that can be interacted with by the Avatar.
 * It is designed to be collected by the Avatar, providing an energy bonus, and then respawns after a delay.
 * The fruit can also be collidable with certain objects, such as the Avatar and trees.
 */
public class Fruit extends GameObject {
    private static final Vector2 FRUIT_SIZE = new Vector2(BLOCK_SIZE*0.75f, BLOCK_SIZE*1.25f);
    private static final int FRUIT_ENERGY_BONUS = 20;
    private static final float FRUIT_RESPAWN_DELAY = DAY_CYCLE_LENGTH;
    private boolean collidable = true;
    Renderable fruitImage;

    /**
     * Constructs a new Fruit object at the specified position with the provided renderable image.
     *
     * @param topLeftCorner The top-left corner position where the fruit will be placed.
     * @param renderable    The renderable image representing the fruit's visual appearance.
     */
    public Fruit(Vector2 topLeftCorner, Renderable renderable) {
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
    return collidable && (other.getTag().equals(AVATAR_TAG) || other.getTag().equals(TREE_TAG));
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
        if (!other.getTag().equals(AVATAR_TAG)) {
            if (other.getTag().equals(TREE_TAG)) despawnFruit();
            return;
        }
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
