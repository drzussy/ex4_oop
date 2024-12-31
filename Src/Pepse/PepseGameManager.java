package Src.Pepse;

import Src.Pepse.world.Block;
import Src.Pepse.world.Sky;
import Src.Pepse.world.Terrain;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;

import java.util.List;
import java.util.Random;

public class PepseGameManager extends GameManager{

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
        Random random = new Random();
        Terrain terrain = new Terrain(windowDimensions, random.nextInt());
        List<Block> terrainList = terrain.createInRange(0,(int) windowDimensions.x());
        for(Block block: terrainList){
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

}

