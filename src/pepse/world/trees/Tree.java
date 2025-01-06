package src.pepse.world.trees;

import danogl.GameObject;
import danogl.components.Component;
import danogl.components.GameObjectPhysics;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import src.pepse.world.JumpObserver;

import java.awt.*;

public class Tree extends GameObject{
    private static final String TREE_TAG = "tree";

    /**
     * Construct a new Tree. Receives all of its parameters from Flora.
     *
     * @param topLeftCorner Position of the tree, in window coordinates (pixels).
     * @param dimensions    Width and height in window coordinates. Width is constant (one block wide).
     * @param renderable    The brown vertical rectangle renderable representing the tree.
     */
    public Tree(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable) {
        super(topLeftCorner, dimensions, renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        setTag(TREE_TAG);
    }
}
