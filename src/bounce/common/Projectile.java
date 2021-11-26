package bounce.common;

import bounce.client.ExplorerGameClient;
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


    public Projectile(final float x, final float y, final float vx, final float vy) {
        super(x,y);
        velocity = new Vector(vx, vy);
        gamepos = new Vector(x,y);
        damage = 1;

        // add image with offset to it renders from top left corner
        addImageWithBoundingBox(ResourceManager.getImage(ExplorerGameClient.PROJECTILE));
        id = ID_COUNTER.getAndIncrement();
    }


    public Projectile(Vector pos, Vector vel, Image img, long _id){
        super(pos.getX(), pos.getY());
        gamepos = pos;
        velocity = vel;
        addImageWithBoundingBox(img);
        id = _id;
        damage = 1;
    }
    public Projectile(Vector pos, Vector vel, Image img){
        super(pos.getX(), pos.getY());
        gamepos = pos;
        velocity = vel;
        addImageWithBoundingBox(img);
        id = ID_COUNTER.getAndIncrement();
        damage = 1;
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
