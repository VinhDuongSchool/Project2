package bounce.common.entities;

import bounce.common.lib;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


public class Enemy extends Entity {

    //(Kevin) identification stuff
    private static final AtomicLong ID_COUNTER = new AtomicLong(0);
    public final long id;

    protected Vector velocity; //Velocity vectore.
    protected Vector gamepos;
    private int health;
    public float speed;
    public long attack_timer;
    public boolean attacking = false;
    public Vector dir;
    public ArrayList<Shape> attack_shapes;
    public lib.DIRS curdir;
    public int damage;

    public Enemy(final float x, final float y, final float vx, final float vy, Image img, long _id) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        health = 3;
        id = _id;
        damage = 5;
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

    public Optional<ArrayList<Projectile>> attack(Character c){
        throw new IllegalStateException("dont call base enemy attack");
    }

    public Optional<ArrayList<Projectile>> update(final int delta, Character[] characters, lib.DIRS td) {
        throw new IllegalStateException("dont call base enemy update");

        //Kevin, commented and not deleted for reference, yes i know git exists
        /*
        var c = Arrays.stream(characterVector)
                .min((a,b)-> (int)(gamepos.distance(a.getGamepos()) - gamepos.distance(b.getGamepos()))).get();
        float distance = gamepos.distance(c.getGamepos());

        if (distance <= 40 && attack_timer <= 0) { //If the distance is less than 40
            System.out.println("attacked");
            attack(c);
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

        return Optional.empty();

         */
    }

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
