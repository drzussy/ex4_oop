package pepse.world;

/**
 * The JumpObserver interface, used as part of the Observer design pattern to safely allow objects
 * to be notified of the game avatar jumping.
 */
public interface JumpObserver {
    void notifyAboutJump();
}
