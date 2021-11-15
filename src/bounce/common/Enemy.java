package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;


public class Enemy extends Entity {

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;
    private int health;

    public Enemy(final float x, final float y, final float vx, final float vy, Image img) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 1;

        // add image with offset to it renders from top left corner
        addImage(img);
        Vector[] one = new Vector[]{
                new Vector(- 32.0f, 0.0f),
                new Vector(0.0f,  16.0f),
                new Vector( 32.0f, 0.0f),
                new Vector(0.0f, - 16.0f)
        };
        ConvexPolygon test = new ConvexPolygon(one);
        addShape(test,new Vector(0.0f, 32.0f));
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




}
