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
    public Entity testentitiy;


    public Character(final float x, final float y, final float vx, final float vy, Image img, int id) {
        super(x,y);
        gamepos = new Vector(32,32);
        velocity = new Vector(vx, vy);
        client_id = id;

        // add image with offset to it renders from top left corner
        addImageWithBoundingBox(img);


        testentitiy = new Entity(x,y);



        Vector[] one = new Vector[]{
           new Vector(- 32.0f, 0.0f),
                    new Vector(0.0f,  16.0f),
                   new Vector( 32.0f, 0.0f),
                    new Vector(0.0f, - 16.0f)
        };
        ConvexPolygon test = new ConvexPolygon(one);
        testentitiy.addShape(test,new Vector(0.0f, 32.0f),Color.transparent, Color.red);

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
        ;

    }




    public void update(final int delta) {
        gamepos = gamepos.add(velocity.scale(delta));
    } //Update base off of the velocity

    public int getClient_id() {
        return client_id;
    }
}
