package Src.Pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

public class Block  extends GameObject {
    public static final int SIZE = 30;

    /**
     * this class is a block of constant size- SIZE*SIZE which is immovable and does not allow for any other
     * GameObject to pass through it should a collision between them be wanted by either.
     * @param topLeftCorner
     * @param renderable of the block instance
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        //set to constant size of 30*30
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }
}
