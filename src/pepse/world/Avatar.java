package src.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.*;

import static src.pepse.world.Block.BLOCK_TAG;

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
    private final List<JumpObserver> jumpObservers = new ArrayList<>();
    private static final float WALKING_SPEED = 300;
    private static final float JUMP_SPEED = 700;
    private static final float GRAVITY = 600;
    private static final Vector2 AVATAR_SIZE = new Vector2 (40, 62); // first is width, second is height
//    public static final String BLOCK_TAG = "block";
    public static final double HORIZONTAL_MOVE_ENERGY_DECREASE = -0.5;
    public static final int JUMP_ENERGY_DECREASE = -10;
    public static final int FULL_ENERGY = 100;
    private final UserInputListener inputListener;
    private double energy = 100;

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

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //check for horizontal movement
        if (getVelocity().y() == 0 && inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
            // try to expend enough energy to jump
            // TODO: Maybe add check you're not jumping at the peak of the current jump,
            // TODO where your vertical speed is 0 for a brief moment
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
        if (getVelocity().x() == 0 && getVelocity().y() == 0 || !inputListener.isKeyPressed(KeyEvent.VK_LEFT)
                &&  !inputListener.isKeyPressed(KeyEvent.VK_RIGHT)){
            // static avatar, add 1 point of energy
            changeEnergy(1);
            renderer().setRenderable(idleAnimation);
        }
        transform().setVelocityX(xVel);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
//// TODO: correctly adjust the tag constants to not have multiple instances (one here, one in the original object)
        // This code is unnecessary!
        if(other.getTag().equals(BLOCK_TAG)){
            this.transform().setVelocityY(0);
            this.transform().setVelocityX(0);
        }
    }

    public double getEnergy(){
        return energy;
    }

    public boolean changeEnergy(double change){
        if(energy+change<0) return false;
        energy = Math.min(energy+change, FULL_ENERGY);
        return true;
    }

     public void addJumpObserver (JumpObserver obs) {
        this.jumpObservers.add(obs);
     }
//    public void removeJumpObserver (JumpObserver obs) {
//        this.jumpObservers.remove(obs);
//    }
}
