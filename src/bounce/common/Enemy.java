package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Arrays;
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
    public Vector dir;
    private ArrayList<Shape> attack_shapes;
    public lib.DIRS curdir;

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

    public void attack(Vector cp){
        attack_timer = 3000; //Set a timer.

        //Kevin, attack in the direction of the player
        //not using lib.dir_from_point_to_point incase we need to add multiple shapes depending on direction

        double ang = (cp.angleTo(gamepos)+180 + 360 - 45)%360;
        int diridx = (int)Math.round((ang)/45);
        var offsetdir = lib.dir_enum_to_dir_vector(lib.angle_index_to_dir[diridx]);
        var s = new ConvexPolygon(lib.sqr.getPoints()); //Set a box to point right
        attack_shapes.add(s);
        addShape(s, offsetdir.scale(32.0f));
    }

    public void update(final int delta, Tile curt, Character[] characterVector) {
        Vector cp = Arrays.stream(characterVector)
                .map(Character::getGamepos)
                .min((a,b)-> (int)(gamepos.distance(a) - gamepos.distance(b))).get();

       float distance = gamepos.distance(cp);

        if (distance <= 40 && attack_timer <= 0) { //If the distance is less than 40
            System.out.println("attacked");
            attack(cp);
        }

        //Kevin, if we are done attacking remove shapes
        if(attack_shapes.size() > 0 && attack_timer <= 0){
            attack_shapes.stream().forEach(this::removeShape);
            attack_shapes.clear();
        }

        //Kevin, do movement stuff
        if (attack_timer <= 0) { //If the enemy is still moving then continue getting close to the player.
            if (goal == null){
                goal = curt.next;
            }

            //Kevin, if the distance to the next goal is less than how much we move setup next goal
            var vs = velocity.scale(delta);
            var gp = goal.gamepos;
            if(gamepos.distanceSquared(gp) < vs.lengthSquared() || gamepos.equals(gp)){
                gamepos = goal.gamepos;
                goal = curt.next;
                curdir = lib.dir_from_point_to_point(goal.gamepos, gamepos);
                System.out.println(curdir);
            }else {
                gamepos = gamepos.add(velocity.scale(delta));
            }
            //Kevin, setup next goal direction
            velocity = goal.gamepos.subtract(gamepos).unit().scale(0.05f);

        } else { //attack timer is  > 0 so decrement it
            attack_timer -= delta;
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
