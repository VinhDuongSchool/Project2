package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.ResourceManager;
import jig.Vector;

import java.util.concurrent.atomic.AtomicLong;


public class Projectile extends Entity {

    //(Kevin) identification stuff
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    private Vector velocity; //Velocity vectore.
    private boolean hit;
    public int damage;
    protected Vector gamepos;
    public lib.DIRS curdir;
    public Object sender;


    public Projectile(Vector gp, Vector v, long _id, lib.DIRS d) {
        super(gp);
        velocity = v;
        gamepos = gp;
        damage = 1;
        sender = this;

        // add image with offset to it renders from top left corner
        if (d == lib.DIRS.NORTHWEST || d == lib.DIRS.SOUTHEAST) { //Check what direction the player is moving in a load the appropiate image.
            addImage(ResourceManager.getImage(ExplorerGameClient.UD));
        } else if (d == lib.DIRS.SOUTHWEST || d == lib.DIRS.NORTHEAST) {
            addImage(ResourceManager.getImage(ExplorerGameClient.LR));
        } else if (d == lib.DIRS.WEST || d == lib.DIRS.EAST) {
            addImage(ResourceManager.getImage(ExplorerGameClient.DR));
        } else if (d == lib.DIRS.NORTH || d == lib.DIRS.SOUTH) {
            addImage(ResourceManager.getImage(ExplorerGameClient.UR));
        }

        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        id = _id;
        curdir = d;
    }

    public Projectile(Vector pos, Vector vel, final lib.DIRS d){
        this(pos, vel,  ID_COUNTER.getAndIncrement(), d);
    }

    public Projectile(Vector pos, Vector vel, lib.DIRS d, Object s){
        this(pos, vel, d);
        sender = s;
    }

    // this should be needed?
//    public Projectile(Vector pos, Vector vel, lib.DIRS d, long i,  Object s){
//        this(pos, vel, i, d);
//        sender = s;
//    }


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


    public void setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        setPosition(gamepos);
    }

    public Vector getGamepos() {
        return gamepos;
    }
}
