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

    private Vector velocity; //Velocity vectore.
    private Vector gamepos;
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


    public void primary(ArrayList<lib.DIRS> dirs){
        /**
         * looking at the grid we can asume that if this is a wariior
         */

        var offsetdirs = new HashMap<lib.DIRS, Vector>(){{
            put(lib.DIRS.NORTH, new Vector(32, 0));
            put(lib.DIRS.WEST, new Vector(0,-32));
            put(lib.DIRS.EAST, new Vector(0,32 ));
            put(lib.DIRS.SOUTH, new Vector(-32,0));
            put(lib.DIRS.NORTHWEST, new Vector(32,-32));
            put(lib.DIRS.NORTHEAST, new Vector(32,32));
            put(lib.DIRS.SOUTHWEST, new Vector(-32,-32));
            put(lib.DIRS.SOUTHEAST, new Vector(-32,32));
        }};

        //Kevin, set attack timer, for each dir in the list create a new shape,
        //keep a reference to the shape so we can delete it later,
        //add the shape to the entity with the specific offset for the dir it should be in
        attack_timer = 1000;
        for(var d : dirs){
            var s = new ConvexPolygon(lib.sqr.getPoints());
            attack_shapes.add(s);
            addShape(s, offsetdirs.get(d));
        }

        var dir = 0;
        switch (dir){
            case 0:
                break;
            case 1: Vector [] up = new Vector[]{
                    new Vector(48.0f,-16.0f),
                    new Vector(48.0f, -48.0f),
                    new Vector(-48.0f, -48.0f),
                    new Vector(-48.0f, -16.0f)
                };
                addShape(new ConvexPolygon(up),Color.transparent,Color.green);
                break;
            case 2:
                Vector [] topright = new Vector[]{
                    new Vector(-16.0f,-16.0f),
                    new Vector(-16.0f, -48.0f),

                    new Vector(48.0f, -48.0f),
                    new Vector(48.0f, 16.0f),

                    new Vector(16.0f, 16.0f),
                    new Vector(16.0f, -16.0f)
                };
//                addShape(new ConvexPolygon(topright),Color.transparent, Color.blue);
                addShape(new ConvexPolygon(lib.sqr.getPoints()), new Vector(0,-32));
                break;
            case 3: Vector [] right = new Vector[]{
                    new Vector(16.0f,48.0f),
                    new Vector(48.0f, 48.0f),
                    new Vector(48.0f, -48.0f),
                    new Vector(16.0f, -48.0f)

            };
                addShape(new ConvexPolygon(right), Color.transparent, Color.green);
                break;
            case 4: Vector[] rightbottom = new Vector[]{
                    new Vector(16.0f, -16.0f),
                    new Vector(48.0f,-16.0f),
                    new Vector(48.0f, 48.0f),
                    new Vector(-16.0f, 48.0f),
                    new Vector(-16.0f, 16.0f),
                    new Vector(16.0f, 16.0f)
            };
                addShape(new ConvexPolygon(rightbottom), Color.transparent, Color.green);
                break;
            case 5:Vector [] down = new Vector[]{
                    new Vector(48.0f,16.0f),
                    new Vector(48.0f, 48.0f),
                    new Vector(-48.0f, 48.0f),
                    new Vector(-48.0f, 16.0f)
            };
                addShape(new ConvexPolygon(down),Color.transparent,Color.green);
                break;
            case 6: Vector[] leftbottom = new Vector[]{
                    new Vector(-16.0f, -16.0f),
                    new Vector(-48.0f,-16.0f),
                    new Vector(-48.0f, 48.0f),
                    new Vector(16.0f, 48.0f),
                    new Vector(16.0f, 16.0f),
                    new Vector(-16.0f, 16.0f)
            };
                addShape(new ConvexPolygon(leftbottom), Color.transparent, Color.green);
                break;
            case 7: Vector[] left = new Vector[]{
                    new Vector(-16.0f, 48.0f),
                    new Vector(-48.0f, 48.0f),
                    new Vector(-48.0f, -48.0f),
                    new Vector(-16.0f, -48.0f)
            };
                addShape(new ConvexPolygon(left), Color.transparent, Color.green);
                break;
            case 8: Vector [] topleft = new Vector[]{
                    new Vector(16.0f,-16.0f),
                    new Vector(16.0f, -48.0f),

                    new Vector(-48.0f, -48.0f),
                    new Vector(-48.0f, 16.0f),

                    new Vector(-16.0f, 16.0f),
                    new Vector(-16.0f, -16.0f)
            };
                addShape(new ConvexPolygon(topleft),Color.transparent, Color.blue);;
                break;

        }

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


