package bounce.common;

import bounce.client.ExplorerGameClient;
import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

import java.util.ArrayList;
import java.util.HashMap;


public class Warrior extends Character {
    private ArrayList<Shape> attack_shapes;

    public Warrior(final float x, final float y, final float vx, final float vy, Image img, long id) {
        super(x,y,vx,vy,img,id);
        health = 100;
        defense = 50;
        stamina = 100;
        magic = 0;
        attack = 50;
        speed = 25;
        attack_shapes = new ArrayList<>();
    }

    @Override
    public void primary(ArrayList<lib.DIRS> dirs, final lib.DIRS md) { //Primary attack
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

    @Override
    public void update(int delta) { //To check the countdown timer.
        super.update(delta);
        if (countdown > 0) {
            countdown -= delta;
            if (countdown <= 0) {
                removeImage(ExplorerGameClient.game_sprites.getSprite(0,11));
            }
        }

    }
}