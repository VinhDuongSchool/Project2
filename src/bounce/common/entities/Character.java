package bounce.common.entities;

import bounce.common.lib;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Optional;


public abstract class Character extends Entity {

    protected Vector velocity; //Velocity vectore.
    protected Vector gamepos;
    public final long client_id;
    public int attack_timer;
    public ArrayList<Shape> attack_shapes;
    protected lib.DIRS curdir;
    public int health;
    public int defense;
    public int stamina;
    public int magic;
    public int attack;
    protected float speed;
    public int countdown;
    public int lookingDirIdx;
    public boolean doingAttackAnim;
    public boolean dead = false;

    public boolean hit_in_this_attack;

    public Character(final float x, final float y, final float vx, final float vy, long id) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        client_id = id;

        // add image with offset to it renders from top left corner
        //addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        attack_shapes = new ArrayList<>();
    }


    //Kevin, make a character give a class
    public static Character dyn(Class<? extends Character> ct, Vector gp, Vector v, long id){
        try {
            //this works because characters only have a single constructor
            return (Character) ct.getConstructors()[0].newInstance(gp,v,id);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e){
            e.printStackTrace();
            throw new IllegalArgumentException("character initialization failed");
        }
    }

    public Character(Vector pos, Vector vel, long id){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(), id);
    }

    //Kevin, set current dir and update velocity accordingly
    //null dir means no keys are pressed so no movement
    public void setCurdir(lib.DIRS curdir) {
        this.curdir = curdir;
        velocity = (curdir == null) ? lib.v0 : lib.dir_enum_to_unit_vector(curdir).scale(speed);
    }

    public lib.DIRS getCurdir() {
        return curdir;
    }

    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity


    public abstract Optional<ArrayList<Projectile>> primary();
    public abstract void doAnim();

    public void update(final int delta) {

        if(attack_timer > 0) {
            attack_timer -= delta;
        } else {
            gamepos = gamepos.add(velocity.scale(delta));
        }

        setPosition(gamepos);
    }

    public void setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        setPosition(gamepos);
    }

    public Vector getGamepos() {
        return gamepos;
    }

    public void dieScene() {

    }
}


