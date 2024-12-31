package src.pepse;

import src.pepse.world.Block;
import src.pepse.world.Sky;
import src.pepse.world.Terrain;
import src.pepse.world.daynight.Night;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import src.pepse.world.daynight.Sun;
import src.pepse.world.daynight.SunHalo;

import java.util.List;
import java.util.Random;

public class PepseGameManager extends GameManager{

    public static final int DEFAULT_CYCLE_LENGTH = 30;

    private Vector2 windowDimensions;

    public static void main(String args[]){
        new PepseGameManager().run();
    }

    /**
     * this method intializes basic gameObjects sky, terrain avtar etc.
     * @param imageReader Contains a single method: readImage, which reads an image from disk.
     *                 See its documentation for help.
     * @param soundReader Contains a single method: readSound, which reads a wav file from
     *                    disk. See its documentation for help.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self explanatory methods
     *                         concerning the window.
     */
    @Override
    public void initializeGame(ImageReader imageReader,
                               SoundReader soundReader,
                               UserInputListener inputListener,
                               WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);

        windowDimensions = windowController.getWindowDimensions();
        //initialize sky backround and set to layer
        GameObject sky = Sky.create(windowDimensions);
        gameObjects().addGameObject(sky, Layer.BACKGROUND);

        //terrain initialization
        Terrain terrain = new Terrain(windowDimensions, new Random().nextInt());
        List<Block> terrainList = terrain.createInRange(0, (int) windowDimensions.x());
        for(Block block: terrainList){
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }

        //night initialization
        GameObject night = new Night().create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        //sun intialization
        GameObject sun = Sun.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        //halo initialization
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
    }

}

