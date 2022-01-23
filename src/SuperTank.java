import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
/**
 * Implementation of the Super Tank tower type.
 */
public class SuperTank extends Tower {
    private Image image;
    private double x;
    private double y;
    private Point position;
    private Point targetposition;
    //dir is normalised directional vector
    private Vector2 facing;
    private double renderAngle;
    private DrawOptions renderSettings;
    //Gameplay values
    private static final int cooldownms = 500;
    private static final double msperframe = 1000/60;
    private static final int range = 150;
    private int timeSinceShotms;
    private int targetID;
    private Rectangle collisionBox;
    /**
     * Constructor for the SuperTank class.
     * @param point  The point at which the mouse was clicked to place the super tank.
     */
    SuperTank (Point point) {
        image = new Image("res/images/supertank.png");
        x = point.x;
        y = point.y;
        position = point;
        facing = Vector2.up;
        renderAngle = Math.atan2(facing.y,facing.x);
        renderSettings = new DrawOptions();
        renderSettings.setRotation(renderAngle);
        timeSinceShotms = 0;
        targetposition = null;
        collisionBox = new Rectangle(x - 32, y - 32, 64, 64);
    }
    /**
     * Updates the current state of this tower.
     * If tower is ready to fire, checks if a valid target can be found.
     * If so, creates a projectile.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected void towerUpdate(int timescale) {
        timeSinceShotms += timescale * msperframe;
        if (timeSinceShotms >= cooldownms) {
            targetID = ShadowDefend.waveControl.findTargetID(range,position);
            if (targetID != -1) {
                timeSinceShotms = 0;
                ShadowDefend.waveControl.projectileList.add(new SuperTankProjectile(targetID,x,y));
                targetposition = ShadowDefend.waveControl.findSlicerPoint(targetID);
                facing = new Vector2((targetposition.x - x),(targetposition.y - y));
                renderAngle = Math.atan2(facing.y,facing.x);
                renderAngle -= 90 * (180/Math.PI);
                renderSettings.setRotation(renderAngle);
            }
        }
    }
    /**
     * Renders this tower onto the screen.
     */
    protected void towerRender() {
        image.draw(x,y,renderSettings);
    }
    /**
     * Checks if the given point intersects with this tower.
     * @param point  is the point to check
     * @return returns true if the point given intersects with this tower, otherwise returns false.
     */
    protected boolean towerCollision(Point point) {
        if (collisionBox.intersects(point)) {
            return true;
        }
        else {
            return false;
        }
    }
}
