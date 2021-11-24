package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Shape;
import jig.Vector;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.geom.Polygon;
import org.newdawn.slick.geom.Transform;

import javax.crypto.EncryptedPrivateKeyInfo;


public class Character extends Entity {

    private Vector velocity; //Velocity vectore.
    public Vector gamepos;
    public final int client_id;
    public Entity CurrentMelee;


    public Character(final float x, final float y, final float vx, final float vy, Image img, int id) {
        super(x,y);
        gamepos = new Vector(32,32);
        velocity = new Vector(vx, vy);
        client_id = id;

        // add image with offset to it renders from top left corner
        addImage(img);

        Vector[] one = new Vector[]{
           new Vector(- 32.0f, 0.0f),
                    new Vector(0.0f,  16.0f),
                   new Vector( 32.0f, 0.0f),
                    new Vector(0.0f, - 16.0f)
        };
        ConvexPolygon test = new ConvexPolygon(one);
        addShape(test,new Vector(0.0f, 32.0f));

    }



    public void setVelocity(final Vector v) {
        velocity = v;
    } //Set the velocity.

    public Vector getVelocity() {
        return velocity;
    } //Get the velocity


    public void playermelee(int dir){//todo figure out the area that the  weapon of choice
        /**
         * looking at the grid we can asume that if this is a wariior
         */
        switch (dir){
            case 1: Vector [] up = new Vector[]{
                    new Vector(48.0f,-16.0f),
                    new Vector(48.0f, -48.0f),
                    new Vector(-48.0f, -48.0f),
                    new Vector(-48.0f, -16.0f)
                };
                addShape(new ConvexPolygon(up),Color.transparent,Color.green);
                break;
            case 2: Vector [] topright = new Vector[]{
                    new Vector(-16.0f,-16.0f),
                    new Vector(-16.0f, -48.0f),

                    new Vector(48.0f, -48.0f),
                    new Vector(48.0f, 16.0f),

                    new Vector(16.0f, 16.0f),
                    new Vector(16.0f, -16.0f)
                };
                addShape(new ConvexPolygon(topright),Color.transparent, Color.blue);
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
        gamepos = gamepos.add(velocity.scale(delta));
    } //Update base off of the velocity

    public int getClient_id() {
        return client_id;
    }
}
