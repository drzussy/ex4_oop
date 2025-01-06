package src.pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Cloud implements JumpObserver{

    public static final String ASSETS_ALIEN_PNG = "assets/alien.png";
    private static final float CLOUD_BLOCK_CHANCE = 0.8f;
    private static final float CLOUD_CYCLE_TIME = 5;
    private static final float CLOUD_BUFFER = 0.1F;
    public static final int RAINDROP_CHANCES = 3;
    public static final String RAINDROP_PATH = "assets/laser.png";
    private final Vector2 topLeftCorner;
    private final Vector2 dimensions;
    private final Vector2 windowDimensions;
    private final Consumer<GameObject> gameObjectsAdd;
    private final BiFunction<String, Boolean, Renderable> imageReader;
    private final Consumer<GameObject> gameObjectsRemove;
    private List<GameObject> cloudList;

    /**
     * Construct a new cloud.
     *
     * @param topLeftCorner Position of the cloud in pixels. Note: Cloud will move with the camera.
     * @param dimensions    Width and height in window coordinates.
     * @param windowDimensions The game window dimensions,
     *                         used for resetting the cloud when it goes out-of-bounds.
     */
    public Cloud(Vector2 topLeftCorner,
                 Vector2 dimensions,
                 Vector2 windowDimensions,
                 Consumer<GameObject> gameObjectsAdd,
                 Consumer<GameObject> gameObjectsRemove,
                 BiFunction<String, Boolean,
                             Renderable > readImage){
        this.topLeftCorner = topLeftCorner;
        this.dimensions = dimensions;
        this.windowDimensions = windowDimensions;
        this.gameObjectsAdd = gameObjectsAdd;
        this.gameObjectsRemove = gameObjectsRemove;
        this.imageReader = readImage;
    }
    public List<GameObject> create () {

        int cloudWidth = (int) (dimensions.x()/Block.SIZE);
        int cloudHeight = (int) (dimensions.y()/Block.SIZE);
        List<GameObject> cloudList = new ArrayList<>();
        List<List<Boolean>> cloudShape = generateCloud(cloudWidth, cloudHeight);
        if (cloudShape==null) return null;
        for (int row = 0; row<cloudHeight; row++) {
            List<Boolean> cloudRow = cloudShape.get(row);
            for (int col = 0; col<cloudWidth; col++) {
                if (cloudRow.get(col)) {
                    float rightShift = col*Block.SIZE;
                    Vector2 cloudTopLeft = topLeftCorner.add(new Vector2(col, row).mult(Block.SIZE));
//                    Block cloud = new Block(cloudTopLeft, new RectangleRenderable(
//                            ColorSupplier.approximateColor(CLOUD_COLOR, CLOUD_COLOR_DELTA)));
                    Block cloud = new Block(cloudTopLeft, imageReader.apply(ASSETS_ALIEN_PNG, true));
                    cloud.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                    new Transition<>(cloud, (Float x)->cloud.transform().setTopLeftCornerX(x),
                            windowDimensions.x()*(-2*CLOUD_BUFFER)+rightShift,
                            windowDimensions.x()*(1+CLOUD_BUFFER)+rightShift,
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



    private static List<List<Boolean>> generateCloud (int width, int height) {
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



    @Override
    public void notifyAboutJump() {
        //cause raindrops to fall
        Random random = new Random();
        Renderable raindropRenderable = imageReader.apply (RAINDROP_PATH, true);
        for(GameObject cloudBlock: cloudList)
            if(random.nextInt(10) < RAINDROP_CHANCES){
//                cloudBlock.setCoordinateSpace(CoordinateSpace.WORLD_COORDINATES);
                Raindrop raindrop =
                        new Raindrop(cloudBlock.getCenter(),
                        raindropRenderable,
                                gameObjectsRemove);
//                cloudBlock.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
                gameObjectsAdd.accept(raindrop);

            }
    }
}
