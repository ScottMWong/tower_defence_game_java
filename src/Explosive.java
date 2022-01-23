import bagel.Image;
import bagel.util.Point;
import bagel.util.Rectangle;
import bagel.util.Vector2;
/**
 * Implementation of Aircraft projectile/explosive.
 */
public class Explosive extends Projectile{
    private Image image;
    private static final int speedpxperframe = 10;
    private double x;
    private double y;
    private static final int damage = 500;
    private Point currentpos;
    private boolean delete;
    private Vector2 move;
    private int explosiveTimerms;
    private static final double msperframe = 1000/60;
    private static final double explosionwaitms = 2000;
    /**
     * Constructor for aircraft projectile.
     * @param xpos  is x position of aircraft creating this projectile
     * @param ypos  is y position of aircraft creating this projectile
     */
    Explosive(double xpos, double ypos) {
        x = xpos;
        y = ypos;
        image = new Image("res/images/explosive.png");

        currentpos = new Point(x,y);
        explosiveTimerms = 0;
        delete = false;
    }
    /**
     * Updates the position and state of this projectile.
     * Also calls damage dealing method for slicer if appropriate.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected void ProjectileUpdate(int timescale) {
        explosiveTimerms += msperframe * timescale;
        if (explosiveTimerms > explosionwaitms) {
            Rectangle AoE = new Rectangle(x - 100,y - 100, 200,200);
            ShadowDefend.waveControl.ExplosiveDamage(damage,AoE);
            delete = true;
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
