package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;


public class Enemy extends Entity {

    //(Kevin) identification stuff
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    private Vector velocity; //Velocity vectore.
    private Vector gamepos;
    private int health;
    public Tile goal;
    public float speed;
    public long attack_timer;
    public boolean moving = true;
    public Vector dir;
    private ArrayList<Shape> attack_shapes;

    public Enemy(final float x, final float y, final float vx, final float vy, Image img, long _id) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 3;
        id = _id;
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        attack_shapes = new ArrayList<>();
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

    public void update(final int delta, Tile curt, Character[] characterVector) {
        Vector cp = Arrays.stream(characterVector)
                .map(Character::getGamepos)
                .min((a,b)-> (int)(gamepos.distance(a) - gamepos.distance(b))).get();

       float distance = gamepos.distance(cp);

        if (distance <= 40 && moving) { //If the distance is less than 40
            moving = false; //The enemy should stop moving
            attack_timer = 3000; //Set a timer.
            var offsetdirs = new HashMap<lib.DIRS, Vector>(){{ //Offset for the boxes
                put(lib.DIRS.NORTH, new Vector(32, 0));
                put(lib.DIRS.WEST, new Vector(0,-32));
                put(lib.DIRS.EAST, new Vector(0,32 ));
                put(lib.DIRS.SOUTH, new Vector(-32,0));
                put(lib.DIRS.NORTHWEST, new Vector(32,-32));
                put(lib.DIRS.NORTHEAST, new Vector(32,32));
                put(lib.DIRS.SOUTHWEST, new Vector(-32,-32));
                put(lib.DIRS.SOUTHEAST, new Vector(-32,32));
            }};

            var s = new ConvexPolygon(lib.sqr.getPoints()); //Set a box to point right
            attack_shapes.add(s);
            addShape(s, offsetdirs.get(lib.DIRS.NORTH));

        } else if (moving) { //If the enemy is still moving then continue getting close to the player.
            if (goal == null){
                goal = curt.next;
            }

            //Kevin, if the distance to the next goal is less than how much we move setup next goal
            var vs = velocity.scale(delta);
            var gp = goal.gamepos;
            if(gamepos.distanceSquared(gp) < vs.lengthSquared() || gamepos.equals(gp)){
                gamepos = goal.gamepos;
                goal = curt.next;
            }else {
                gamepos = gamepos.add(velocity.scale(delta));
            }
            //Kevin, setup next goal direction
            velocity = goal.gamepos.subtract(gamepos).unit().scale(0.05f);


        } else if (attack_timer <= 0) { //If the attack timer reaches zero
            moving = true; //Then the enemy can move and remove the boxes.
            attack_shapes.stream().forEach(this::removeShape);
            attack_shapes.clear();

        } else { //else decrement the attack timer.
            attack_timer = attack_timer - delta;
        }
        setPosition(gamepos);

    } //Update base off of the velocity

    public void setHealth(final int i) {health = i; } //Add health and check if enemy is dead.
    public int getHealth() { return health; }


    public void setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        setPosition(gamepos);
    }

    public Vector getGamepos() {
        return gamepos;
    }
}
