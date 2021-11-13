package bounce.common;

import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Enemy extends Entity {

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;
    private int health;
    private boolean dead;

    public Enemy(final float x, final float y, final float vx, final float vy, Image img) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 1;
        dead = false;

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
        translate(velocity.scale(delta));
    } //Update base off of the velocity

    public void setHealth(final int i) {health = i; } //Add health and check if enemy is dead.
    public int getHealth() { return health; }

    public void setDead(final boolean b) {dead = b; }
    public boolean getDead() { return dead; }



}
