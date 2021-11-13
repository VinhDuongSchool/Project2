package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.Entity;
import jig.Vector;
import jig.ResourceManager;
import org.newdawn.slick.Image;


public class Projectile extends Entity {

    private Vector velocity; //Velocity vectore.
    private boolean fired;
    public Vector gamepos;


    public Projectile(final float x, final float y, final float vx, final float vy) {
        super(x,y);
        velocity = new Vector(vx, vy);
        gamepos = new Vector(32,32);

        // add image with offset to it renders from top left corner
        addImageWithBoundingBox(ResourceManager.getImage(ExplorerGameClient.PROJECTILE));
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity

    public void setFired(final boolean b) { fired = b; }

    public Boolean getFired() { return fired; }

    public void update(final int delta) {
        gamepos = gamepos.add(velocity.scale(delta)); //Move the projectile based off of the gamepos.
    } //Update base off of the velocity
}
