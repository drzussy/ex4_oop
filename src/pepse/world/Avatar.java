package src.pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.CoordinateSpace;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    public static final String IMAGE_PATH = "assets/assets/idle_0.png";
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
        super(topLeftCorner,  Vector2.ONES.mult(AVATAR_SIZE),
                imageReader.readImage(IMAGE_PATH, true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);
        this.inputListener = inputListener;


    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float xVel = 0;
        //check for horizontal movement
        if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && !inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
            if (changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE)) {
                xVel -= VELOCITY_X;
            }
        } else if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && !inputListener.isKeyPressed(KeyEvent.VK_LEFT))
        {
            if(changeEnergy(HORIZONTAL_MOVE_ENERGY_DECREASE))
            {
                xVel += VELOCITY_X;
            }
        } else if (getVelocity().y() == 0) {
            //check for jump
            if (inputListener.isKeyPressed(KeyEvent.VK_SPACE)) {
                if (changeEnergy(JUMP_ENERGY_DECREASE)) {
                    transform().setVelocityY(VELOCITY_Y);
                }
                // static avatar, add 1 point of energy
            }
        }
            if (getVelocity().x() == 0 && getVelocity().y() == 0){
                changeEnergy(1);

        }
        transform().setVelocityX(xVel);

//        System.out.println(getEnergy());

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
        if(energy+change<0){
            return false;
        } else if (energy+change>FULL_ENERGY) {
            energy = FULL_ENERGY;
        }
        else{
            energy = energy+change;
        }
        return true;
    }
}
