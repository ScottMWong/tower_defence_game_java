import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;

/**
 * Implementation of Tank projectile
 */
public class TankProjectile extends Projectile {
    private Image image;
    private int targetID;
    private static final int speedpxperframe = 10;
    private double x;
    private double y;
    private static final int damage = 1;
    private Point currentpos;
    private Point targetpos;
    private boolean delete;
    private Vector2 move;
    private Rectangle targetbound;

    /**
     * Constructor for tank projectile.
     * @param TargetID  is generated ID of slicer targeted
     * @param xpos  is x position of tank creating this projectile
     * @param ypos  is y position of tank creating this projectile
     */
    TankProjectile(int TargetID, double xpos, double ypos) {
        targetID = TargetID;
        x = xpos;
        y = ypos;
        image = new Image("res/images/tank_projectile.png");

        targetpos = ShadowDefend.waveControl.findSlicerPoint(targetID);
        targetbound = new Rectangle(targetpos.x - 32,targetpos.y - 32,64,64);
        currentpos = new Point(x,y);
        delete = false;
    }
    /**
     * Updates the position and state of this projectile.
     * Also calls damage dealing method for slicer if appropriate.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected void ProjectileUpdate(int timescale) {
        targetpos = ShadowDefend.waveControl.findSlicerPoint(targetID);
        if (targetpos == null) {
            delete = true;
        }
        else {
            move = new Vector2((targetpos.x - currentpos.x),(targetpos.y - currentpos.y));
            move = move.normalised();
            x += move.x * timescale * speedpxperframe;
            y += move.y * timescale * speedpxperframe;
            currentpos = new Point(x,y);
            targetbound.moveTo(new Point(targetpos.x - 32,targetpos.y - 32));
            if (targetbound.intersects(currentpos)) {
                delete = true;
                ShadowDefend.waveControl.ProjectileDamage(damage, targetID);
            }
        }
    }
    /**
     * Renders this projectile onto the screen.
     */
    protected void ProjectileRender() {
        image.draw(x,y);
    }
    /**
     * Getter for the delete variable
     * @return returns the value of delete
     */
    protected boolean getDelete() {
        return delete;
    }

}
