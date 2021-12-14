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
    private Vector gamepos;
    public lib.DIRS curdir;
    public Object sender;


    public Projectile(final float x, final float y, final float vx, final float vy, long _id, final lib.DIRS d) {
        super(x,y);
        velocity = new Vector(vx, vy);
        gamepos = new Vector(x,y);
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

        velocity = lib.dir_enum_to_unit_vector(d).scale(0.4f);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        id = _id;

    }

    public Projectile(Vector pos, Vector vel, long _id, final lib.DIRS d){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(), _id, d);
        velocity = vel;
    }

    public Projectile(Vector pos, Vector vel, final lib.DIRS d){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(),  ID_COUNTER.getAndIncrement(), d);
    }

    public Projectile(final float x, final float y, final float vx, final float vy, final lib.DIRS d) {
        this(x,y,vx,vy,  ID_COUNTER.getAndIncrement(),d);
    }

    public Projectile(Vector pos, Vector vel, lib.DIRS d, Object s){
        this(pos, vel, d);
        sender = s;
    }

    public Projectile(Vector pos, Vector vel, lib.DIRS d, long i,  Object s){
        this(pos, vel, i, d);
        sender = s;
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


    public void setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        setPosition(gamepos);
    }

    public Vector getGamepos() {
        return gamepos;
    }
}
