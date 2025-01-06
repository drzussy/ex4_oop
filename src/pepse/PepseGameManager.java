package src.pepse;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Camera;
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
import java.util.function.Supplier;

public class PepseGameManager extends GameManager{

    public static final int DEFAULT_CYCLE_LENGTH = 30;
    public static final double MIDDLE = 0.5f;
    public static final int PLACEMENT_BUFFER = 4 * Block.SIZE;
    private static final float CAMERA_HEIGHT = 0.1F;
    private static final float CLOUD_HEIGHT_FRACTION = 0.1F;
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(300, 160);
    private static final int LEAF_LAYER = 50;
    private static final int FRUIT_LAYER = 51;
    private static final int CHUNK_SIZE = 1500;
    private static final Vector2 DISPLAY_DIMENSIONS = Vector2.ONES.mult(50);
    private static final String AVATAR_TAG = "avatar";
    private GameObjectCollection gameObjects;
    private Avatar avatar;
    private Terrain terrain;
    private Flora flora;
    private Vector2 windowDimensions;
    private int minLoadedX;
    private int maxLoadedX;


    public static void main(String[] args){
        new PepseGameManager().run();
    }

    /**
     * this method initializes basic gameObjects sky, terrain avtar etc.
     * @param imageReader Allows reading image files to create a Renderable.
     * @param soundReader Allows reading sound files to create Sounds.
     * @param inputListener Contains a single method: isKeyPressed, which returns whether
     *                      a given key is currently pressed by the user or not. See its
     *                      documentation.
     * @param windowController Contains an array of helpful, self-explanatory methods
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
        float windowWidth = windowDimensions.x();
        minLoadedX = (int) (-CHUNK_SIZE);
        maxLoadedX = (int) ((windowWidth/Block.SIZE)*Block.SIZE+CHUNK_SIZE);

        createBackgroundObjects();

        //terrain initialization
        terrain = new Terrain(windowDimensions, new Random().nextInt());
        flora = new Flora(new Random().nextInt(), terrain::surfaceLevelAt, imageReader::readImage, AVATAR_TAG);
        loadWorld(minLoadedX, maxLoadedX);
        gameObjects.layers().shouldLayersCollide(Layer.DEFAULT, FRUIT_LAYER, true);

        // avatar initialization
        float middle_x = (float) (windowDimensions.x()* MIDDLE);
        Vector2 avatarInitialPosition = new Vector2(middle_x, terrain.groundHeightAt(middle_x)- PLACEMENT_BUFFER);
        avatar = new Avatar(avatarInitialPosition, inputListener, imageReader);
        gameObjects.addGameObject(avatar);
        avatar.setTag(AVATAR_TAG);

        // Create energy display
        Supplier<Double> callback = avatar::getEnergy;
        gameObjects.addGameObject(new EnergyDisplay(Vector2.ZERO ,DISPLAY_DIMENSIONS, callback), Layer.UI);

        // TODO - remove this
        Supplier<Double> locationCallback = () -> (double) avatar.getCenter().x();
        gameObjects.addGameObject(new EnergyDisplay(new Vector2(windowWidth*0.5f, 0) ,DISPLAY_DIMENSIONS, locationCallback), Layer.UI);

        Vector2 cameraPosition = new Vector2(0, -windowDimensions.y()*CAMERA_HEIGHT);
        setCamera(new Camera(avatar, cameraPosition,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    private void createBackgroundObjects() {
        //initialize sky background and set to layer
        GameObject sky = Sky.create(windowDimensions);
        gameObjects = gameObjects();
        gameObjects.addGameObject(sky, Layer.BACKGROUND);
        // night initialization
        GameObject night = Night.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects.addGameObject(night, Layer.FOREGROUND);

        // sun initialization
        GameObject sun = Sun.create(windowDimensions, DEFAULT_CYCLE_LENGTH);
        gameObjects.addGameObject(sun, Layer.BACKGROUND);

        // halo initialization
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects.addGameObject(sunHalo, Layer.BACKGROUND);

        // cloud creation
        List<GameObject> cloud = Cloud.create(new Vector2(0, windowDimensions.y()*CLOUD_HEIGHT_FRACTION),
                CLOUD_DIMENSIONS, windowDimensions);
        if (cloud!=null) {
            for (GameObject cloudBlock : cloud) {
                gameObjects.addGameObject(cloudBlock, Layer.BACKGROUND);
            }
        }
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        if (avatar.getCenter().x()-minLoadedX < CHUNK_SIZE) {
            loadWorld(minLoadedX-CHUNK_SIZE, minLoadedX);
            minLoadedX -= CHUNK_SIZE;
            maxLoadedX -= CHUNK_SIZE;
        }
        else if (maxLoadedX-avatar.getCenter().x() < CHUNK_SIZE) {
            loadWorld(maxLoadedX, maxLoadedX+CHUNK_SIZE);
            minLoadedX += CHUNK_SIZE;
            maxLoadedX += CHUNK_SIZE;
        }
        for (GameObject obj : gameObjects) {
            if (obj.getTopLeftCorner().x()<minLoadedX || obj.getTopLeftCorner().x()>maxLoadedX) {
                gameObjects.removeGameObject(obj, LEAF_LAYER); // Removes only leaves
                gameObjects.removeGameObject(obj, FRUIT_LAYER); // Removes only fruit
                gameObjects.removeGameObject(obj, Layer.STATIC_OBJECTS); // removes only trees and ground
            }
        }
    }

    private void loadWorld (int minX, int maxX) {
        List<Block> terrainList = terrain.createInRange(minX, maxX);
        for(Block block: terrainList){
            gameObjects.addGameObject(block, Layer.STATIC_OBJECTS);
        }
        Map<Tree, List<GameObject>> treeList = flora.createInRange(minX, maxX);
        for(Tree tree: treeList.keySet()){
            gameObjects.addGameObject(tree, Layer.STATIC_OBJECTS);
            List<GameObject> treeLeavesAndFruit = treeList.get(tree);
            for (GameObject obj : treeLeavesAndFruit) {
                if (obj.getTag().equals(Flora.LEAF_TAG)) gameObjects.addGameObject(obj, LEAF_LAYER);
                else if (obj.getTag().equals(Flora.FRUIT_TAG)) gameObjects.addGameObject(obj,FRUIT_LAYER);
                else gameObjects.addGameObject(obj, Layer.STATIC_OBJECTS);
            }
        }
    }
}

