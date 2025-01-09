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

import java.awt.event.KeyEvent;
import java.util.*;
import java.util.function.Supplier;

import static src.pepse.util.PepseConstants.*;

public class PepseGameManager extends GameManager{


    public static final double MIDDLE = 0.5f;
    public static final int PLACEMENT_BUFFER = 4 * BLOCK_SIZE;
    private static final float CAMERA_HEIGHT = 0.1F;
    private static final float CLOUD_HEIGHT_FRACTION = 0.1F;
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(300, 160);
    private static final int LEAF_LAYER = 50;
    private static final int FRUIT_LAYER = 51;
    private static final int CHUNK_SIZE = 1500;
    private static final Vector2 DISPLAY_DIMENSIONS = Vector2.ONES.mult(50);
    private UserInputListener inputListener;
    private WindowController windowController;
    private Avatar avatar;
    private Terrain terrain;
    private Flora flora;
    private Vector2 windowDimensions;
    private int minLoadedX;
    private int maxLoadedX;
    private GameObjectCollection gameObjects;


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
        this.gameObjects = gameObjects();
        this.windowController = windowController;
        this.inputListener = inputListener;
        windowController.setTargetFramerate(60);
        windowDimensions = windowController.getWindowDimensions();
        float windowWidth = windowDimensions.x();
        minLoadedX = (int) (-CHUNK_SIZE);
        maxLoadedX = (int) ((windowWidth/ BLOCK_SIZE)* BLOCK_SIZE +CHUNK_SIZE);

        // Collision optimization
        optimizeLayerCollisions();

        createBackgroundObjects();

        //terrain initialization
        terrain = new Terrain(windowDimensions, new Random().nextInt());
        flora = new Flora(new Random().nextInt(), terrain::groundHeightAt, imageReader::readImage, AVATAR_TAG);
        loadWorld(minLoadedX, maxLoadedX);

        // avatar initialization
        float middle_x = (float) (windowDimensions.x()* MIDDLE);
        Vector2 avatarInitialPosition = new Vector2(middle_x, terrain.groundHeightAt(middle_x)- PLACEMENT_BUFFER);
        avatar = new Avatar(avatarInitialPosition, inputListener, imageReader);
        gameObjects.addGameObject(avatar);
        avatar.setTag(AVATAR_TAG);

        // cloud creation
        Cloud cloud = new Cloud(new Vector2(0, windowDimensions.y()*CLOUD_HEIGHT_FRACTION),
                CLOUD_DIMENSIONS,
                windowDimensions,
                gameObjects::addGameObject, gameObjects::removeGameObject, imageReader::readImage);
        List<GameObject> cloudList = cloud.create();
        if (cloud!=null) {
            for (GameObject cloudBlock : cloudList) {
                gameObjects.addGameObject(cloudBlock, Layer.BACKGROUND);
            }
        }
        avatar.addJumpObserver(cloud);

        // Create energy display
        Supplier<Double> callback = avatar::getEnergy;
        gameObjects.addGameObject(new EnergyDisplay(Vector2.ZERO ,DISPLAY_DIMENSIONS, callback), Layer.UI);

        // TODO - remove this once we're sure the randomly generated trees are consistent
        Supplier<Double> locationCallback = () -> (double) avatar.getCenter().x();
        gameObjects.addGameObject(new EnergyDisplay(new Vector2(windowWidth-(5*DISPLAY_DIMENSIONS.x()), 0),
                DISPLAY_DIMENSIONS, locationCallback), Layer.UI);

        Vector2 cameraPosition = new Vector2(0, -windowDimensions.y()*CAMERA_HEIGHT);
        setCamera(new Camera(avatar, cameraPosition,
                windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    private void optimizeLayerCollisions() {
        gameObjects.layers().shouldLayersCollide(Layer.DEFAULT, LEAF_LAYER, false);
        gameObjects.layers().shouldLayersCollide(Layer.DEFAULT, FRUIT_LAYER, true);
        gameObjects.layers().shouldLayersCollide(Layer.STATIC_OBJECTS, LEAF_LAYER, false);
        gameObjects.layers().shouldLayersCollide(Layer.STATIC_OBJECTS, FRUIT_LAYER, false);
        gameObjects.layers().shouldLayersCollide(LEAF_LAYER, LEAF_LAYER, false);
        gameObjects.layers().shouldLayersCollide(LEAF_LAYER, FRUIT_LAYER, false);
    }

    private void createBackgroundObjects() {
        //initialize sky background and set to layer
        GameObject sky = Sky.create(windowDimensions);
        gameObjects.addGameObject(sky, Layer.BACKGROUND);
        // night initialization
        GameObject night = Night.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects.addGameObject(night, Layer.FOREGROUND);

        // sun initialization
        GameObject sun = Sun.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects.addGameObject(sun, Layer.BACKGROUND);

        // halo initialization
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects.addGameObject(sunHalo, Layer.BACKGROUND);


    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        // TODO: remove the following line
        if (inputListener.isKeyPressed(KeyEvent.VK_R)) windowController.resetGame();
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
//            if(obj.getTag().equals(Raindrop.RAIN_TAG)){
//                if(obj.renderer().getOpaqueness() ==0) {
//                    gameObjects.removeGameObject(obj);
//                }
//            }
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
                if (obj.getTag().equals(LEAF_TAG)) gameObjects.addGameObject(obj, LEAF_LAYER);
                else if (obj.getTag().equals(FRUIT_TAG)) gameObjects.addGameObject(obj,FRUIT_LAYER);
                else gameObjects.addGameObject(obj, Layer.STATIC_OBJECTS);
            }
        }
    }
}

