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


    public Projectile(final float x, final float y, final float vx, final float vy, long _id, final lib.DIRS d) {
        super(x,y);
        velocity = new Vector(vx, vy);
        gamepos = new Vector(x,y);
        damage = 1;

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

        var UP_V = new Vector(0.2f,0).unit().scale(.2f); //To find out what the velocity should be based off the mouse direction.
        var LEFT_V = new Vector(0,-.2f).unit().scale(.2f);
        Vector tempVector;

        switch (d){
            case NORTH:
                tempVector = UP_V;
                break;
            case SOUTH:
                tempVector = UP_V.scale(-1);
                break;
            case WEST:
                tempVector = LEFT_V;
                break;
            case EAST:
                tempVector = LEFT_V.scale(-1);
                break;
            case NORTHEAST:
                tempVector = UP_V.add(LEFT_V.scale(-1));
                break;
            case NORTHWEST:
                tempVector = UP_V.add(LEFT_V);
                break;
            case SOUTHEAST:
                tempVector = UP_V.scale(-1).add(LEFT_V.scale(-1));
                break;
            case SOUTHWEST:
                tempVector = UP_V.scale(-1).add(LEFT_V);
                break;
            default:
                tempVector = new Vector(0,0);
                assert false : "unreachable";
                break;
        }

        velocity = tempVector;
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        id = _id;

    }

    public Projectile(Vector pos, Vector vel, long _id, final lib.DIRS d){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(), _id, d);
    }

    public Projectile(Vector pos, Vector vel, final lib.DIRS d){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(),  ID_COUNTER.getAndIncrement(), d);
    }

    public Projectile(final float x, final float y, final float vx, final float vy, final lib.DIRS d) {
        this(x,y,vx,vy,  ID_COUNTER.getAndIncrement(),d);
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
