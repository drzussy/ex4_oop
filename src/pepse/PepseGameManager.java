package src.pepse;

import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.TextRenderable;
import src.pepse.world.*;
import src.pepse.world.daynight.Night;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.Transition;
import danogl.gui.*;
import danogl.util.Vector2;
import src.pepse.world.daynight.*;
import src.pepse.world.trees.*;
import java.util.*;
import java.util.function.Function;

public class PepseGameManager extends GameManager{

    public static final int DEFAULT_CYCLE_LENGTH = 30;
    public static final double MIDDLE = 0.5f;
    public static final int PLACEMENT_BUFFER = 4 * Block.SIZE;

    private Vector2 windowDimensions;


    public static void main(String[] args){
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
        windowController.setTargetFramerate(60);
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

        Flora flora = new Flora(windowDimensions, new Random().nextInt(), terrain::surfaceLevelAt);
        Map<Tree, List<Block>> treeList = flora.createInRange(0, (int) windowDimensions.x());
        for(Tree tree: treeList.keySet()){
            gameObjects().addGameObject(tree, Layer.STATIC_OBJECTS);
            List<Block> treeLeaves = treeList.get(tree);
            for (Block leaf : treeLeaves) {
                gameObjects().addGameObject(leaf, Layer.BACKGROUND);
            }
        }

        //night initialization
        GameObject night = Night.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects().addGameObject(night, Layer.FOREGROUND);

        //sun intialization
        GameObject sun = Sun.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);

        //halo initialization
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);

        //avatar initialization
        float middle_x = (float) (windowDimensions.x()* MIDDLE);
        Avatar avatar =
                new Avatar(new Vector2(middle_x, terrain.groundHeightAt(middle_x)- PLACEMENT_BUFFER),
                inputListener, imageReader);
        gameObjects().addGameObject(avatar);

        ValueProvider callback = avatar::getEnergy;
        gameObjects().addGameObject(new HealthDisplay(Vector2.ZERO ,windowDimensions, callback));
    }



}

