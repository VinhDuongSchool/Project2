package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Tile extends Entity {

    //(Kevin) index of this in the tilemap
    public int gridx;
    public int gridy;
    public TileMap.TYPE type;
    public Tile next;
    public Room room;
    public Image curim;


    //Kevin, consider making a door class that extends tile

    public Vector gamepos;

    public Tile(final float x, final float y, Vector gp, Image img){
       super(x,y);
       curim = img;
       addImage(img);
       gamepos = gp;
       addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

    public Tile(float x, float y, Image img, TileMap.TYPE t, Room r){
        this(x, y, new Vector(x, y), img);
        this.type = t;
        this.room = r;
    }

}
