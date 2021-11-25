package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


import java.util.concurrent.atomic.AtomicLong;


public class Enemy extends Entity {

    //(Kevin) identification stuff
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;
    private int health;

    public Enemy(final float x, final float y, final float vx, final float vy, Image img) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 1;

        id = ID_COUNTER.getAndIncrement();
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Enemy(Vector pos, Vector vel, Image img, long _id){
        super(pos.getX(), pos.getY());
        gamepos = pos;
        velocity = vel;
        health = 1;
        id = _id;
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }


    public Enemy(Vector pos, Vector vel, Image img){
        super(pos.getX(), pos.getY());
        gamepos = pos;
        velocity = vel;
        health = 1;
        id = ID_COUNTER.getAndIncrement();
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
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
