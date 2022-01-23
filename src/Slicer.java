
import bagel.util.Point;
import java.util.ArrayList;
/**
 * The parent class for all slicer types.
 */
public abstract class Slicer {
    /**
     * Constructor for new slicer at start of the path
     * @param SpawnID  is a unique identifier, given by WaveControl instance.
     * @param inRoute  is the route for this slicer to take, given by WaveControl instance.
     */
    protected Slicer(int SpawnID, ArrayList<Point> inRoute){
    }
    /**
     * Stops several compiler errors and warnings.
     * This constructor doesn't actually DO anything by itself, and should not be initiated.
     */
    protected Slicer() {
    }
    /**
     * Updates the position of this slicer.
     * Also checks to see whether this slicer has escaped.
     * @param timescale  is the current timescale, given by ShadowDefend instance.
     */
    protected abstract void updatePos(int timescale);
    /**
     * Renders this slicer onto the screen.
     */
    protected abstract void renderSlicer();
    /**
     * Lowers this slicers health by the amount of damage given.
     * Also triggers death effects if this slicer was destroyed by the damage.
     * @param damage  is the amount of damage this slicer has taken
     */
    protected abstract void takeDamage(int damage);
    /**
     * Getter for spawnID variable.
     * @return returns value of spawnID
     */
    protected abstract int getSpawnID();
    /**
     * Getter for position of this slicer.
     * @return returns point which contains position of this slicer
     */
    protected abstract Point returnPoint();
    /**
     * Getter for gone variable.
     * @return returns value of gone
     */
    protected abstract boolean getDelete();
}
