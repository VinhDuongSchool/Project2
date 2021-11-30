package bounce.common;

import jig.ConvexPolygon;
import jig.Entity;
import jig.Vector;
import org.newdawn.slick.Image;


public class Tile extends Entity {

    //(Kevin) index of this in the tilemap
    public int gridx;
    public int gridy;
    public String tileType;
    public TileMap.TYPE type;
    public Tile next;

    public Vector gamepos;

    public Tile(final float x, final float y, Vector gp, Image img, String tp){
       super(x,y);
       addImage(img);
       gamepos = gp;
       tileType = tp;
       addShape(new ConvexPolygon(lib.sqr.getPoints()));
    }

}
