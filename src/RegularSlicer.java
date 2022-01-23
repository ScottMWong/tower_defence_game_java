import bagel.DrawOptions;
import bagel.Image;
import bagel.util.Point;
import bagel.util.Vector2;

import java.util.ArrayList;
/**
 * Implementation of the Regular Slicer slicer type.
 */
public class RegularSlicer extends Slicer {
    private Image image;
    private int nextpointnum;
    private ArrayList<Point> route;
    private Point next;
    private Point prev;
    private double x;
    private double y;
    private Vector2 dir;
    private double dirLength;
    private double distTravelled;
    private double renderAngle;
    private DrawOptions renderSettings;
    private int health;
    private int spawnID;
    private static final int reward = 2;
    private static final int startHealth = 1;
    private static final double speedPxFrame = 2;
    private static final int penalty = 1;
    private boolean gone;
    /**
     * Constructor for new regular slicer at start of the path
     * @param SpawnID  is a unique identifier, given by WaveControl instance.
     * @param inRoute  is the route for this slicer to take, given by WaveControl instance.
     */
    RegularSlicer(int SpawnID, ArrayList<Point> inRoute){
        nextpointnum = 1;
        spawnID = SpawnID;
        route = new ArrayList<Point>();
        for (Point point : inRoute) {
            route.add(point);
        }
        prev = route.get(0);
        next = route.get(1);
        x = prev.x;
        y = prev.y;
        dir = new Vector2((next.x - prev.x),(next.y - prev.y));
        dirLength = dir.length();
        dir = dir.normalised();
        renderAngle = 0;
        renderSettings = new DrawOptions();
        renderSettings.setRotation(renderAngle);

        image = new Image("res/images/slicer.png");
        health = startHealth;
        gone = false;
    }
    /**
     * Constructor for when a super slicer was destroyed
     * @param SpawnID  is a unique identifier, given by WaveControl instance.
     * @param inRoute  is the route for this slicer to take, given by WaveControl instance.
     * @param xpos  is the x position where the super slicer died
     * @param ypos  is the y position where the super slicer died
     * @param nextpoint  is the number of the point the super slicer was travelling to
     */
    RegularSlicer(int SpawnID, ArrayList<Point> inRoute, int nextpoint, double xpos, double ypos){
        nextpointnum = nextpoint;
        spawnID = SpawnID;
        route = new ArrayList<Point>();
        for (Point point : inRoute) {
            route.add(point);
        }
        prev = route.get(nextpoint - 1);
        next = route.get(nextpoint);
        x = xpos;
        y = ypos;
        dir = new Vector2((next.x - x),(next.y - y));
        dirLength = dir.length();
        dir = dir.normalised();
        renderAngle = 0;
        renderSettings = new DrawOptions();
        renderSettings.setRotation(renderAngle);

        image = new Image("res/images/slicer.png");
        health = startHealth;
        gone = false;
    }
    /**
     * Updates the position of this slicer.
     * Also checks to see whether this slicer has escaped.
     * @param timescale  is the current timescale, given by ShadowDefend instance.
     */
    protected void updatePos(int timescale) {
        distTravelled += timescale * speedPxFrame;
        //if you've hit the next point follow this loop coded to prevent weird drifting at high speeds!
        while (distTravelled > dirLength && !gone) {
            nextpointnum += 1;
            if (nextpointnum >= route.size()) {
                escaped();
                gone = true;
            }
            //perfectly line up to new position
            else {
                x = next.x;
                y = next.y;
                prev = next;
                next = route.get(nextpointnum);
                distTravelled -= dirLength;
                dir = new Vector2((next.x - prev.x),(next.y - prev.y));
                dirLength = dir.length();
                dir = dir.normalised();
                renderAngle = Math.atan2(dir.y,dir.x);
                renderSettings.setRotation(renderAngle);
            }
        }
        x += dir.x * timescale * speedPxFrame;
        y += dir.y * timescale * speedPxFrame;
    }
    /**
     * Renders this slicer onto the screen.
     */
    protected void renderSlicer() {
        image.draw(x,y,renderSettings);
    }
    /**
     * Lowers this slicers health by the amount of damage given.
     * Also triggers death effects if this slicer was destroyed by the damage.
     * @param damage  is the amount of damage this slicer has taken
     */
    protected void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            destroyed();
            gone = true;
        }
    }

    private void destroyed() {
        ShadowDefend.reward(reward);
        gone = true;
    }

    private void escaped() {
        ShadowDefend.penalty(penalty);
        gone = true;
    }
    /**
     * Getter for spawnID variable.
     * @return returns value of spawnID
     */
    protected int getSpawnID() {
        return spawnID;
    }
    /**
     * Getter for position of this slicer.
     * @return returns point which contains position of this slicer
     */
    protected Point returnPoint() {
        return new Point(x,y);
    }
    /**
     * Getter for gone variable.
     * @return returns value of gone
     */
    protected boolean getDelete() {
        return gone;
    }
}
