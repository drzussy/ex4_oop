package src.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.*;

import static src.pepse.util.PepseConstants.*;

/**
 * The class representing the game avatar, controlled by the player.
 */
public class Avatar extends GameObject {

    private static final String[] IDLE_IMAGES_PATHS = {
            "assets/idle_0.png", "assets/idle_1.png", "assets/idle_2.png", "assets/idle_3.png"
    };
    private static final String[] JUMPING_IMAGES_PATHS = {
            "assets/jump_0.png", "assets/jump_1.png", "assets/jump_2.png", "assets/jump_3.png"
    };
    private static final String[] RUNNING_IMAGES_PATHS = {
            "assets/run_0.png", "assets/run_1.png", "assets/run_2.png",
            "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"
    };
    private static final double IDLE_ANIMATION_FRAME_TIME = 0.3;
    private static final double JUMP_ANIMATION_FRAME_TIME = 0.5;
    private static final double RUN_ANIMATION_FRAME_TIME = 0.1;
    private final AnimationRenderable idleAnimation;
    private final AnimationRenderable jumpingAnimation;
    private final AnimationRenderable runningAnimation;
    private static final float WALKING_SPEED = BLOCK_SIZE*15;
    private static final float JUMP_SPEED = BLOCK_SIZE*24;
    private static final Vector2 AVATAR_SIZE = new Vector2 (BLOCK_SIZE*4/3F, BLOCK_SIZE*2);
    private static final double HORIZONTAL_MOVE_ENERGY_DECREASE = -0.5;
    private static final int JUMP_ENERGY_DECREASE = -10;
    private static final int FULL_ENERGY = 100;
    private final UserInputListener inputListener;
    private final List<JumpObserver> jumpObservers = new ArrayList<>();
    private double energy = 100;

    /**
     * The constructor for a new Avatar GameObject.
     * @param topLeftCorner The top-left corner of the created avatar.
     * @param inputListener The InputListener used for reading keyboard input.
     * @param imageReader   The ImageReader used to create the avatar animations.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader){
        super(topLeftCorner,  AVATAR_SIZE, null);
        idleAnimation = new AnimationRenderable(IDLE_IMAGES_PATHS, imageReader,
                true, IDLE_ANIMATION_FRAME_TIME);
        jumpingAnimation = new AnimationRenderable(JUMPING_IMAGES_PATHS, imageReader,
                true, JUMP_ANIMATION_FRAME_TIME);
        runningAnimation = new AnimationRenderable(RUNNING_IMAGES_PATHS, imageReader,
                true, RUN_ANIMATION_FRAME_TIME);
        renderer().setRenderable(idleAnimation);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;
    }

    /**
     * The update method, allowing control of the avatar movement via the keyboard.
     * @param deltaTime The time elapsed, in seconds, since the last frame.
     *                  Used only by super.update(), not by this override.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //check for horizontal movement
        if (getVelocity().y() == 0 && inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            // try to expend enough energy to jump
            if (changeEnergy(JUMP_ENERGY_DECREASE)) {
                transform().setVelocityY(-JUMP_SPEED);
                renderer().setRenderable(jumpingAnimation);
                for (JumpObserver obs : jumpObservers) {
                        obs.notifyAboutJump();
                }
            }
        } else if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && !inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (getVelocity().y()!=0 || changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE)) {
                xVel -= WALKING_SPEED;
                renderer().setRenderable(runningAnimation);
                renderer().setIsFlippedHorizontally(true);
            }
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && !inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
            if (getVelocity().y()!=0 || changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE)) {
                xVel += WALKING_SPEED;
                renderer().setRenderable(runningAnimation);
                renderer().setIsFlippedHorizontally(false);
            }
        }
        if (getVelocity().x() == 0 && getVelocity().y() == 0){
            // static avatar, add 1 point of energy
            changeEnergy(1);
            renderer().setRenderable(idleAnimation);
        }
        transform().setVelocityX(xVel);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(BLOCK_TAG)){
            this.transform().setVelocityY(0);
            this.transform().setVelocityX(0);
        }
    }

    /**
     * A getter for the avatar's current energy level.
     * Used only by the energy display, which is passed a callback to this from the PepseGameManager.
     * @return The avatar's current energy level.
     */
    public double getEnergy(){
        return energy;
    }

    /**
     * Allows requesting changes to the avatar's current energy level, if possible.
     * Returns true iff change was successful.
     * @param change The amount to increase the avatar's energy by. Pass negative numbers to decrease.
     * @return  True in case of positive changes or for successful negative changes.
     *          False if there wasn't enough energy for a negative change.
     */
    public boolean changeEnergy(double change){
        if(energy+change<0) return false;
        energy = Math.min(energy+change, FULL_ENERGY);
        return true;
    }

    /**
     * Allows adding a JumpObserver to the list of objects to be notified when the avatar jumps,
     * as part of the Observer design pattern.
     * Used by the cloud, to know when to create raindrops.
     * @param obs The JumpObserver to be removed.
     */
    public void addJumpObserver (JumpObserver obs) {
        this.jumpObservers.add(obs);
     }

    /**
     * Allows removing a JumpObserver from the list of objects to be notified when the avatar jumps,
     * as part of the Observer design pattern.
     * @param obs The JumpObserver to be removed.
     */
     public void removeJumpObserver (JumpObserver obs) {
        this.jumpObservers.remove(obs);
    }
}
