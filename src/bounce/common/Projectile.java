package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.concurrent.atomic.AtomicLong;


public class Projectile extends Entity {

    //(Kevin) identification stuff
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    private Vector velocity; //Velocity vectore.
    private boolean hit;
    public int damage;
    public Vector gamepos;
    public lib.DIRS curdir;


    public Projectile(final float x, final float y, final float vx, final float vy, long _id) {
        super(x,y);
        velocity = new Vector(vx, vy);
        gamepos = new Vector(x,y);
        damage = 1;

        // add image with offset to it renders from top left corner
        addImage(ResourceManager.getImage(ExplorerGameClient.PROJECTILE));
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        id = _id;
    }

    public Projectile(Vector pos, Vector vel, Image img, long _id){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(), _id);
    }

    public Projectile(Vector pos, Vector vel, Image img){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(),  ID_COUNTER.getAndIncrement());
    }

    public Projectile(final float x, final float y, final float vx, final float vy) {
        this(x,y,vx,vy,  ID_COUNTER.getAndIncrement());
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity

    public void setHit(final boolean b) { hit = b; }

    public Boolean getHit() { return hit; }

    public void update(final int delta) {
        gamepos = gamepos.add(velocity.scale(delta)); //Move the projectile based off of the gamepos.
        setPosition(gamepos);
    } //Update base off of the velocity
}
