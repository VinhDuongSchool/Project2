package bounce.common;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class EnemyClass extends Entity {

    private Vector velocity; //Velocity vectore.


    public EnemyClass(final float x, final float y, final float vx, final float vy, Image img) {
        super(x,y);

        velocity = new Vector(vx, vy);
        addImageWithBoundingBox(img);
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity

    public void update(final int delta) {
        translate(velocity.scale(delta));
    } //Update base off of the velocity



}
