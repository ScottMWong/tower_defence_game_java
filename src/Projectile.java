/**
 * The parent class for all projectile types.
 */
public abstract class Projectile {
    /**
     * Updates the position and state of this projectile.
     * Also calls damage dealing method for slicer if appropriate.
     * @param timescale  is the current timescale, given from ShadowDefend instance
     */
    protected abstract void ProjectileUpdate(int timescale);
    /**
     * Renders this projectile onto the screen.
     */
    protected abstract void ProjectileRender();
    /**
     * Getter for the delete variable
     */
    protected abstract boolean getDelete();
}
