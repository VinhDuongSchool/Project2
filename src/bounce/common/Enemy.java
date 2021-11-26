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

    public Enemy(final float x, final float y, final float vx, final float vy, Image img, long _id) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 1;
        id = _id;
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Enemy(Vector pos, Vector vel, Image img, long _id){
        this(pos.getX(),pos.getY(),vel.getX(),vel.getY(),img, _id);
    }

    public Enemy(Vector pos, Vector vel, Image img){
        this(pos.getX(),pos.getY(),vel.getX(),vel.getY(),img, ID_COUNTER.getAndIncrement());
    }

    public Enemy(final float x, final float y, final float vx, final float vy, Image img) {
        this(x,y,vx,vy,img, ID_COUNTER.getAndIncrement());
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity

    public void update(final int delta) {
        gamepos = gamepos.add(velocity.scale(delta));
        setPosition(gamepos);
    } //Update base off of the velocity

    public void setHealth(final int i) {health = i; } //Add health and check if enemy is dead.
    public int getHealth() { return health; }




}
