import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

/**
 * The parent class for all tower types.
 */
public abstract class Tower {
    private Image image;
    private double x;
    private double y;
    //dir is normalised directional vector
    private Vector2 face;
    private double dirLength;
    private double distTravelled;
    private double renderAngle;
    private DrawOptions renderSettings;
    //Gameplay values

    /**
     * Updates the current state of this tower.
     * If tower is ready to fire, checks if a valid target can be found.
     * If so, creates a projectile. Also handles movement if appropriate.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected abstract void towerUpdate(int timescale);
    /**
     * Renders this tower onto the screen.
     */
    protected abstract void towerRender();

    /**
     * Checks if the given point intersects with this tower.
     * @param point  is the point to check
     * @return returns true if the point given intersects with this tower, otherwise returns false.
     */
    protected abstract boolean towerCollision(Point point);
}
