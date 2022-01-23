import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of the Airplane tower type.
 */
public class Aircraft extends Tower {
    private Image image;
    private double x;
    private double y;
    //dir is normalised directional vector
    private Vector2 facing;
    private double renderAngle;
    private DrawOptions renderSettings;
    //Gameplay values
    private int cooldownms;
    private static final double msperframe = 1000/60;
    private int timeSinceShotms;
    private static final int speedpxframe = 3;
    private static boolean spawnHorizontal = true;
    private boolean moveHorizontal;
    private static final Rectangle playfield = new Rectangle (0,100, 1024,643);
    private static final Rectangle windowsize = new Rectangle (0,0, 1024,768);
    private boolean toDelete;

    /**
     * Constructor for the Aircraft class.
     * @param point  The point at which the mouse was clicked to place the aircraft.
     */
    Aircraft (Point point) {
        image = new Image("res/images/airsupport.png");
        toDelete = false;
        if (spawnHorizontal) {
            moveHorizontal = true;
            spawnHorizontal = false;

            setCooldown();
            timeSinceShotms = 0;
            x = 0;
            y = point.y;
            facing = Vector2.down;
            renderAngle = Math.atan2(facing.y,facing.x);
            renderSettings = new DrawOptions();
            renderSettings.setRotation(renderAngle);
        }
        else {
            moveHorizontal = false;
            spawnHorizontal  = true;

            setCooldown();
            timeSinceShotms = 0;
            x = point.x;
            y = 0;
            facing = Vector2.left;
            renderAngle = Math.atan2(facing.y,facing.x);
            renderSettings = new DrawOptions();
            renderSettings.setRotation(renderAngle);
        }
    }
    /**
     * Updates the current state of this tower.
     * If tower is ready to fire, checks if a valid target can be found.
     * If so, creates a projectile. Also handles movement.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected void towerUpdate(int timescale) {
        if (moveHorizontal) {
            x += timescale * speedpxframe;
        }

        else {
            y += timescale * speedpxframe;
        }

        if (!windowsize.intersects(new Point(x,y))) {
            toDelete = true;
        }
        else {
            if (playfield.intersects(new Point(x,y))) {
                timeSinceShotms += timescale * msperframe;
                if (timeSinceShotms >= cooldownms) {
                    ShadowDefend.waveControl.projectileList.add(new Explosive(x,y));
                    timeSinceShotms = 0;
                    setCooldown();
                }
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
     * @return  Always returns false since aircraft should never interfere with tower placement.
     */
    protected boolean towerCollision(Point point) {
        return false;
    }

    /**
     * Resets aircraft spawn direction to horizontal.
     */
    protected static void resetAircraftOrientation() {
        spawnHorizontal = true;
    }

    /**
     * Getter for toDelete variable.
     * @return returns value of toDelete
     */
    protected boolean getDelete() {
        return toDelete;
    }

    private void setCooldown() {
        //this line originating for https://stackoverflow.com/questions/363681/how-do-i-generate-random-integers-within-a-specific-range-in-java,
        //Top answer by Greg Case
        cooldownms = ThreadLocalRandom.current().nextInt(1000, 2001);
    }
}
