package bounce.common;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Character extends Entity {

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;


    public Character(final float x, final float y, final float vx, final float vy, Image img) {
        super(x,y);
        gamepos = new Vector(32,32);
        velocity = new Vector(vx, vy);

        // add image with offset to it renders from top left corner
        addImageWithBoundingBox(img);
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity

    public void update(final int delta) {
        gamepos = gamepos.add(velocity.scale(delta));
    } //Update base off of the velocity



}
