package src.pepse;

import danogl.collisions.GameObjectCollection;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.Renderable;
import src.pepse.world.*;
import src.pepse.world.daynight.Night;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.util.Vector2;
import src.pepse.world.daynight.*;
import src.pepse.world.trees.*;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class PepseGameManager extends GameManager{

    public static final int DEFAULT_CYCLE_LENGTH = 30;
    public static final double MIDDLE = 0.5f;
    public static final int PLACEMENT_BUFFER = 4 * Block.SIZE;
    private static final float CAMERA_HEIGHT = 0.1F;
    private static final float FRUIT_RESPAWN_DELAY = 5;
    private static final float CLOUD_HEIGHT_FRACTION = 0.1F;
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(300, 160);
    private GameObjectCollection gameObjects;
    private Vector2 windowDimensions;


    public static void main(String[] args){
        new PepseGameManager().run();
    }

    /**
     * this method initializes basic gameObjects sky, terrain avtar etc.
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
        //initialize sky background and set to layer
        GameObject sky = Sky.create(windowDimensions);
        gameObjects = gameObjects();
        gameObjects.addGameObject(sky, Layer.BACKGROUND);

        //terrain initialization
        Terrain terrain = new Terrain(windowDimensions, new Random().nextInt());
        List<Block> terrainList = terrain.createInRange(0, (int) windowDimensions.x());
        for(Block block: terrainList){
            gameObjects.addGameObject(block, Layer.STATIC_OBJECTS);
        }

        Consumer<GameObject> eatFruit = (GameObject f) -> {
            gameObjects.removeGameObject(f, Layer.STATIC_OBJECTS);
            new ScheduledTask(
                    f, FRUIT_RESPAWN_DELAY,false,
                    () -> gameObjects.addGameObject(f, Layer.STATIC_OBJECTS) // Action to re-add the fruit
            );
        };

        // cloud creation
        List<GameObject> cloud = Cloud.create(new Vector2(0, windowDimensions.y()*CLOUD_HEIGHT_FRACTION),
                CLOUD_DIMENSIONS, windowDimensions);
        if (cloud!=null) {
            for (GameObject cloudBlock : cloud) {
                gameObjects.addGameObject(cloudBlock, Layer.BACKGROUND);
            }
        }

        Flora flora = new Flora(new Random().nextInt(), terrain::surfaceLevelAt, imageReader::readImage);
        Map<Tree, List<GameObject>> treeList = flora.createInRange(0, (int) windowDimensions.x());
        for(Tree tree: treeList.keySet()){
            gameObjects.addGameObject(tree, Layer.STATIC_OBJECTS);
            List<GameObject> treeLeavesAndFruit = treeList.get(tree);
            for (GameObject obj : treeLeavesAndFruit) {
                if (obj.getTag().equals("leaf")) gameObjects.addGameObject(obj, Layer.BACKGROUND);
                else gameObjects.addGameObject(obj, Layer.STATIC_OBJECTS);
            }
        }

        // night initialization
        GameObject night = Night.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects.addGameObject(night, Layer.FOREGROUND);

        // sun initialization
        GameObject sun = Sun.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects.addGameObject(sun, Layer.BACKGROUND);

        // halo initialization
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects.addGameObject(sunHalo, Layer.BACKGROUND);

        // avatar initialization
        float middle_x = (float) (windowDimensions.x()* MIDDLE);
        Vector2 avatarInitialPosition = new Vector2(middle_x, terrain.groundHeightAt(middle_x)- PLACEMENT_BUFFER);
        Avatar avatar =
                new Avatar(avatarInitialPosition,
                inputListener, imageReader);
        gameObjects.addGameObject(avatar);
        avatar.setTag("avatar"); // TODO: move to someplace else

        ValueProvider callback = avatar::getEnergy;
        gameObjects.addGameObject(new EnergyDisplay(Vector2.ZERO ,windowDimensions, callback));

        Vector2 cameraPosition = new Vector2(0, -windowDimensions.y()*CAMERA_HEIGHT);
        setCamera(new Camera(avatar, cameraPosition,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }
}

