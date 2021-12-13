package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.HashMap;


public class Character extends Entity {

    public Vector velocity; //Velocity vectore.
    public Vector gamepos;
    public final long client_id;
    public long attack_timer;
    private ArrayList<Shape> attack_shapes;
    public lib.DIRS curdir;
    public int health;
    public int defense;
    public int stamina;
    public int magic;
    public int attack;
    public int speed;
    public int countdown;


    public Character(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y);
        gamepos = new Vector(x,y);
        velocity = new Vector(vx, vy);
        client_id = id;

        // add image with offset to it renders from top left corner
        addImage(img);
        addShape(new ConvexPolygon(lib.sqr.getPoints()));
        attack_shapes = new ArrayList<>();
    }

    public Character(Vector pos, Vector vel, Image img, long id){
        this(pos.getX(), pos.getY(), vel.getX(), vel.getY(), img, id);
    }


    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity


    public void primary(ArrayList<lib.DIRS> dirs, final lib.DIRS d){

    }

    public void update(final int delta) {
        attack_timer -= delta;
        //Kevin, update the attack timer and if its 0 remove the attack shapes from the entity
        if (attack_timer <= 0){
            attack_shapes.stream().forEach(this::removeShape);
            attack_shapes.clear();
        }
        gamepos = gamepos.add(velocity.scale(delta));
        setPosition(gamepos);
    } //Update base off of the velocity

    public long getClient_id() {
        return client_id;
    }

    public void setGamepos(Vector gamepos) {
        this.gamepos = gamepos;
        setPosition(gamepos);
    }

    public Vector getGamepos() {
        return gamepos;
    }
}


