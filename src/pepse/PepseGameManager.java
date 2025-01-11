package src.pepse;

import danogl.collisions.GameObjectCollection;
import danogl.gui.rendering.Camera;
import src.pepse.world.*;
import src.pepse.world.daynight.*;
import src.pepse.world.trees.*;
import static src.pepse.PepseConstants.*;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.*;
import danogl.util.Vector2;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

/**
 * The game manager for our PEPSE. Initializes the game and makes sure to load and unload additional world
 * chunks when and where needed.
 */
public class PepseGameManager extends GameManager{
    private static final double MIDDLE = 0.5f;
    private static final int PLACEMENT_BUFFER = 4 * BLOCK_SIZE;
    private static final float CAMERA_HEIGHT = 0.1F;
    private static final float CLOUD_HEIGHT_FRACTION = 0.1F;
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(300, 160);
    private static final int CHUNK_SIZE = BLOCK_SIZE*25;
    private static final Vector2 DISPLAY_DIMENSIONS = Vector2.ONES.mult(50);
    private static final String PATH_TO_MOON_IMAGE = "assets/moon.png";
    private static final int SECOND_SUN_ANGLE = 30;
    private static final int MOON_ANGLE = 195;
    private static final int TARGET_FRAMERATE = 30;
    private GameObjectCollection gameObjects;
    private ImageReader imageReader;
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
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.gameObjects = gameObjects();
        this.imageReader = imageReader;
        windowController.setTargetFramerate(TARGET_FRAMERATE);
        windowDimensions = windowController.getWindowDimensions();
        float windowWidth = windowDimensions.x();
        minLoadedX = -CHUNK_SIZE;
        maxLoadedX = (int) (CHUNK_SIZE + (windowWidth/BLOCK_SIZE)*BLOCK_SIZE);
        // Collision optimization
        optimizeLayerCollisions();
        // Create background objects - sky, sun & halo, nighttime
        createBackgroundObjects();
        //terrain initialization
        terrain = new Terrain(windowDimensions, new Random().nextInt());
        flora = new Flora(new Random().nextInt(), terrain::groundHeightAt, imageReader::readImage);
        loadWorld(minLoadedX, maxLoadedX);
        // avatar initialization
        float middle_x = (float) (windowDimensions.x()* MIDDLE);
        Vector2 avatarInitialPosition = new Vector2(
                middle_x, terrain.groundHeightAt(middle_x)- PLACEMENT_BUFFER);
        avatar = new Avatar(avatarInitialPosition, inputListener, imageReader);
        gameObjects.addGameObject(avatar);
        avatar.setTag(AVATAR_TAG);
        // cloud creation
        Cloud cloud = new Cloud(new Vector2(0, windowDimensions.y()*CLOUD_HEIGHT_FRACTION),
                CLOUD_DIMENSIONS, windowDimensions,
                gameObjects::addGameObject, gameObjects::removeGameObject, imageReader::readImage);
        List<Block> cloudList = cloud.create();
        if (cloudList!=null) {
            for (GameObject cloudBlock : cloudList) {
                gameObjects.addGameObject(cloudBlock, Layer.BACKGROUND);
            }
        }
        avatar.addJumpObserver(cloud);
        // Create energy display
        Supplier<Double> callback = avatar::getEnergy;
        gameObjects.addGameObject(new EnergyDisplay(Vector2.ZERO ,DISPLAY_DIMENSIONS, callback), Layer.UI);
        // Set the camera to follow the game avatar
        Vector2 cameraPosition = new Vector2(0, -windowDimensions.y()*CAMERA_HEIGHT);
        setCamera(new Camera(avatar, cameraPosition,
                windowController.getWindowDimensions(), windowController.getWindowDimensions()));
    }

    /*
        Helper method to nullify specific layer collisions,
        so fewer collisions are computed and the game runs faster.
     */
    private void optimizeLayerCollisions() {
        gameObjects.layers().shouldLayersCollide(Layer.STATIC_OBJECTS, FRUIT_LAYER, true);
        gameObjects.layers().shouldLayersCollide(Layer.DEFAULT, FRUIT_LAYER, true);
        gameObjects.layers().shouldLayersCollide(Layer.DEFAULT, LEAF_AND_RAIN_LAYER, false);
        gameObjects.layers().shouldLayersCollide(Layer.STATIC_OBJECTS, LEAF_AND_RAIN_LAYER, false);
        gameObjects.layers().shouldLayersCollide(Layer.STATIC_OBJECTS, Layer.STATIC_OBJECTS, false);
        gameObjects.layers().shouldLayersCollide(LEAF_AND_RAIN_LAYER, LEAF_AND_RAIN_LAYER, false);
        gameObjects.layers().shouldLayersCollide(LEAF_AND_RAIN_LAYER, FRUIT_LAYER, false);
    }

    private void createBackgroundObjects() {
        //initialize sky background and set to layer
        GameObject sky = Sky.create(windowDimensions);
        gameObjects.addGameObject(sky, Layer.BACKGROUND);
        // night initialization
        GameObject night = Night.create(windowDimensions, DAY_CYCLE_LENGTH);
        gameObjects.addGameObject(night, Layer.FOREGROUND);
        // sun & halo initialization
        GameObject sun = Sun.create(windowDimensions, DAY_CYCLE_LENGTH);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects.addGameObject(sun, Layer.BACKGROUND);
        gameObjects.addGameObject(sunHalo, Layer.BACKGROUND);

        // Second sun + moon initialization
        GameObject secondSun = Sun.create(windowDimensions, DAY_CYCLE_LENGTH, Color.RED, SECOND_SUN_ANGLE);
        GameObject secondSunHalo = SunHalo.create(secondSun, Color.RED);
        gameObjects.addGameObject(secondSun, Layer.BACKGROUND);
        gameObjects.addGameObject(secondSunHalo, Layer.BACKGROUND);
        GameObject moon = Sun.create(windowDimensions, DAY_CYCLE_LENGTH, Color.WHITE, MOON_ANGLE);
        moon.renderer().setRenderable(imageReader.readImage(PATH_TO_MOON_IMAGE, true));
        gameObjects.addGameObject(moon, Layer.BACKGROUND);
    }

    /**
     * The update method, used to manage the addition and removal of GameObjects to/from the game.
     * If the avatar is close to the edge of the loaded world, we load the next chunk (of 25 blocks),
     * then delete the farthest loaded chunk (from the other side).
     * @param deltaTime The time, in seconds, that passed since the last invocation.
     *                  Only passed to super.update() and not directly used by us.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        reloadInfiniteWorld();
        removeGameObjectsOutOfBounds();
    }

    /* Both processes are handled by maintaining the smallest (furthest to the left) and biggest
     * (furthest to the right) horizontal coordinates loaded, adjusting them according to the avatar position,
     * generating the new chunk, and deleting everything not between those two values.
     */
    private void reloadInfiniteWorld() {
        if (avatar.getCenter().x()-minLoadedX < CHUNK_SIZE) {
            loadWorld(minLoadedX-CHUNK_SIZE, minLoadedX); // Load the world to the left
            minLoadedX -= CHUNK_SIZE;
            maxLoadedX -= CHUNK_SIZE;
        }
        else if (maxLoadedX-avatar.getCenter().x() < CHUNK_SIZE) {
            loadWorld(maxLoadedX, maxLoadedX+CHUNK_SIZE); // Load the world to the right
            minLoadedX += CHUNK_SIZE;
            maxLoadedX += CHUNK_SIZE;
        }
    }

    private void removeGameObjectsOutOfBounds() {
        for (GameObject obj : gameObjects) {
            if (obj.getTopLeftCorner().x()<minLoadedX || obj.getTopLeftCorner().x()>maxLoadedX) {
                if (obj.getTag().equals(RAIN_TAG)) continue;
                gameObjects.removeGameObject(obj, LEAF_AND_RAIN_LAYER); // Removes only leaves
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
                if (obj.getTag().equals(LEAF_TAG)) gameObjects.addGameObject(obj, LEAF_AND_RAIN_LAYER);
                else gameObjects.addGameObject(obj,FRUIT_LAYER);
            }
        }
    }
}

