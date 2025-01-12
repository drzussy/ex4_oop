package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static pepse.util.PepseConstants.BLOCK_SIZE;
import static pepse.util.PepseConstants.LEAF_AND_RAIN_LAYER;

/**
 * A class for creating a cloud, which moves across the camera and causes rain when the avatar jumps.
 * Implements JumpObserver as part of the Observer design pattern.
 */
public class Cloud implements JumpObserver{
    private static final String PATH_TO_CLOUD_BLOCK_IMAGE = "assets/alien.png";
    private static final String PATH_TO_RAINDROP_IMAGE = "assets/laser.png";
    private static final float CLOUD_BLOCK_CHANCE = 0.6f;
    private static final float CLOUD_CYCLE_TIME = 15;
    private static final float CLOUD_BUFFER = 0.1F;
    private static final float LEFT_CLOUD_BUFFER = -2*CLOUD_BUFFER;
    private static final float RIGHT_CLOUD_BUFFER = 1+CLOUD_BUFFER;
    private static final float RAINDROP_CHANCES = 0.2F;
    private static final float TEARDROP_FALL_TIME = 2f;
    private static final Vector2 CLOUD_DIMENSIONS = new Vector2(300, 160);
    private static final float CLOUD_HEIGHT_FRACTION = 0.1F;
    private final Vector2 topLeftCorner;
//    private final Vector2 dimensions;
    private final Vector2 windowDimensions;
    private final BiFunction<String, Boolean, Renderable> imageReader;
    private final BiConsumer<GameObject, Integer> gameObjectsAdd;
    private final BiConsumer<GameObject, Integer> gameObjectsRemove;
    private final Renderable raindropRenderable;
    private List<Block> cloudList;

    /**
     * Construct a new cloud.
     *
     * @param windowDimensions  The game window dimensions,
     *                          used for resetting the cloud when it goes out-of-bounds.
     * @param gameObjectsAdd    A callback to the method used for adding GameObjects to the game.
     *                          Used to create raindrops.
     * @param gameObjectsRemove A callback to the method used for removing GameObjects from the game.
     *                          Passed to the created raindrops to enable their self-destruct.
     * @param readImage         A callback to the method used to create ImageRenderables from files.
     */
    public Cloud(Vector2 windowDimensions,
                 BiConsumer<GameObject, Integer> gameObjectsAdd,
                 BiConsumer<GameObject, Integer> gameObjectsRemove,
                 BiFunction<String, Boolean, Renderable > readImage){
        this.topLeftCorner = new Vector2(0, windowDimensions.y()*CLOUD_HEIGHT_FRACTION);
//        this.dimensions = CLOUD_DIMENSIONS;
        this.windowDimensions = windowDimensions;
        this.gameObjectsAdd = gameObjectsAdd;
        this.gameObjectsRemove = gameObjectsRemove;
        this.imageReader = readImage;
        this.raindropRenderable = imageReader.apply (PATH_TO_RAINDROP_IMAGE, true);
    }

    /**
     * Creates a new cloud, which is returned as a list of blocks. This list is saved as a field,
     * and is used to generate raindrops. Only the last cloud created with this method will cause rainfall!
     *
     * @return A list of blocks which constitute the cloud.
     */
    public List<Block> create () {
        if (!cloudList.isEmpty()) return cloudList; // to prevent multiple creations with the same cloud
        int cloudWidth = (int) (CLOUD_DIMENSIONS.x()/BLOCK_SIZE);
        int cloudHeight = (int) (CLOUD_DIMENSIONS.y()/BLOCK_SIZE);
        List<Block> cloudList = new ArrayList<>();
        List<List<Boolean>> cloudShape = generateCloudShape(cloudWidth, cloudHeight);
        if (cloudShape==null) return null;
        Renderable cloudRenderable =  imageReader.apply(PATH_TO_CLOUD_BLOCK_IMAGE, true);
        for (int row = 0; row<cloudHeight; row++) {
            List<Boolean> cloudRow = cloudShape.get(row);
            for (int col = 0; col<cloudWidth; col++) {
                if (cloudRow.get(col)) {
                    float rightShift = col*BLOCK_SIZE;
                    Vector2 cloudTopLeft = topLeftCorner.add(new Vector2(col, row).mult(BLOCK_SIZE));
                    Block cloud = new Block(cloudTopLeft,cloudRenderable);
                    cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    new Transition<>(cloud, (Float x)->cloud.transform().setTopLeftCornerX(x),
                            windowDimensions.x()*(LEFT_CLOUD_BUFFER)+rightShift,
                            windowDimensions.x()*(RIGHT_CLOUD_BUFFER)+rightShift,
                            Transition.LINEAR_INTERPOLATOR_FLOAT, CLOUD_CYCLE_TIME,
                            Transition.TransitionType.TRANSITION_LOOP,
                            null);
                    cloudList.add(cloud);
                }
            }
        }
        this.cloudList = cloudList;
        return cloudList;
    }

    private static List<List<Boolean>> generateCloudShape(int width, int height) {
        if (width<=0 || height<=0) return null;
        Random random = new Random();
        List<List<Boolean>> cloudSpotsToFill = new ArrayList<>();
        for (int row=0; row<height; row++) {
            List<Boolean> cloudRow = new ArrayList<>();
            for (int col=0; col<width; col++) {
                cloudRow.add(random.nextFloat()<CLOUD_BLOCK_CHANCE);
            }
            cloudSpotsToFill.add(cloudRow);
        }
        return cloudSpotsToFill;
    }

    /**
     * Allows notifying the cloud that the avatar has jumped, meaning it should cause rainfall,
     * as part of the Observer design pattern.
     */
    @Override
    public void notifyAboutJump() {
        // Randomly select cloud blocks from which raindrops will fall
        Random random = new Random();
        for(GameObject cloudBlock: cloudList)
            if(random.nextFloat() < RAINDROP_CHANCES){
                Raindrop raindrop = new Raindrop(cloudBlock.getCenter(), raindropRenderable);
                gameObjectsAdd.accept(raindrop, LEAF_AND_RAIN_LAYER);
                raindrop.renderer().fadeOut(
                        TEARDROP_FALL_TIME, ()-> gameObjectsRemove.accept(raindrop, LEAF_AND_RAIN_LAYER));
            }
    }
}
