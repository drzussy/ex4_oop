package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import static pepse.util.PepseConstants.BLOCK_TAG;
import static pepse.util.PepseConstants.BLOCK_SIZE;

/**
 * A basic, square GameObject block. Used in the terrain, in leaves, and in cloud parts.
 */
public class Block  extends GameObject {
    /**
     * this class is a block of constant size (SIZE*SIZE) which is immovable and does not allow for any other
     * GameObject to pass through it, should a collision between them be wanted by either.
     * @param topLeftCorner The top-left corner of the block to be created.
     * @param renderable of the block instance
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        //set to constant size of 30*30
        super(topLeftCorner, Vector2.ONES.mult(BLOCK_SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        setTag(BLOCK_TAG);
    }
}
