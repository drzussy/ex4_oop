package pepse.world;

/**
 * Represents an observer in the Observer design pattern that responds to jump events.
 * <p>
 * The {@code JumpObserver} interface provides a contract for objects that wish to be
 * notified when the game avatar performs a jump. Implementations of this interface
 * should define the specific behavior to execute upon receiving a jump notification.
 * </p>
 */
public interface JumpObserver {
    /**
     * Notifies the observer about a jump event.
     * <p>
     * This method is called when the game avatar jumps, allowing implementing classes
     * to perform actions in response to the jump event.
     * </p>
     */
    void notifyAboutJump();
}
