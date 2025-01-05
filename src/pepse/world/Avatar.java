package src.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

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
    // TODO: Implement jump observers. Something like: private final List<JumpObserver> jumpObservers = new List<>();
    private static final float VELOCITY_X = 200;
    private static final float VELOCITY_Y = -450;
    private static final float GRAVITY = 700;
    public static final int AVATAR_SIZE = 40;
    public static final String BLOCK_TAG = "block";
    public static final double HORIZONTAL_MOVE_ENERGY_DECREASE = -0.5;
    public static final int JUMP_ENERGY_DECREASE = -10;
    public static final int FULL_ENERGY = 100;
    //    private final Vector2 dimensions = new Vector2(30, 30);
    private final UserInputListener inputListener;
    private double energy = 100;

    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader){
        super(topLeftCorner,  Vector2.ONES.mult(AVATAR_SIZE), null);
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
        if (getVelocity().y() == 0) { // TODO: Maybe add check you're not jumping at the peak of the current jump,
            // TODO where your vertical speed is 0 for a brief moment
            //check for jump
            if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (changeEnergy(JUMP_ENERGY_DECREASE)) {
                    transform().setVelocityY(VELOCITY_Y);
                    renderer().setRenderable(jumpingAnimation);
                    // TODO: once observers were implemented, add the following
                    /*  for (JumpObserver obs : jumpObservers) {
                            obs.notifyAboutJump();
                        }
                     */
                }
                // static avatar, add 1 point of energy
            }
        } else if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && !inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE)) {
                xVel -= VELOCITY_X;
                renderer().setRenderable(runningAnimation);
                renderer().setIsFlippedHorizontally(true);
            }
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && !inputListener.isKeyPressed(KeyEvent.VK_LEFT))
        {
            if(changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE))
            {
                xVel += VELOCITY_X;
                renderer().setRenderable(runningAnimation);
                renderer().setIsFlippedHorizontally(false);
            }
        }
        if (getVelocity().x() == 0 && getVelocity().y() == 0){
            changeEnergy(1);
            renderer().setRenderable(idleAnimation);
        }
        transform().setVelocityX(xVel);
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals(BLOCK_TAG)){
            //
            this.transform().setVelocityY(0);
            this.transform().setVelocityX(0);
        }
    }

    public double getEnergy(){
        return energy;
    }

    public boolean changeEnergy(double change){
        if(energy+change<0) return false;
        if (getVelocity().y()==0) energy = Math.min(energy+change, FULL_ENERGY);
        return true;
    }
}
