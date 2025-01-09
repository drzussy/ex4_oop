package src.pepse.util;

public class PepseConstants {
    /**
     * Space constant: The size of a single block. Defines the size of everything:
     * The avatar, trees (width and height), leaves and fruits, the cloud and its parts.
     */
    public static final int BLOCK_SIZE = 30;
    /**
     * Constant: Tag for the "night" GameObject.
     */
    public static final String NIGHT_TAG = "night";
    /**
     * Constant: Tag for the sun GameObject.
     */
    public static final String SUN_TAG = "sun";
    /**
     * Constant: Tag for the sun's halo GameObject.
     */
    public static final String SUN_HALO_TAG = "sunHalo";
    /**
     * Constant: Tag for all Fruit GameObjects.
     */
    public static final String FRUIT_TAG = "fruit";
    /**
     * Constant: Tag for all tree leaf GameObjects.
     */
    public static final String LEAF_TAG = "leaf";
    /**
     * Constant: Tag for all tree GameObjects.
     */
    public static final String TREE_TAG = "tree";
    /**
     * Constant: Tag for all raindrop GameObject.
     */
    public static final String RAIN_TAG = "rain";
    /**
     * Constant: Tag for all block GameObjects,
     * if they don't have another tag assigned to them (such as leaves).
     */
    public static final String BLOCK_TAG = "block";
    /**
     * Constant: Tag for the sky background GameObject.
     */
    public static final String SKY_TAG = "sky";
    /**
     * Constant: Tag for the playable avatar GameObject.
     */
    public static final String AVATAR_TAG = "avatar";
    /**
     * Gravity constant: defines how quickly objects fall. Currently affects only the avatar and raindrops.
     */
    public static final float GRAVITY = 600;
    /**
     * Time constant: The length of a full single day/night cycle.
     * Used by the Night, Sun, and SunHalo Gameobjects.
     */
    public static final int DAY_CYCLE_LENGTH = 30;
    /**
     * Layer constant: The layer ID for the leaves and raindrops.
     * Placed in front of everything except fruit and the night darkness.
     * Does not collide with any other layer.
     */
    public static final int LEAF_AND_RAIN_LAYER = 50;
    /**
     * Layer constant: The layer ID for the fruit layer.
     * Placed in front of everything except nighttime darkness.
     * Does not collide with any layer except the default.
     */
    public static final int FRUIT_LAYER = 51;
}
