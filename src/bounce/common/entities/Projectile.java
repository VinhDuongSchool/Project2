package bounce.common.entities;

import bounce.common.lib;
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
    public Entity sender;


    public Projectile(Vector gp, Vector v, lib.DIRS d, Entity s, int dmg){
        this(gp, v, d, ID_COUNTER.incrementAndGet(), s, dmg);
    }

    public Projectile(Vector gp, Vector v, lib.DIRS d, long _id, Entity s, int dmg){
        this(gp, v, d, _id, s.getClass(), dmg);
        sender = s;
    }

    //main constructor
    public Projectile(Vector gp, Vector v, lib.DIRS d, long _id, Class<? extends Entity> sc, int dmg){
        super(gp);
        velocity = v;
        gamepos = gp;
        damage = dmg;
        id = _id;
        curdir = d;

        addShape(new ConvexPolygon(lib.sqr.getPoints()));

        if (sc == Archer.class || sc == ShadowArcher.class) {
            // add image with offset to it renders from top left corner
            if (d == lib.DIRS.NORTHWEST || d == lib.DIRS.SOUTHEAST) { //Check what direction the player is moving in a load the appropiate image.
                addImage(ResourceManager.getImage(lib.UD));
            } else if (d == lib.DIRS.SOUTHWEST || d == lib.DIRS.NORTHEAST) {
                addImage(ResourceManager.getImage(lib.LR));
            } else if (d == lib.DIRS.WEST || d == lib.DIRS.EAST) {
                addImage(ResourceManager.getImage(lib.DR));
            } else if (d == lib.DIRS.NORTH || d == lib.DIRS.SOUTH) {
                addImage(ResourceManager.getImage(lib.UR));
            }
        } else if (sc == Mage.class) {
            addImage(ResourceManager.getImage(lib.MAGEFIREBALL));
        } else if (sc == Rogue.class) {
            addImage(ResourceManager.getImage(lib.ROGUEBOMB));
        }
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
