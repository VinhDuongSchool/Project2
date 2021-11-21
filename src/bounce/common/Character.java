package bounce.common;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Character extends Entity {

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;
    public final long client_id;


    public Character(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y);
        gamepos = new Vector(32,32);
        velocity = new Vector(vx, vy);
        client_id = id;

        // add image with offset to it renders from top left corner
        addImageWithBoundingBox(img);
    }
    public Character(Vector pos, Vector vel, Image img, long id){
        super(pos.getX(), pos.getY());
        gamepos = pos;
        velocity = vel;
        client_id = id;
        addImage(img);

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

    public long getClient_id() {
        return client_id;
    }
}
